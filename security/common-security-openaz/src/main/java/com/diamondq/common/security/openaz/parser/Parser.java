package com.diamondq.common.security.openaz.parser;

import com.diamondq.common.security.xacml.model.Conditional;
import com.diamondq.common.security.xacml.model.ConditionalCode;
import com.diamondq.common.security.xacml.model.IFunctionArgument;
import com.diamondq.common.security.xacml.model.ILiteralBag;
import com.diamondq.common.security.xacml.model.ILiteralSingle;
import com.diamondq.common.security.xacml.model.IResource;
import com.diamondq.common.security.xacml.model.LiteralBag;
import com.diamondq.common.security.xacml.model.LiteralSingle;
import com.diamondq.common.security.xacml.model.Operation;
import com.diamondq.common.security.xacml.model.OperationCode;
import com.diamondq.common.security.xacml.model.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestFactory;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pdp.ScopeResolver;
import org.apache.openaz.xacml.pdp.OpenAZPDPEngine;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationContextFactory;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.policy.AllOf;
import org.apache.openaz.xacml.pdp.policy.AnyOf;
import org.apache.openaz.xacml.pdp.policy.Bag;
import org.apache.openaz.xacml.pdp.policy.Condition;
import org.apache.openaz.xacml.pdp.policy.Expression;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.Match;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.PolicyDef;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.pdp.policy.PolicyFinderResult;
import org.apache.openaz.xacml.pdp.policy.PolicyIdReference;
import org.apache.openaz.xacml.pdp.policy.PolicySet;
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;
import org.apache.openaz.xacml.pdp.policy.PolicySetIdReference;
import org.apache.openaz.xacml.pdp.policy.Rule;
import org.apache.openaz.xacml.pdp.policy.Target;
import org.apache.openaz.xacml.pdp.policy.expressions.Apply;
import org.apache.openaz.xacml.pdp.policy.expressions.AttributeDesignator;
import org.apache.openaz.xacml.std.StdIndividualDecisionRequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Parser {

	private static final Logger				sLogger	= LoggerFactory.getLogger(Parser.class);

	private final OpenAZPDPEngine			mPDPEngine;

	private final EvaluationContextFactory	mEvalContextFactory;

	private final ScopeResolver				mScopeResolver;

	private final PepRequestFactory			mPEPRequestFactory;

	private final Decision					mDefaultDecision;

	private final Method					mPolicySetEnsureReferenceeMethod;

	private final Method					mPolicyEnsureReferenceeMethod;

	public Parser(PepAgent pAgent) {
		try {
			Method method = pAgent.getClass().getMethod("getPdpEngine");
			method.setAccessible(true);
			mPDPEngine = (OpenAZPDPEngine) method.invoke(pAgent);
			Field field = mPDPEngine.getClass().getDeclaredField("evaluationContextFactory");
			field.setAccessible(true);
			mEvalContextFactory = (EvaluationContextFactory) field.get(mPDPEngine);
			field = mPDPEngine.getClass().getDeclaredField("scopeResolver");
			field.setAccessible(true);
			mScopeResolver = (ScopeResolver) field.get(mPDPEngine);
			field = mPDPEngine.getClass().getDeclaredField("defaultDecision");
			field.setAccessible(true);
			mDefaultDecision = (Decision) field.get(mPDPEngine);
			field = pAgent.getClass().getDeclaredField("pepRequestFactory");
			field.setAccessible(true);
			mPEPRequestFactory = (PepRequestFactory) field.get(pAgent);

			mPolicySetEnsureReferenceeMethod =
				PolicySetIdReference.class.getDeclaredMethod("ensureReferencee", EvaluationContext.class);
			mPolicySetEnsureReferenceeMethod.setAccessible(true);
			mPolicyEnsureReferenceeMethod =
				PolicyIdReference.class.getDeclaredMethod("ensureReferencee", EvaluationContext.class);
			mPolicyEnsureReferenceeMethod.setAccessible(true);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}

	public IFunctionArgument generateParseTree(List<?> pAssociations, Object... pObjects) {

		/* Build a request */

		PepRequest pepRequest = mPEPRequestFactory.newBulkPepRequest(pAssociations, pObjects);
		Request request = pepRequest.getWrappedRequest();

		/* Split the original request up into individual decision requests */

		StdIndividualDecisionRequestGenerator stdIndividualDecisionRequestGenerator =
			new StdIndividualDecisionRequestGenerator(mScopeResolver, request);

		Iterator<Request> iterRequestsIndividualDecision =
			stdIndividualDecisionRequestGenerator.getIndividualDecisionRequests();
		if (iterRequestsIndividualDecision == null || !iterRequestsIndividualDecision.hasNext())
			throw new RuntimeException("");

		Set<IFunctionArgument> policySet = new HashSet<>();
		while (iterRequestsIndividualDecision.hasNext()) {
			Request requestIndividualDecision = iterRequestsIndividualDecision.next();

			EvaluationContext evaluationContext = mEvalContextFactory.getEvaluationContext(requestIndividualDecision);
			if (evaluationContext == null)
				throw new RuntimeException("");

			PolicyFinderResult<PolicyDef> policyFinderResult = evaluationContext.getRootPolicyDef();
			if (policyFinderResult.getStatus() != null && !policyFinderResult.getStatus().isOk())
				throw new RuntimeException("");

			PolicyDef policyDefRoot = policyFinderResult.getPolicyDef();
			IFunctionArgument childResult;
			if (policyDefRoot == null) {
				switch (mDefaultDecision) {
				case NOTAPPLICABLE:
				case DENY:
					childResult = ShortCircuit.FALSE.getLiteral();
					break;
				case PERMIT:
					childResult = ShortCircuit.TRUE.getLiteral();
					break;
				case INDETERMINATE:
				case INDETERMINATE_DENY:
				case INDETERMINATE_DENYPERMIT:
				case INDETERMINATE_PERMIT:
				default:
					throw new RuntimeException("");
				}
			}
			else {

				if (policyDefRoot instanceof PolicySet)
					childResult = handlePolicySet((PolicySet) policyDefRoot, evaluationContext);
				else if (policyDefRoot instanceof Policy)
					childResult = handlePolicy((Policy) policyDefRoot, evaluationContext);
				else
					throw new UnsupportedOperationException();
			}

			ShortCircuit rsc = shortcut(childResult);

			/* If a policy is false, then it will always fail, so all the other policies don't count */

			if (rsc == ShortCircuit.FALSE)
				return rsc.getLiteral();

			/* If a policy is true, then it can be skipped, since it doesn't matter */

			if (rsc == ShortCircuit.TRUE)
				continue;

			/* Otherwise, the child is AND'd to the other children */

			policySet.add(childResult);

		}

		if (policySet.isEmpty() == true)
			return ShortCircuit.FALSE.getLiteral();
		if (policySet.size() == 1)
			return policySet.iterator().next();
		return Conditional.builder().code(ConditionalCode.AND).addAllOperations(policySet).build();
	}

	private IFunctionArgument handleTarget(Target pTarget, EvaluationContext pEvaluationContext) {
		if (pTarget != null) {
			IFunctionArgument firstAny = null;
			Conditional.Builder anyBuilder = null;
			Iterator<AnyOf> anyOfs = pTarget.getAnyOfs();
			if (anyOfs != null)
				while (anyOfs.hasNext()) {
					AnyOf anyOf = anyOfs.next();
					IFunctionArgument firstAll = null;
					Conditional.Builder allBuilder = null;
					Iterator<AllOf> allOfs = anyOf.getAllOfs();
					if (allOfs != null)
						while (allOfs.hasNext()) {
							AllOf allOf = allOfs.next();
							IFunctionArgument firstMatch = null;
							Conditional.Builder matchBuilder = null;
							Iterator<Match> matches = allOf.getMatches();
							if (matches != null)
								while (matches.hasNext()) {
									Match match = matches.next();
									IFunctionArgument matchResult = handleMatch(match, pEvaluationContext);

									ShortCircuit matchSC = shortcut(matchResult);

									/* If a match is true, then it can be skipped */

									if (matchSC == ShortCircuit.TRUE)
										continue;

									/*
									 * If a match is false, then it's never going to match, so all other matches don't
									 * count
									 */

									if (matchSC == ShortCircuit.FALSE) {
										firstMatch = matchSC.getLiteral();
										matchBuilder = null;
										break;
									}

									/* Otherwise, the child is AND'd to the other children */

									if (firstMatch == null)
										firstMatch = matchResult;
									else {
										if (matchBuilder == null)
											matchBuilder = Conditional.builder().code(ConditionalCode.AND)
												.addOperation(firstMatch);
										matchBuilder.addOperation(matchResult);
									}
								}

							IFunctionArgument allOfResult = matchBuilder == null
								? (firstMatch == null ? ShortCircuit.TRUE.getLiteral() : firstMatch)
								: matchBuilder.build();

							ShortCircuit allSC = shortcut(allOfResult);

							/* If a match is false, then it can be skipped */

							if (allSC == ShortCircuit.FALSE)
								continue;

							/*
							 * If a match is true, then it's always going to match, so all other matches don't count
							 */

							if (allSC == ShortCircuit.TRUE) {
								firstAll = allSC.getLiteral();
								allBuilder = null;
								break;
							}

							/* Otherwise, the child is OR'd to the other children */

							if (firstAll == null)
								firstAll = allOfResult;
							else {
								if (allBuilder == null)
									allBuilder = Conditional.builder().code(ConditionalCode.OR).addOperation(firstAll);
								allBuilder.addOperation(allOfResult);
							}
						}

					IFunctionArgument anyOfResult = allBuilder == null
						? (firstAll == null ? ShortCircuit.FALSE.getLiteral() : firstAll) : allBuilder.build();
					ShortCircuit anySC = shortcut(anyOfResult);

					/* If a match is true, then it can be skipped */

					if (anySC == ShortCircuit.TRUE)
						continue;

					/*
					 * If a match is false, then it's never going to match, so all other matches don't count
					 */

					if (anySC == ShortCircuit.FALSE) {
						firstAny = anySC.getLiteral();
						anyBuilder = null;
						break;
					}

					/* Otherwise, the child is AND'd to the other children */

					if (firstAny == null)
						firstAny = anyOfResult;
					else {
						if (anyBuilder == null)
							anyBuilder = Conditional.builder().code(ConditionalCode.AND).addOperation(firstAny);
						anyBuilder.addOperation(anyOfResult);
					}
				}

			IFunctionArgument targetResult = anyBuilder == null
				? (firstAny == null ? ShortCircuit.TRUE.getLiteral() : firstAny) : anyBuilder.build();

			return targetResult;
		}
		else
			return ShortCircuit.TRUE.getLiteral();
	}

	private IFunctionArgument handleMatch(Match pMatch, EvaluationContext pEvaluationContext) {
		try {
			MatchResult result = pMatch.match(pEvaluationContext);
			switch (result.getMatchCode()) {
			case MATCH:
				return ShortCircuit.TRUE.getLiteral();
			case NOMATCH:
				return ShortCircuit.FALSE.getLiteral();
			case INDETERMINATE:
			default:
				throw new UnsupportedOperationException();
			}
		}
		catch (EvaluationException ex) {
			throw new RuntimeException(ex);
		}
	}

	private IFunctionArgument handlePolicySet(PolicySet pSet, EvaluationContext pEvaluationContext) {

		sLogger.debug("Handling policy set {}...", pSet.getIdentifier());

		/* Get the target */

		Target target = pSet.getTarget();

		IFunctionArgument targetResult = handleTarget(target, pEvaluationContext);
		ShortCircuit tsc = shortcut(targetResult);

		if (tsc == ShortCircuit.FALSE)
			return tsc.getLiteral();

		/*
		 * Assuming that this policy set is usable, then we need to look at the combining elements (ie. children Policy,
		 * PolicySet, etc.)
		 */

		Set<IFunctionArgument> childSet = new HashSet<>();
		boolean trueMatch = false;

		Iterator<PolicySetChild> iterPolicies = pSet.getChildren();
		if (iterPolicies != null) {
			while (iterPolicies.hasNext()) {
				IFunctionArgument childResult;
				PolicySetChild policySetChild = iterPolicies.next();
				if (policySetChild instanceof PolicySet)
					childResult = handlePolicySet((PolicySet) policySetChild, pEvaluationContext);
				else if (policySetChild instanceof Policy)
					childResult = handlePolicy((Policy) policySetChild, pEvaluationContext);
				else if (policySetChild instanceof PolicySetIdReference) {

					try {
						PolicySet child =
							(PolicySet) mPolicySetEnsureReferenceeMethod.invoke(policySetChild, pEvaluationContext);
						childResult = handlePolicySet(child, pEvaluationContext);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						throw new RuntimeException(ex);
					}

				}
				else if (policySetChild instanceof PolicyIdReference) {

					try {
						Policy child =
							(Policy) mPolicyEnsureReferenceeMethod.invoke(policySetChild, pEvaluationContext);
						childResult = handlePolicy(child, pEvaluationContext);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						throw new RuntimeException(ex);
					}

				}
				else
					throw new UnsupportedOperationException();

				ShortCircuit rsc = shortcut(childResult);

				/* If a rule is false, then it can be skipped */

				if (rsc == ShortCircuit.FALSE)
					continue;

				/* If a rule is true, then it's always going to match, so all other rules don't count */

				if (rsc == ShortCircuit.TRUE) {
					childSet.clear();
					trueMatch = true;
					break;
				}

				/* Otherwise, the child is OR'd to the other children */

				childSet.add(childResult);

			}
		}

		/* Now merge the target and rules together */

		IFunctionArgument childResults;
		if (childSet.isEmpty() == true) {
			if (trueMatch == true)
				childResults = null;
			else
				return ShortCircuit.FALSE.getLiteral();
		}
		else if (childSet.size() == 1)
			childResults = childSet.iterator().next();
		else
			childResults = Conditional.builder().code(ConditionalCode.OR).addAllOperations(childSet).build();

		if (tsc == ShortCircuit.TRUE)
			return (childResults == null ? ShortCircuit.TRUE.getLiteral() : childResults);

		if (childResults == null)
			return targetResult;
		return Conditional.builder().code(ConditionalCode.AND).addOperation(targetResult).addOperation(childResults)
			.build();
	}

	private IFunctionArgument handlePolicy(Policy pPolicy, EvaluationContext pEvaluationContext) {

		sLogger.debug("Handling policy {}...", pPolicy.getIdentifier());

		/* Get the target */

		Target target = pPolicy.getTarget();

		IFunctionArgument targetResult = handleTarget(target, pEvaluationContext);
		ShortCircuit tsc = shortcut(targetResult);
		if (tsc == ShortCircuit.FALSE)
			return tsc.getLiteral();

		IFunctionArgument firstRule = null;
		Conditional.Builder orRule = null;
		Iterator<Rule> rules = pPolicy.getRules();
		if (rules != null)
			while (rules.hasNext()) {
				Rule rule = rules.next();

				IFunctionArgument ruleResult = handleRule(rule, pEvaluationContext);
				ShortCircuit rsc = shortcut(ruleResult);

				/* If a rule is false, then it can be skipped */

				if (rsc == ShortCircuit.FALSE)
					continue;

				/* If a rule is true, then it's always going to match, so all other rules don't count */

				if (rsc == ShortCircuit.TRUE) {
					firstRule = rsc.getLiteral();
					orRule = null;
					break;
				}

				/* Otherwise, the rule is OR'd to the other rules */

				if (firstRule == null)
					firstRule = ruleResult;
				else {
					if (orRule == null)
						orRule = Conditional.builder().code(ConditionalCode.OR).addOperation(firstRule);
					orRule.addOperation(ruleResult);
				}
			}

		/* Now merge the target and rules together */

		IFunctionArgument ruleResults =
			orRule == null ? (firstRule == null ? ShortCircuit.FALSE.getLiteral() : firstRule) : orRule.build();
		if (tsc == ShortCircuit.TRUE)
			return ruleResults;

		return Conditional.builder().code(ConditionalCode.AND).addOperation(targetResult).addOperation(ruleResults)
			.build();
	}

	private IFunctionArgument handleRule(Rule pRule, EvaluationContext pEvaluationContext) {

		sLogger.debug("Handling rule {}...", pRule.getRuleId());

		Target target = pRule.getTarget();

		IFunctionArgument targetResult = handleTarget(target, pEvaluationContext);
		ShortCircuit tsc = shortcut(targetResult);
		if (tsc != ShortCircuit.NA)
			targetResult = null;

		if (tsc == ShortCircuit.FALSE)
			return tsc.getLiteral();

		Condition condition = pRule.getCondition();

		if (condition != null) {
			Expression expression = condition.getExpression();
			if (expression != null)
				try {
					IFunctionArgument arg = handleExpression(expression, pEvaluationContext, null);
					ShortCircuit sc = shortcut(arg);
					if (sc == ShortCircuit.TRUE)
						return targetResult == null ? sc.getLiteral() : targetResult;
					else if (sc == ShortCircuit.FALSE)
						return sc.getLiteral();
					else {
						if (targetResult == null)
							return arg;
						else
							return Conditional.builder().code(ConditionalCode.AND).addOperation(targetResult)
								.addOperation(arg).build();
					}

				}
				catch (EvaluationException ex) {
					throw new RuntimeException(ex);
				}
		}

		return targetResult == null ? tsc.getLiteral() : targetResult;
	}

	private IFunctionArgument handleExpression(Expression pExpression, EvaluationContext pEvaluationContext,
		PolicyDefaults pPolicyDefaults) throws EvaluationException {

		if (pExpression instanceof Apply)
			return handleApply((Apply) pExpression, pEvaluationContext, pPolicyDefaults);
		else if (pExpression instanceof AttributeDesignator)
			return handleDesignator((AttributeDesignator) pExpression, pEvaluationContext);
		else
			throw new UnsupportedOperationException();
	}

	private IFunctionArgument handleDesignator(AttributeDesignator pExpression, EvaluationContext pEvaluationContext) {
		try {

			/* Only resource category attributes translate into IResource */

			boolean isResource = pExpression.getCategory().equals(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
			Identifier attributeId = pExpression.getAttributeId();
			if (isResource == true) {
				if (attributeId.equals(XACML3.ID_RESOURCE_RESOURCE_ID) == false) {

					/* Resource specific attribute. */

					Resource resource = Resource.builder().resourceId(attributeId.stringValue()).build();
					return resource;

				}
			}

			/* Everything else becomes a literal */

			ExpressionResult result = pExpression.evaluate(pEvaluationContext, null);
			Identifier dataTypeId = pExpression.getDataTypeId();
			return convertExpressionResultToLiteral(result, dataTypeId.equals(XACML.ID_DATATYPE_BOOLEAN));
		}
		catch (EvaluationException ex) {
			throw new RuntimeException(ex);
		}

	}

	private ShortCircuit shortcut(IFunctionArgument arg) {
		if (arg instanceof ILiteralSingle) {
			ILiteralSingle ls = (ILiteralSingle) arg;
			if (ls.isBooleanValue() == true) {
				Boolean bool = Boolean.valueOf(ls.getSingleValue());

				/*
				 * If a literal evaluates to true in an AND, then it can be ignored, since it has no impact
				 */

				if (Boolean.TRUE == bool)
					return ShortCircuit.TRUE;

				/*
				 * If a literal evaluates to false in an AND, then the entire AND becomes false, regardless of whether
				 * there are other types
				 */

				if (Boolean.FALSE == bool)
					return ShortCircuit.FALSE;
			}
		}
		return ShortCircuit.NA;
	}

	private IFunctionArgument convertExpressionResultToLiteral(ExpressionResult result, boolean pBooleanValue) {
		if (result == null)
			throw new UnsupportedOperationException();
		if (result.isBag() == true) {

			Bag bag = result.getBag();
			if (bag == null)
				throw new UnsupportedOperationException();
			List<String> values = new ArrayList<>();
			for (AttributeValue<?> av : bag.getAttributeValueList()) {
				Object valueObject = av.getValue();
				if (valueObject == null)
					throw new UnsupportedOperationException();
				values.add(valueObject.toString());
			}

			LiteralBag literalBag = LiteralBag.builder().value(values).booleanValue(pBooleanValue).build();
			return literalBag;
		}
		else {
			AttributeValue<?> attributeValue = result.getValue();
			if (attributeValue == null)
				throw new UnsupportedOperationException();
			Object valueObject = attributeValue.getValue();
			if (valueObject == null)
				throw new UnsupportedOperationException();
			LiteralSingle literal =
				LiteralSingle.builder().value(valueObject.toString()).booleanValue(pBooleanValue).build();
			return literal;
		}
	}

	private IFunctionArgument handleApply(Apply pExpression, EvaluationContext pEvaluationContext,
		PolicyDefaults pPolicyDefaults) throws EvaluationException {
		Identifier functionId = pExpression.getFunctionId();

		String functionIdStr = functionId.stringValue();
		int offset = functionIdStr.lastIndexOf(':');
		String functionName;
		if (offset == -1)
			functionName = functionIdStr;
		else
			functionName = functionIdStr.substring(offset + 1);
		functionName = functionName.replace('-', '_').toUpperCase();

		OperationCode code = OperationCode.valueOf(functionName);

		switch (code) {
		case AND: {
			IFunctionArgument firstArg = null;
			Conditional.Builder builder = null;
			Iterator<Expression> arguments = pExpression.getArguments();
			if (arguments != null)
				while (arguments.hasNext()) {
					Expression expression = arguments.next();
					IFunctionArgument arg = handleExpression(expression, pEvaluationContext, pPolicyDefaults);
					ShortCircuit sc = shortcut(arg);
					if (sc == ShortCircuit.TRUE)
						continue;
					if (sc == ShortCircuit.FALSE)
						return sc.getLiteral();
					if (firstArg == null)
						firstArg = arg;
					else {
						if (builder == null)
							builder = Conditional.builder().code(ConditionalCode.AND).addOperation(firstArg);
						builder.addOperation(arg);
					}
				}

			return builder == null ? (firstArg == null ? ShortCircuit.TRUE.getLiteral() : firstArg) : builder.build();
		}
		case STRING_ONE_AND_ONLY: {
			Iterator<Expression> arguments = pExpression.getArguments();
			if (arguments != null)
				while (arguments.hasNext()) {
					IFunctionArgument r = handleExpression(arguments.next(), pEvaluationContext, pPolicyDefaults);
					if (r instanceof ILiteralBag)
						return LiteralSingle.builder().value(((ILiteralBag) r).getSingleValue())
							.booleanValue(((ILiteralBag) r).isBooleanValue()).build();
					else if (r instanceof ILiteralSingle)
						return r;
					else if (r instanceof IResource)
						return r;
					else
						throw new UnsupportedOperationException();
				}
			throw new UnsupportedOperationException();
		}
		default: {
			Iterator<Expression> arguments = pExpression.getArguments();
			List<IFunctionArgument> argResults = new ArrayList<>();
			if (arguments != null)
				while (arguments.hasNext()) {
					Expression expression = arguments.next();
					argResults.add(handleExpression(expression, pEvaluationContext, pPolicyDefaults));
				}

			int argCount = code.getArgCount();
			if ((argCount != -1) && (argCount != argResults.size()))
				throw new UnsupportedOperationException();

			/* If all the arguments are literals, then evaluate the function */

			boolean isLiteral = true;
			for (IFunctionArgument ar : argResults)
				if ((ar instanceof ILiteralSingle == false) && (ar instanceof ILiteralBag == false)) {
					isLiteral = false;
					break;
				}

			if (isLiteral == true) {
				ExpressionResult result = pExpression.evaluate(pEvaluationContext, pPolicyDefaults);
				return convertExpressionResultToLiteral(result, code.isBooleanReturn());
			}

			/* Generate an operation */

			return Operation.builder().operation(code).addAllArguments(argResults).build();
		}
		}
	}

}

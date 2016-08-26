package com.diamondq.common.security.openaz;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acl.api.AuthenticationQueryExtender;

import java.awt.Component;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jdo.JDOQLTypedQuery;
import javax.jdo.query.BooleanExpression;
import javax.jdo.query.ComparableExpression;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestFactory;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Request;
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
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthQueryExtenderImpl implements AuthenticationQueryExtender {

	private static final Logger				sLogger	= LoggerFactory.getLogger(AuthQueryExtenderImpl.class);

	private final OpenAZPDPEngine			mPDPEngine;

	private final EvaluationContextFactory	mEvalContextFactory;

	private final ScopeResolver				mScopeResolver;

	private final PepRequestFactory			mPEPRequestFactory;

	private final Decision					mDefaultDecision;

	private final Code						mCode;

	private final Method					mPolicySetEnsureReferenceeMethod;

	private final Method					mPolicyEnsureReferenceeMethod;

	@Inject
	public AuthQueryExtenderImpl(PepAgent pAgent, Config pConfig) {
		String fqdn = pConfig.bind("application.fqdn", String.class);
		mCode = new Code(fqdn);
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

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationQueryExtender#extendForAccessControl(javax.jdo.JDOQLTypedQuery,
	 *      javax.jdo.query.BooleanExpression, java.util.List, java.lang.Object[])
	 */
	@Override
	public BooleanExpression extendForAccessControl(JDOQLTypedQuery<?> pTypedQuery, BooleanExpression pExpression,
		List<?> pAssociations, Object... pObjects) {

		Object[] expand = new Object[(pObjects != null ? pObjects.length + 1 : 1)];
		if (pObjects != null)
			System.arraycopy(pObjects, 0, expand, 1, pObjects.length);
		expand[0] = mCode;

		/* Build a request */

		PepRequest pepRequest = mPEPRequestFactory.newBulkPepRequest(pAssociations, expand);
		Request request = pepRequest.getWrappedRequest();

		/* Split the original request up into individual decision requests */

		StdIndividualDecisionRequestGenerator stdIndividualDecisionRequestGenerator =
			new StdIndividualDecisionRequestGenerator(mScopeResolver, request);

		Iterator<Request> iterRequestsIndividualDecision =
			stdIndividualDecisionRequestGenerator.getIndividualDecisionRequests();
		if (iterRequestsIndividualDecision == null || !iterRequestsIndividualDecision.hasNext())
			throw new RuntimeException("");

		BooleanExpression expandedExpression = null;
		while (iterRequestsIndividualDecision.hasNext()) {
			Request requestIndividualDecision = iterRequestsIndividualDecision.next();

			EvaluationContext evaluationContext = mEvalContextFactory.getEvaluationContext(requestIndividualDecision);
			if (evaluationContext == null)
				throw new RuntimeException("");

			PolicyFinderResult<PolicyDef> policyFinderResult = evaluationContext.getRootPolicyDef();
			if (policyFinderResult.getStatus() != null && !policyFinderResult.getStatus().isOk())
				throw new RuntimeException("");

			PolicyDef policyDefRoot = policyFinderResult.getPolicyDef();
			if (policyDefRoot == null) {
				switch (mDefaultDecision) {
				case NOTAPPLICABLE:
				case DENY: {
					BooleanExpression e = pTypedQuery.numericParameter("authQueryDeny").eq(0);
					pTypedQuery.setParameter("authQueryDeny", 1);
					expandedExpression = (expandedExpression == null ? e : expandedExpression.and(e));
				}
				case PERMIT: {
					// Leave the expression alone
				}
				case INDETERMINATE:
				case INDETERMINATE_DENY:
				case INDETERMINATE_DENYPERMIT:
				case INDETERMINATE_PERMIT:
					throw new RuntimeException("");
				}
			}
			else {

				if (policyDefRoot instanceof PolicySet)
					handlePolicySet((PolicySet) policyDefRoot, evaluationContext);

			}
		}

		// TODO: Query all the Policies to determine the changes to put against
		// expression.

		return (expandedExpression == null ? pExpression
			: (pExpression == null ? expandedExpression : pExpression.and(expandedExpression)));
	}

	private boolean handleTarget(Target pTarget, EvaluationContext pEvaluationContext) {
		boolean continueProcessing = true;
		if (pTarget != null) {
			Iterator<AnyOf> anyOfs = pTarget.getAnyOfs();
			if (anyOfs != null)
				while (anyOfs.hasNext()) {
					AnyOf anyOf = anyOfs.next();
					Iterator<AllOf> allOfs = anyOf.getAllOfs();
					if (allOfs != null)
						while (allOfs.hasNext()) {
							AllOf allOf = allOfs.next();
							Iterator<Match> matches = allOf.getMatches();
							if (matches != null)
								while (matches.hasNext()) {
									Match match = matches.next();
									Identifier matchId = match.getMatchId();
									System.out.println(matchId);
									AttributeValue<?> value = match.getAttributeValue();
									System.out.println(value);
									try {
										MatchResult result = match.match(pEvaluationContext);
										switch (result.getMatchCode()) {
										case INDETERMINATE:
											System.out.println(result.getMatchCode());
											break;
										case MATCH:
											System.out.println(result.getMatchCode());
											break;
										case NOMATCH:
											System.out.println(result.getMatchCode());
											break;
										}

									}
									catch (EvaluationException ex) {
										sLogger.error("", ex);
									}
								}
						}
				}
		}

		return continueProcessing;
	}

	private void handlePolicySet(PolicySet pSet, EvaluationContext pEvaluationContext) {

		sLogger.debug("Handling policy set {}...", pSet.getIdentifier());

		/* Get the target */

		Target target = pSet.getTarget();

		boolean processChildren = handleTarget(target, pEvaluationContext);

		if (processChildren == true) {

			/*
			 * Assuming that this policy set is usable, then we need to look at the combining elements (ie. children
			 * Policy, PolicySet, etc.)
			 */

			Iterator<PolicySetChild> iterPolicies = pSet.getChildren();
			if (iterPolicies != null) {
				while (iterPolicies.hasNext()) {
					PolicySetChild policySetChild = iterPolicies.next();
					if (policySetChild instanceof PolicySet)
						handlePolicySet((PolicySet) policySetChild, pEvaluationContext);
					else if (policySetChild instanceof Policy)
						handlePolicy((Policy) policySetChild, pEvaluationContext);
					else if (policySetChild instanceof PolicySetIdReference) {

						try {
							PolicySet child =
								(PolicySet) mPolicySetEnsureReferenceeMethod.invoke(policySetChild, pEvaluationContext);
							handlePolicySet(child, pEvaluationContext);
						}
						catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
							throw new RuntimeException(ex);
						}

					}
					else if (policySetChild instanceof PolicyIdReference) {

						try {
							Policy child =
								(Policy) mPolicyEnsureReferenceeMethod.invoke(policySetChild, pEvaluationContext);
							handlePolicy(child, pEvaluationContext);
						}
						catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
							throw new RuntimeException(ex);
						}

					}
					else
						throw new UnsupportedOperationException();
				}
			}
		}
	}

	private void handlePolicy(Policy pPolicy, EvaluationContext pEvaluationContext) {

		sLogger.debug("Handling policy {}...", pPolicy.getIdentifier());

		/* Get the target */

		Target target = pPolicy.getTarget();

		boolean processRules = handleTarget(target, pEvaluationContext);

		if (processRules == true) {

			Iterator<Rule> rules = pPolicy.getRules();
			if (rules != null)
				while (rules.hasNext()) {
					Rule rule = rules.next();

					handleRule(rule, pEvaluationContext);
				}

		}

	}

	private void handleRule(Rule pRule, EvaluationContext pEvaluationContext) {

		sLogger.debug("Handling rule {}...", pRule.getRuleId());

		Target target = pRule.getTarget();

		boolean processRules = handleTarget(target, pEvaluationContext);

		if (processRules == true) {

			Condition condition = pRule.getCondition();

			if (condition != null) {
				Expression expression = condition.getExpression();
				if (expression != null)
					try {
						handleExpression(expression, pEvaluationContext, null);
					}
					catch (EvaluationException ex) {
						throw new RuntimeException(ex);
					}
			}
		}
	}

	private ExpressionResult handleExpression(Expression pExpression, EvaluationContext pEvaluationContext,
		PolicyDefaults pPolicyDefaults) throws EvaluationException {

		if (pExpression instanceof Apply)
			return handleApply((Apply) pExpression, pEvaluationContext, pPolicyDefaults);
		else if (pExpression instanceof AttributeDesignator)
			return handleDesignator((AttributeDesignator) pExpression, pEvaluationContext);
		else
			throw new UnsupportedOperationException();
	}

	private static class ResourceExpressionResult extends ExpressionResult {

		public final Identifier mAttribute;

		public ResourceExpressionResult(Identifier pAttributeId) {
			super(new StdStatus(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE));
			mAttribute = pAttributeId;
		}

		@Override
		public AttributeValue<?> getValue() {
			return null;
		}

		@Override
		public boolean isBag() {
			return false;
		}

		@Override
		public Bag getBag() {
			return null;
		}

		public Identifier getAttribute() {
			return mAttribute;
		}

	}

	private static class JDOExpressionResult extends ExpressionResult {

		public final BooleanExpression mExpression;

		public JDOExpressionResult(BooleanExpression pExpression) {
			super(new StdStatus(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE));
			mExpression = pExpression;
		}

		@Override
		public AttributeValue<?> getValue() {
			return null;
		}

		@Override
		public boolean isBag() {
			return false;
		}

		@Override
		public Bag getBag() {
			return null;
		}

	}

	private ExpressionResult handleDesignator(AttributeDesignator pExpression, EvaluationContext pEvaluationContext) {
		try {
			boolean isResource = pExpression.getCategory().equals(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
			if (isResource == true) {
				if (pExpression.getAttributeId().equals(XACML3.ID_RESOURCE_RESOURCE_ID) == false) {

					/* Resource specific attribute. */

					return new ResourceExpressionResult(pExpression.getAttributeId());

				}
				else {
					ExpressionResult result = pExpression.evaluate(pEvaluationContext, null);
					return result;
				}
			}
			else {
				ExpressionResult result = pExpression.evaluate(pEvaluationContext, null);
				return result;
			}
		}
		catch (EvaluationException ex) {
			throw new RuntimeException(ex);
		}

	}

	private ExpressionResult handleApply(Apply pExpression, EvaluationContext pEvaluationContext,
		PolicyDefaults pPolicyDefaults) throws EvaluationException {
		Identifier functionId = pExpression.getFunctionId();

		if (functionId.equals(XACML3.ID_FUNCTION_AND)) {
			Iterator<Expression> arguments = pExpression.getArguments();
			if (arguments != null)
				while (arguments.hasNext()) {
					Expression expression = arguments.next();
					handleExpression(expression, pEvaluationContext, pPolicyDefaults);
				}
		}
		else if (functionId.equals(XACML3.ID_FUNCTION_STRING_EQUAL)) {
			Iterator<Expression> arguments = pExpression.getArguments();
			List<ExpressionResult> argResults = new ArrayList<>();
			if (arguments != null)
				while (arguments.hasNext()) {
					Expression expression = arguments.next();
					argResults.add(handleExpression(expression, pEvaluationContext, pPolicyDefaults));
				}

			if (argResults.size() != 2)
				return pExpression.evaluate(pEvaluationContext, pPolicyDefaults);

			ExpressionResult arg1 = argResults.get(0);
			ExpressionResult arg2 = argResults.get(1);
			if (((arg1 instanceof ResourceExpressionResult) == false)
				&& ((arg2 instanceof ResourceExpressionResult) == false))
				return pExpression.evaluate(pEvaluationContext, pPolicyDefaults);

			/* Generate an expression */

			/* Make sure that the resource expression is first */

			if ((arg1 instanceof ResourceExpressionResult == false)
				&& (arg2 instanceof ResourceExpressionResult == true)) {
				ExpressionResult t = arg1;
				arg1 = arg2;
				arg2 = t;
			}

			ComparableExpression<Object> expr = generateComparableExpression((ResourceExpressionResult) arg1);

			if (arg2 instanceof ResourceExpressionResult == true) {
				ComparableExpression<?> otherExpr = generateComparableExpression((ResourceExpressionResult) arg2);
				return new JDOExpressionResult(expr.eq(otherExpr));
			}
			else {
				return new JDOExpressionResult(expr.eq(arg2.getValue().getValue()));
			}
		}
		else if (functionId.equals(XACML3.ID_FUNCTION_STRING_ONE_AND_ONLY)) {
			Iterator<Expression> arguments = pExpression.getArguments();
			if (arguments != null)
				while (arguments.hasNext()) {
					ExpressionResult r = handleExpression(arguments.next(), pEvaluationContext, pPolicyDefaults);
					if (r instanceof ResourceExpressionResult)
						return r;
				}
			return pExpression.evaluate(pEvaluationContext, pPolicyDefaults);
		}

		throw new UnsupportedOperationException();
	}

	private ComparableExpression<Object> generateComparableExpression(ResourceExpressionResult pArg) {
		Identifier attribute = pArg.getAttribute();

		/* Reverse the attribute to a class/attribute */

		Class<?> dd;
		String methodName;
		if (attribute.stringValue().equals("urn:roadassistant.diamondq.com:xacml:3.0:resource:resource-owner")) {
			try {
				dd = Class.forName("com.diamondq.roadassistant.service.data.server.model.Component");
				methodName = "createdById";
			}
			catch (ClassNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		}
		else
			throw new UnsupportedOperationException();

		/* Calculate the Q name */

		String qClassName = dd.getPackage().getName() + ".Q" + dd.getSimpleName();
		Class<?> qClass;
		try {
			qClass = Class.forName(qClassName);
		}
		catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		/* Lookup the candiate method */

		Method qCandidateMethod;
		try {
			qCandidateMethod = qClass.getMethod("candidate");
		}
		catch (NoSuchMethodException | SecurityException ex) {
			throw new RuntimeException(ex);
		}

		/* Lookup the method */

		Field qField;
		try {
			qField = qClass.getField(methodName);
		}
		catch (NoSuchFieldException | SecurityException ex) {
			throw new RuntimeException(ex);
		}

		/* Now get the expression */

		try {
			Object candidate = qCandidateMethod.invoke(null);
			@SuppressWarnings("unchecked")
			ComparableExpression<Object> result = (ComparableExpression<Object>) qField.get(candidate);
			return result;
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

}

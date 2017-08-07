package com.diamondq.common.reaction.engine;

import com.diamondq.common.config.Config;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.Engine;
import com.diamondq.common.reaction.api.JobContext;
import com.diamondq.common.reaction.api.JobDefinition;
import com.diamondq.common.reaction.api.impl.StateCriteria;
import com.diamondq.common.reaction.api.impl.StateValueCriteria;
import com.diamondq.common.reaction.api.impl.StateVariableCriteria;
import com.diamondq.common.reaction.api.impl.VariableCriteria;
import com.diamondq.common.reaction.engine.definitions.JobDefinitionImpl;
import com.diamondq.common.reaction.engine.definitions.ParamDefinition;
import com.diamondq.common.reaction.engine.definitions.ResultDefinition;
import com.diamondq.common.reaction.engine.definitions.TriggerDefinition;
import com.diamondq.common.reaction.engine.evals.ActionNode;
import com.diamondq.common.reaction.engine.evals.NameNode;
import com.diamondq.common.reaction.engine.evals.TypeNode;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.jdt.annotation.NonNull;

import net.jodah.typetools.TypeResolver;

@ApplicationScoped
public class EngineImpl implements Engine {

	private static final String								sUNDEFINED			= "__UNDEFINED__";

	private static final String								sPERSISTENT_STATE	= "persistent";

	private final Store										mPersistentStore;

	private final Store										mTransientStore;

	private final CopyOnWriteArraySet<JobDefinitionImpl>	mJobs;

	/* Trigger tree */

	private final ConcurrentMap<String, ActionNode>			mTriggers;

	private final ConcurrentMap<String, TypeNode>			mResultTree;

	private final CDIObservingExtension						mObservations;

	private final ExecutorService							mExecutorService;

	@Inject
	public EngineImpl(Toolkit pToolkit, Config pConfig, ExecutorService pExecutorService,
		CDIObservingExtension pExtension) {
		mTriggers = Maps.newConcurrentMap();
		mResultTree = Maps.newConcurrentMap();
		mExecutorService = pExecutorService;
		mObservations = pExtension;
		mJobs = Sets.newCopyOnWriteArraySet();

		/* Persistent */

		String persistentScopeName = pConfig.bind("reaction.engine.scopes.persistent", String.class);
		if (persistentScopeName == null)
			throw new IllegalArgumentException("The config key reaction.engine.scopes.persistent is mandatory");

		mPersistentStore = new Store(pToolkit, persistentScopeName);

		/* Transient */

		String transientScopeName = pConfig.bind("reaction.engine.scopes.transient", String.class);
		if (transientScopeName == null)
			throw new IllegalArgumentException("The config key reaction.engine.scopes.transient is mandatory");

		mTransientStore = new Store(pToolkit, transientScopeName);

	}

	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

		/* Process each possible job configuration */

		JobContext jc = new JobContextImpl(this);
		for (CDIObservingExtension.MethodSupplierPair<?> pair : mObservations.getJobs()) {

			setupClass(jc, pair);

		}
	}

	private <C> void setupClass(JobContext pContext, CDIObservingExtension.MethodSupplierPair<C> pair) {
		Supplier<C> supplier = pair.supplier.apply(pair.clazz);
		C obj = supplier.get();
		try {
			pair.method.invoke(obj, pContext);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void registerJob(JobDefinition pDefinition) {
		if (pDefinition instanceof JobDefinitionImpl == false)
			throw new IllegalArgumentException("Only JobDefinitionImpl is supported");
		JobDefinitionImpl definition = (JobDefinitionImpl) pDefinition;
		mJobs.add(definition);

		/* Register against the trigger tree */

		for (TriggerDefinition<?> td : definition.triggers) {
			String actionName = td.action.name();
			ActionNode actionNode = mTriggers.get(actionName);
			if (actionNode == null) {
				ActionNode newActionNode = new ActionNode(td.action);
				if ((actionNode = mTriggers.putIfAbsent(actionName, newActionNode)) == null)
					actionNode = newActionNode;
			}
			String type = td.clazz.getName();
			TypeNode typeNode = actionNode.getOrAddType(type);
			String name = td.name;
			if (name == null)
				name = sUNDEFINED;
			NameNode nameNode = typeNode.getOrAddName(name);
			nameNode.addCriteria(Iterables.concat(td.requiredStates, td.variables), definition);
		}

		/* Register against the result tree */

		for (ResultDefinition<?> rd : definition.results) {
			String type = rd.clazz.getName();
			TypeNode typeNode = mResultTree.get(type);
			if (typeNode == null) {
				TypeNode newTypeNode = new TypeNode(type);
				if ((typeNode = mResultTree.putIfAbsent(type, newTypeNode)) == null)
					typeNode = newTypeNode;
			}
			String name = rd.name;
			if (name != null) {
				NameNode nameNode = typeNode.getOrAddName(name);
				nameNode.addCriteria(Iterables.concat(rd.requiredStates, rd.variables), definition);
			}
			NameNode nameNode = typeNode.getOrAddName(sUNDEFINED);
			nameNode.addCriteria(Iterables.concat(rd.requiredStates, rd.variables), definition);
		}
	}

	/**
	 * @see com.diamondq.common.reaction.api.Engine#submit(java.lang.Class)
	 */
	@Override
	public <T> @NonNull ExtendedCompletableFuture<T> submit(@NonNull Class<T> pResultClass) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.reaction.api.Engine#addToCollection(java.lang.Object,
	 *      com.diamondq.common.reaction.api.Action, java.lang.String, java.util.Map)
	 */
	@Override
	public <@NonNull T> ExtendedCompletableFuture<@Nullable Void> addToCollection(T pRecord, Action pAction,
		String pName, Map<String, String> pStates) {

		String type = pRecord.getClass().getName();

		/* Determine if the transient or persistent mScope is to be used */

		boolean isPersistent = false;
		String persistenceValue = pStates.get(sPERSISTENT_STATE);
		if ("true".equals(persistenceValue) == true)
			isPersistent = true;

		Store store = (isPersistent == true ? mPersistentStore : mTransientStore);

		return storeAndTrigger(store, pRecord, pAction, type, pName, pStates);
	}

	private <T> ExtendedCompletableFuture<@Nullable Void> storeAndTrigger(Store pStore, T pRecord, Action pAction,
		String pType, String pName, Map<String, String> pStates) {
		/* Persist the object */

		pStore.persist(pRecord, pAction, pType, pName, pStates);

		/* Next, we need to find the set of jobs that would be triggered by this change */

		Set<JobRequest> jobs = resolveTriggers(pRecord, pAction, pType, pName, pStates);

		/* Submit each job for execution */

		Set<ExtendedCompletableFuture<@Nullable Void>> futures = Sets.newHashSet();
		for (JobRequest job : jobs) {
			futures.add(submit(job));
		}
		return ExtendedCompletableFuture.allOf(futures);
	}

	/**
	 * Submit a given job definition for execution. This attempts to resolve all dependencies, and once they are
	 * resolved, executes them. Once the job is queued for execution, it returns and all the work runs on a separate
	 * thread.
	 * 
	 * @param pJob the job to execute
	 * @return the future
	 */
	private ExtendedCompletableFuture<@Nullable Void> submit(JobRequest pJob) {

		ExtendedCompletableFuture<@Nullable Void> result = new ExtendedCompletableFuture<>();
		ExtendedCompletableFuture.runAsync(new Runnable() {

			@Override
			public void run() {
				executeIfDepends(pJob, result);
			}
		}, mExecutorService).whenComplete((v, ex) -> {
			if (ex != null)
				result.completeExceptionally(ex);
		});
		return result;
	}

	/**
	 * Checks all the dependencies on this job, and if they're all available, executes the job. If not, it attempts to
	 * find jobs that can fulfill the dependences, and schedules those.
	 * 
	 * @param pJob the job
	 * @param pResult the result
	 */
	private void executeIfDepends(JobRequest pJob, ExtendedCompletableFuture<@Nullable Void> pResult) {

		List<Object> dependents = Lists.newArrayList();
		for (ParamDefinition<?> param : pJob.jobDefinition.params) {
			Object dependent = null;
			if (param.persistent != null) {
				Store store = (param.persistent == true ? mPersistentStore : mTransientStore);
				Set<Object> storeDependents = store.resolve(param);
				// TODO: For now, just take the first
				if (storeDependents.isEmpty() == false)
					dependent = storeDependents.iterator().next();
			}
			else {
				Set<Object> storeDependents = mTransientStore.resolve(param);
				// TODO: For now, just take the first
				if (storeDependents.isEmpty() == false)
					dependent = storeDependents.iterator().next();
				if (dependent == null) {
					storeDependents = mPersistentStore.resolve(param);
					// TODO: For now, just take the first
					if (storeDependents.isEmpty() == false)
						dependent = storeDependents.iterator().next();
				}
			}

			if (dependent == null) {

				/* Find the list of jobs that can produce it */

				Set<JobRequest> possibleJobs = resolveParams(param);
				JobRequest bestJob = sortJobs(possibleJobs);
				if (bestJob == null)
					throw new IllegalStateException("There is no possible job to produce the needed dependent");

				/* Execute the job */

				ExtendedCompletableFuture.runAsync(new Runnable() {

					@Override
					public void run() {
						ExtendedCompletableFuture<@Nullable Void> result = new ExtendedCompletableFuture<>();

						/*
						 * If the job fails, then we fail. if the job succeeds, then re-run ourselves to see if all our
						 * dependencies are available
						 */
						result.whenComplete((v, ex) -> {
							if (ex != null) {
								pResult.completeExceptionally(ex);
								return;
							}
							ExtendedCompletableFuture.runAsync(() -> executeIfDepends(pJob, pResult), mExecutorService)
								.whenComplete((v2, ex2) -> {
									if (ex2 != null)
										pResult.completeExceptionally(ex2);
								});
						});
						executeIfDepends(bestJob, result);

					}
				}, mExecutorService).whenComplete((v, ex) -> {
					if (ex != null)
						pResult.completeExceptionally(ex);
				});

				/* Exit now, so that the dependent can be built */

				return;
			}
			dependents.add(dependent);
		}

		/* Now execute the job */

		Object result = execute(pJob, pJob.jobDefinition.params.toArray(new ParamDefinition<?>[0]),
			dependents.toArray(new Object[0]));

		/* Store the results and trigger anything that needs it */

		for (ResultDefinition<?> rd : pJob.jobDefinition.results) {

			/* Determine the actual object */

			Object rdObject;
			if (rd.resultIsParam == false) {
				if (result == null)
					throw new IllegalArgumentException("The result must not be null");
				rdObject = result;
			}
			else {
				throw new UnsupportedOperationException();
			}

			Map<String, String> states = Maps.newHashMap();
			// TODO: Determine the states

			String name = rd.name;
			if (name == null)
				throw new UnsupportedOperationException();

			Store store = (rd.persistent == null ? mTransientStore
				: (rd.persistent == true ? mPersistentStore : mTransientStore));
			storeAndTrigger(store, rdObject, Action.CHANGE, rd.clazz.getName(), name, states);

		}

		/* We're done */

		pResult.complete(null);
	}

	/**
	 * Attempt to resolve the param definition to find a job that has it as a result
	 * 
	 * @param pParam the param
	 * @return the set of jobs that produce that result
	 */
	private Set<JobRequest> resolveParams(ParamDefinition<?> pParam) {
		Set<TypeNode> typeNodes = Sets.newIdentityHashSet();

		/* Types */

		TypeNode possibleTypeNode;
		possibleTypeNode = mResultTree.get(pParam.clazz.getName());
		if (possibleTypeNode != null)
			typeNodes.add(possibleTypeNode);

		/* Names */

		Set<NameNode> nameNodes = Sets.newIdentityHashSet();
		for (TypeNode typeNode : typeNodes) {
			NameNode possibleNameNode;
			String name = pParam.name;
			if (name != null) {
				possibleNameNode = typeNode.getName(name);
				if (possibleNameNode != null)
					nameNodes.add(possibleNameNode);
			}
			possibleNameNode = typeNode.getName(sUNDEFINED);
			if (possibleNameNode != null)
				nameNodes.add(possibleNameNode);
		}

		/* States */

		Set<JobRequest> jobs = Sets.newIdentityHashSet();
		for (NameNode nameNode : nameNodes) {
			Set<StateCriteria[]> criteriasSet = nameNode.getCriterias();
			for (StateCriteria[] criterias : criteriasSet) {
				Set<JobDefinitionImpl> jobsByCriteria = nameNode.getJobsByCriteria(criterias);
				if (jobsByCriteria != null)
					for (JobDefinitionImpl job : jobsByCriteria)
						jobs.add(new JobRequest(job, null));
			}
			for (JobDefinitionImpl job : nameNode.getNoCriteriaJobs())
				jobs.add(new JobRequest(job, null));
		}

		return jobs;
	}

	/**
	 * Sorts the set of possible jobs to the best one to try.
	 * 
	 * @param pPossibleJobs
	 * @return
	 */
	private @Nullable JobRequest sortJobs(Set<@NonNull JobRequest> pPossibleJobs) {
		if (pPossibleJobs.isEmpty() == true)
			return null;

		/*
		 * TODO: This hack just returns the first job. A better implementation would look at the ones that have the
		 * fewest dependencies
		 */

		return pPossibleJobs.iterator().next();
	}

	private @Nullable Object execute(JobRequest pJob, ParamDefinition<?>[] pArray, Object[] pDependents) {
		Object callback = pJob.jobDefinition.method.getCallback();
		Method method = pJob.jobDefinition.method.getMethod();
		Class<?> functionClass = pJob.jobDefinition.method.getCallbackClass();
		Class<?>[] parameterTypes = TypeResolver.resolveRawArguments(functionClass, callback.getClass());
		int paramLen = parameterTypes.length - (pJob.jobDefinition.method.getHasReturn() == true ? 1 : 0);
		Object[] invokeParams = new Object[paramLen];

		/* Now, for each parameter, find the matching dependent */

		for (int i = 0; i < paramLen; i++) {
			boolean match = false;
			for (int o = 0; o < pArray.length; o++) {
				if (pArray[o].clazz.equals(parameterTypes[i])) {
					invokeParams[i] = pDependents[o];
					match = true;
					break;
				}
			}
			if ((match == false) && (pJob.triggerObject != null)) {
				if (parameterTypes[i].isAssignableFrom(pJob.triggerObject.getClass())) {
					invokeParams[i] = pJob.triggerObject;
					match = true;
				}
			}
		}

		Object result;
		try {
			method.setAccessible(true);
			result = method.invoke(callback, invokeParams);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}

	private <T> Set<JobRequest> resolveTriggers(T pTriggerObject, @Nullable Action pAction, @Nullable String pType,
		@Nullable String pName, @Nullable Map<String, String> pStates) {
		Set<ActionNode> actionNodes = Sets.newIdentityHashSet();

		/* ActionNodes */

		ActionNode possibleActionNode;
		if (pAction != null) {
			possibleActionNode = mTriggers.get(pAction.name());
			if (possibleActionNode != null)
				actionNodes.add(possibleActionNode);
		}
		possibleActionNode = mTriggers.get(sUNDEFINED);
		if (possibleActionNode != null)
			actionNodes.add(possibleActionNode);

		/* Types */

		Set<TypeNode> typeNodes = Sets.newIdentityHashSet();
		for (ActionNode actionNode : actionNodes) {
			TypeNode possibleTypeNode;
			if (pType != null) {
				possibleTypeNode = actionNode.getType(pType);
				if (possibleTypeNode != null)
					typeNodes.add(possibleTypeNode);
			}
			possibleTypeNode = actionNode.getType(sUNDEFINED);
			if (possibleTypeNode != null)
				typeNodes.add(possibleTypeNode);
		}

		/* Names */

		Set<NameNode> nameNodes = Sets.newIdentityHashSet();
		for (TypeNode typeNode : typeNodes) {
			NameNode possibleNameNode;
			if (pName != null) {
				possibleNameNode = typeNode.getName(pName);
				if (possibleNameNode != null)
					nameNodes.add(possibleNameNode);
			}
			possibleNameNode = typeNode.getName(sUNDEFINED);
			if (possibleNameNode != null)
				nameNodes.add(possibleNameNode);
		}

		/* States */

		Set<JobRequest> jobs = Sets.newIdentityHashSet();
		for (NameNode nameNode : nameNodes) {
			Set<StateCriteria[]> criteriasSet = nameNode.getCriterias();
			for (StateCriteria[] criterias : criteriasSet) {

				/* Check the criterias */

				boolean match = true;
				for (StateCriteria criteria : criterias) {
					if (criteria instanceof StateValueCriteria) {
						StateValueCriteria svc = (StateValueCriteria) criteria;
						if ((pStates == null) || (pStates.containsKey(svc.state) == false)) {
							if (svc.isEqual == false)
								continue;
							match = false;
							break;
						}
						if (svc.value.equals(pStates.get(svc.state)) == false) {
							if (svc.isEqual == false)
								continue;
							match = false;
							break;
						}
					}
					else if (criteria instanceof StateVariableCriteria) {
						throw new UnsupportedOperationException();
					}
					else if (criteria instanceof VariableCriteria) {
						VariableCriteria vc = (VariableCriteria) criteria;
						if ((pStates == null) || (pStates.containsKey(vc.state) == false)) {
							if (vc.isEqual == false)
								continue;
							match = false;
							break;
						}
						// TODO: Remember the variable
					}
					else {
						if ((pStates == null) || (pStates.containsKey(criteria.state) == false)) {
							if (criteria.isEqual == false)
								continue;
							match = false;
							break;
						}
					}
				}
				if (match == true) {
					@Nullable
					Set<JobDefinitionImpl> possibleJobs = nameNode.getJobsByCriteria(criterias);
					if (possibleJobs != null) {
						for (JobDefinitionImpl job : possibleJobs) {
							jobs.add(new JobRequest(job, pTriggerObject));
						}
					}
				}
			}
			for (JobDefinitionImpl job : nameNode.getNoCriteriaJobs()) {
				jobs.add(new JobRequest(job, pTriggerObject));
			}
		}

		return jobs;
	}

}

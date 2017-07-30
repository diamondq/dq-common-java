package com.diamondq.common.asyncjobs.engine;

import com.diamondq.common.asyncjobs.api.Engine;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;

@ApplicationScoped
public class EngineImpl implements Engine {

	private final ExecutorService										mExecutorService;

	private final ConcurrentMap<String, ConcurrentMap<String, Object>>	mCachedContextData	= Maps.newConcurrentMap();

	private final ConcurrentMap<Definition, Set<Job<?>>>				mJobsByProduces;

	@Inject
	public EngineImpl(CDIObservingExtension pExtension, ExecutorService pExecutorService) {
		mExecutorService = pExecutorService;
		mJobsByProduces = pExtension.getJobsByProduces();
	}

	/**
	 * @see com.diamondq.common.asyncjobs.api.Engine#submit(java.lang.Class, java.lang.Class)
	 */
	@Override
	public <T> ExtendedCompletableFuture<T> submit(@Nullable Class<?> pClass, Class<T> pResultClass) {
		Job<T> job = Job.of(pClass, pResultClass);
		JobInstance<T> instance = new JobInstance<T>();
		submit(job, instance);
		return instance.getFuture();
	}

	private <T> void submit(Job<T> pJob, JobInstance<T> pInstance) {

		/* Next, we need to figure out whether the dependency can be resolved */

		calculateNextResolutionStep(pJob, pInstance);
	}

	private <T> void validateJob(Job<T> pJob, JobInstance<T> pInstance) {

	}

	private synchronized <T> void calculateNextResolutionStep(Job<T> pJob, JobInstance<T> pInstance) {

		/* For each dependson, see if it's available */

		ConcurrentMap<String, Object> contextMap = mCachedContextData.get(pInstance.getContext());
		if (contextMap == null) {
			ConcurrentMap<String, Object> newContextMap = Maps.newConcurrentMap();
			if ((contextMap = mCachedContextData.putIfAbsent(pInstance.getContext(), newContextMap)) == null)
				contextMap = newContextMap;
		}
		final ConcurrentMap<String, Object> finalContextMap = contextMap;
		for (Definition d : pJob.getDependsOn()) {
			String dependsKey = d.getDefinitionKey();
			Object dependsObject = finalContextMap.get(dependsKey);
			if (dependsObject == null) {

				/* We don't have this object, so we should figure out how to ask for it */

				Set<Job<?>> set = mJobsByProduces.get(d);
				if ((set == null) || (set.isEmpty() == true))
					throw new IllegalArgumentException(
						"Unable to find a way to resolve the dependency " + d.toString());

				Job<?> job = set.iterator().next();
				@SuppressWarnings("unchecked")
				Job<Object> castedJob = (Job<Object>) job;
				JobInstance<Object> childInstance = new JobInstance<Object>(pInstance.getContext());
				submit(castedJob, childInstance);
				childInstance.getFuture().handle((r, ex) -> {
					if (ex != null)
						pInstance.getFuture().completeExceptionally(ex);
					else {
						finalContextMap.put(dependsKey, r);
						calculateNextResolutionStep(pJob, pInstance);
					}
					return null;
				});
				return;
			}
		}

		/* All dependencies were resolved, submit the job for execution */

		ExtendedCompletableFuture<T> f = ExtendedCompletableFuture.supplyAsync(new Supplier<T>() {

			@Override
			public T get() {
				validateJob(pJob, pInstance);
				Object object = pJob.getSupplier().get();
				Object resultObj;
				try {
					Method method = pJob.getMethod();
					Parameter[] parameters = method.getParameters();
					Object[] paramValues = new Object[parameters.length];
					ConcurrentMap<String, Object> evalContextMap = mCachedContextData.get(pInstance.getContext());
					if (evalContextMap == null)
						throw new IllegalStateException("The context map should not be null");
					List<String> dependsOnParameters = pJob.getDependsOnParameters();
					if (parameters.length > 0)
						for (int i = 0; i < parameters.length; i++) {
							String key = dependsOnParameters.get(i);
							Object value = evalContextMap.get(key);
							if (value == null)
								throw new IllegalStateException("The value should not be null");
							paramValues[i] = value;
						}
					resultObj = method.invoke(object, paramValues);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					throw new RuntimeException(ex);
				}
				@SuppressWarnings("unchecked")
				T result = (T) resultObj;
				return result;
			}
		}, mExecutorService);

		f.handle((r, ex) -> {
			if (ex != null)
				pInstance.getFuture().completeExceptionally(ex);
			else
				pInstance.getFuture().complete(r);
			return null;
		});
		// f.handleAsync(pFn, pExecutor);
	}

}

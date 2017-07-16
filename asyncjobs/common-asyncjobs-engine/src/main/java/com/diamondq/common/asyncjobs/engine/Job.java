package com.diamondq.common.asyncjobs.engine;

import com.diamondq.common.asyncjobs.api.AsyncJob;
import com.diamondq.common.asyncjobs.engine.definitions.ClassDefinition;
import com.diamondq.common.asyncjobs.engine.suppliers.ClassSupplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <R> the expected return type
 */
public class Job<R> {

	private static final ConcurrentMap<String, Job<?>>	sCachableJobs	= Maps.newConcurrentMap();

	private final Supplier<?>							mSupplier;

	private final String								mJobKey;

	private final Method								mMethod;

	private final Set<Definition>						mProduces;

	private final Collection<Definition>				mDependsOn;

	private final List<String>							mDependsOnParameters;

	public Job(String pJobKey, Supplier<?> pSupplier, Method pMethod, Collection<Definition> pDependsOn,
		Set<Definition> pProduces, List<String> pDependsOnParameters) {
		mJobKey = pJobKey;
		mSupplier = pSupplier;
		mMethod = pMethod;
		mDependsOn = pDependsOn;
		mProduces = pProduces;
		mDependsOnParameters = pDependsOnParameters;
	}

	/**
	 * Called to determine if the class provides any 'jobs'
	 * 
	 * @param pClass the class to check
	 * @param pClassSupplierBuilder
	 * @return the list of jobs (empty list is possible)
	 */
	public static <C> Collection<Job<?>> determineJobs(Class<C> pClass,
		Function<Class<C>, Supplier<C>> pClassSupplierBuilder) {
		Set<Method> methods = scanForMethods(pClass);
		String jobKey = pClass.getName();
		ImmutableList.Builder<Job<?>> builder = ImmutableList.builder();
		for (Method m : methods) {
			List<Definition> dependsSet = scanParameters(m);
			List<String> dependsOnParams = Lists.transform(dependsSet, d -> d.getDefinitionKey());
			Set<Definition> resultSet = scanReturn(m);

			Job<Object> newJob =
				new Job<Object>(jobKey, pClassSupplierBuilder.apply(pClass), m, dependsSet, resultSet, dependsOnParams);
			sCachableJobs.putIfAbsent(jobKey, newJob);
			builder.add(newJob);
		}
		return builder.build();
	}

	private static Set<Definition> scanReturn(Method pMethod) {
		ImmutableSet.Builder<Definition> builder = ImmutableSet.builder();
		Class<?> returnType = pMethod.getReturnType();
		ImmutableSet.Builder<Annotation> annotations = ImmutableSet.builder();
		for (@SuppressWarnings("unused")
		Annotation a : pMethod.getAnnotatedReturnType().getAnnotations()) {
			throw new UnsupportedOperationException();
		}

		builder.add(new ClassDefinition(returnType, annotations.build()));
		return builder.build();
	}

	private static List<Definition> scanParameters(Method pMethod) {
		ImmutableList.Builder<Definition> builder = ImmutableList.builder();
		for (Parameter p : pMethod.getParameters()) {
			ImmutableSet.Builder<Annotation> annotations = ImmutableSet.builder();
			for (@SuppressWarnings("unused")
			Annotation a : p.getAnnotations()) {
				throw new UnsupportedOperationException();
			}

			Class<?> paramType = p.getType();
			builder.add(new ClassDefinition(paramType, annotations.build()));
		}
		return builder.build();
	}

	/**
	 * Generates a job based on a single class
	 * 
	 * @param pClass the class
	 * @param pResultClass the expected result class
	 * @return the job
	 */
	public static <T> Job<T> of(Class<?> pClass, Class<T> pResultClass) {
		String jobKey = pClass.getName();

		Job<?> existingJob = sCachableJobs.get(jobKey);
		if (existingJob != null) {
			@SuppressWarnings("unchecked")
			Job<T> result = (Job<T>) existingJob;
			return result;
		}

		/* Find the method */
		Set<Method> methods = scanForMethods(pClass, pResultClass);
		if (methods.size() != 1)
			throw new IllegalArgumentException(
				"Only one method annotated with @AsyncJob is supported when only passing a Class");
		Method method = Iterables.get(methods, 0);

		/* Now scan for all the dependencies on the method */

		List<Definition> dependsSet = scanParameters(method);
		List<String> dependsOnParams = Lists.transform(dependsSet, d -> d.getDefinitionKey());
		Set<Definition> resultSet = scanReturn(method);

		Job<T> newJob = new Job<T>(jobKey, new ClassSupplier<>(pClass), method, dependsSet, resultSet, dependsOnParams);
		sCachableJobs.putIfAbsent(jobKey, newJob);
		return newJob;
	}

	private static <T> Set<Method> scanForMethods(Class<?> pClass, Class<T> pResultClass) {
		Set<Method> results = Sets.newHashSet();
		for (Method m : pClass.getMethods()) {
			if (m.isAnnotationPresent(AsyncJob.class) == false)
				continue;
			if (pResultClass.equals(Void.class) == false) {
				Class<?> returnType = m.getReturnType();
				if (returnType.isAssignableFrom(pResultClass) == false)
					continue;
			}
			results.add(m);
		}
		return results;
	}

	private static <T> Set<Method> scanForMethods(Class<?> pClass) {
		Set<Method> results = Sets.newHashSet();
		for (Method m : pClass.getMethods()) {
			if (m.isAnnotationPresent(AsyncJob.class) == false)
				continue;
			results.add(m);
		}
		return results;
	}

	public String getJobKey() {
		return mJobKey;
	}

	public Supplier<?> getSupplier() {
		return mSupplier;
	}

	public Method getMethod() {
		return mMethod;
	}

	public Collection<Definition> getDependsOn() {
		return mDependsOn;
	}

	public List<String> getDependsOnParameters() {
		return mDependsOnParameters;
	}

	public Set<Definition> getProduces() {
		return mProduces;
	}
}

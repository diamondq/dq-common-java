package com.diamondq.common.asyncjobs.engine;

import com.diamondq.common.asyncjobs.engine.suppliers.CDISupplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessManagedBean;

public class CDIObservingExtension implements Extension {

	private final ConcurrentMap<Definition, Set<Job<?>>>						mJobsByProduces	=
		Maps.newConcurrentMap();

	private final ConcurrentMap<BeanManager, Function<Class<?>, Supplier<?>>>	mCachedFuncMap	=
		Maps.newConcurrentMap();

	/**
	 * Called by the CDI system whenever a new bean is registered. Used to keep the job directory up-to-date
	 * 
	 * @param pBean the bean
	 * @param pBeanManager the bean manager
	 */
	protected <X> void notifyOfBean(@Observes ProcessManagedBean<X> pBean, BeanManager pBeanManager) {

		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Class<X> beanClass = (Class<X>) (Class) pBean.getBean().getBeanClass();

		Function<Class<?>, Supplier<?>> function = mCachedFuncMap.get(pBeanManager);
		if (function == null) {
			Function<Class<X>, Supplier<X>> newFunction = new Function<Class<X>, Supplier<X>>() {

				@Override
				public Supplier<X> apply(Class<X> pT) {
					return new CDISupplier<X>(pBeanManager, pT);
				}
			};
			@SuppressWarnings({"cast", "unchecked", "rawtypes"})
			Function<Class<?>, Supplier<?>> castedNewFunction =
				(Function<Class<?>, Supplier<?>>) (Function) newFunction;
			if ((function = mCachedFuncMap.putIfAbsent(pBeanManager, castedNewFunction)) == null)
				function = castedNewFunction;
		}

		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Function<Class<X>, Supplier<X>> castedF = (Function<Class<X>, Supplier<X>>) (Function) function;
		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Class<X> castedBeanClass = (Class<X>) (Class) beanClass;
		Collection<Job<?>> jobs = Job.determineJobs(castedBeanClass, castedF);
		for (Job<?> job : jobs) {
			Set<Definition> produces = job.getProduces();
			for (Definition d : produces) {
				Set<Job<?>> set = mJobsByProduces.get(d);
				if (set == null) {
					Set<Job<?>> newSet = Sets.newConcurrentHashSet();
					if ((set = mJobsByProduces.putIfAbsent(d, newSet)) == null)
						set = newSet;
				}
				set.add(job);
			}
		}
	}

	public ConcurrentMap<Definition, Set<Job<?>>> getJobsByProduces() {
		return mJobsByProduces;
	}

}

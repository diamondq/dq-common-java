package com.diamondq.common.asyncjobs.api.methods;

@FunctionalInterface
public interface Function2<T1, T2, R> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t1 the input argument
	 * @param t2 the input argument
	 * @return the result
	 */
	R apply(T1 t1, T2 t2);
}

package com.diamondq.common.asyncjobs.api.methods;

@FunctionalInterface
public interface Function1<T1, R> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t1 the input argument
	 * @return the result
	 */
	R apply(T1 t1);
}

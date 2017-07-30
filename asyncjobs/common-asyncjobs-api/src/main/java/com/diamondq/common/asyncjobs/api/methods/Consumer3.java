package com.diamondq.common.asyncjobs.api.methods;

@FunctionalInterface
public interface Consumer3<T1, T2, T3> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t1 the input argument
	 * @param t2 the input argument
	 * @param t3 the input argument
	 */
	void accept(T1 t1, T2 t2, T3 t3);
}

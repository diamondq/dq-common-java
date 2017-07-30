package com.diamondq.common.asyncjobs.api.methods;

@FunctionalInterface
public interface Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t1 the input argument
	 * @param t2 the input argument
	 * @param t3 the input argument
	 * @param t4 the input argument
	 * @param t5 the input argument
	 * @param t6 the input argument
	 * @param t7 the input argument
	 * @param t8 the input argument
	 * @return the result
	 */
	R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
}

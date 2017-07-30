package com.diamondq.common.asyncjobs.api.methods;

@FunctionalInterface
public interface Consumer1<T> {

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t the input argument
	 */
	void accept(T t);
}

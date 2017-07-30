package com.diamondq.common.asyncjobs.api.methods;

@FunctionalInterface
public interface Function0<R> {

	/**
	 * Performs this operation
	 *
	 * @return the result
	 */
	R apply();
}

package com.diamondq.common.tracing.opentracing.wrappers;

public interface AbortableContinuation {

	/**
	 * Aborts any stored Continuation (to release any held resources)
	 */
	public void abortContinuation();

}

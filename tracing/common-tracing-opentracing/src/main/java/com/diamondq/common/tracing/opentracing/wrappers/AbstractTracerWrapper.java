package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.concurrent.atomic.AtomicReference;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;

public abstract class AbstractTracerWrapper implements AbortableContinuation {

	protected final AtomicReference<@Nullable Continuation> mSpanContinuation;

	public AbstractTracerWrapper(Tracer pTracer) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		mSpanContinuation = new AtomicReference<>(activeSpan != null ? activeSpan.capture() : null);
	}

	/**
	 * @see com.diamondq.common.tracing.opentracing.wrappers.AbortableContinuation#abortContinuation()
	 */
	@Override
	public void abortContinuation() {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c != null)
			c.activate().close();
	}
}

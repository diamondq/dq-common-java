package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.BiConsumer;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerBiConsumer<A, B> extends AbstractTracerWrapper implements BiConsumer<A, B>, AbortableContinuation {

	private final BiConsumer<A, B> mDelegate;

	public TracerBiConsumer(BiConsumer<A, B> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerBiConsumer(Tracer pTracer, BiConsumer<A, B> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void accept(A pA, B pB) {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c == null) {
			mDelegate.accept(pA, pB);
			return;
		}
		try (ActiveSpan span = c.activate()) {
			mDelegate.accept(pA, pB);
		}
	}

}

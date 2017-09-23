package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.BiFunction;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerBiFunction<A, B, C> extends AbstractTracerWrapper implements BiFunction<A, B, C> {

	private final BiFunction<A, B, C> mDelegate;

	public TracerBiFunction(BiFunction<A, B, C> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerBiFunction(Tracer pTracer, BiFunction<A, B, C> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public C apply(A pA, B pB) {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c == null)
			return mDelegate.apply(pA, pB);
		try (ActiveSpan span = c.activate()) {
			return mDelegate.apply(pA, pB);
		}
	}

}

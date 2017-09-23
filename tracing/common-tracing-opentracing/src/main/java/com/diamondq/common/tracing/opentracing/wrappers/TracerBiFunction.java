package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerBiFunction<A, B, C> implements BiFunction<A, B, C> {

	private final @Nullable Continuation	mSpanContinuation;

	private final BiFunction<A, B, C>		mDelegate;

	public TracerBiFunction(BiFunction<A, B, C> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerBiFunction(Tracer pTracer, BiFunction<A, B, C> pDelegate) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		if (activeSpan != null)
			mSpanContinuation = activeSpan.capture();
		else
			mSpanContinuation = null;
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public C apply(A pA, B pB) {
		if (mSpanContinuation == null)
			return mDelegate.apply(pA, pB);
		try (@SuppressWarnings("null")
		ActiveSpan span = mSpanContinuation.activate()) {
			return mDelegate.apply(pA, pB);
		}
	}

}

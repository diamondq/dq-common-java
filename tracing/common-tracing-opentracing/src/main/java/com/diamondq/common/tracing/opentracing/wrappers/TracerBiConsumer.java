package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.BiConsumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerBiConsumer<A, B> implements BiConsumer<A, B> {

	private final @Nullable Continuation	mSpanContinuation;

	private final BiConsumer<A, B>			mDelegate;

	public TracerBiConsumer(BiConsumer<A, B> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerBiConsumer(Tracer pTracer, BiConsumer<A, B> pDelegate) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		if (activeSpan != null)
			mSpanContinuation = activeSpan.capture();
		else
			mSpanContinuation = null;
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void accept(A pA, B pB) {
		if (mSpanContinuation == null) {
			mDelegate.accept(pA, pB);
			return;
		}
		try (@SuppressWarnings("null")
		ActiveSpan span = mSpanContinuation.activate()) {
			mDelegate.accept(pA, pB);
		}
	}

}

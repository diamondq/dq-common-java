package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerFunction<A, B> implements Function<A, B> {

	private final @Nullable Continuation	mSpanContinuation;

	private final Function<A, B>			mDelegate;

	public TracerFunction(Function<A, B> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerFunction(Tracer pTracer, Function<A, B> pDelegate) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		if (activeSpan != null)
			mSpanContinuation = activeSpan.capture();
		else
			mSpanContinuation = null;
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public B apply(A pT) {
		if (mSpanContinuation == null)
			return mDelegate.apply(pT);
		try (@SuppressWarnings("null")
		ActiveSpan span = mSpanContinuation.activate()) {
			return mDelegate.apply(pT);
		}
	}

}

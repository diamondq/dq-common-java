package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerSupplier<A> implements Supplier<A> {

	private final @Nullable Continuation	mSpanContinuation;

	private final Supplier<A>				mDelegate;

	public TracerSupplier(Supplier<A> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerSupplier(Tracer pTracer, Supplier<A> pDelegate) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		if (activeSpan != null)
			mSpanContinuation = activeSpan.capture();
		else
			mSpanContinuation = null;
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.Supplier#get()
	 */
	@Override
	public A get() {
		if (mSpanContinuation == null)
			return mDelegate.get();
		try (@SuppressWarnings("null")
		ActiveSpan span = mSpanContinuation.activate()) {
			return mDelegate.get();
		}
	}

}

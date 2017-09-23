package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerConsumer<T> implements Consumer<T> {

	private final @Nullable Continuation	mSpanContinuation;

	private final Consumer<T>				mDelegate;

	public TracerConsumer(Consumer<T> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerConsumer(Tracer pTracer, Consumer<T> pDelegate) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		if (activeSpan != null)
			mSpanContinuation = activeSpan.capture();
		else
			mSpanContinuation = null;
		mDelegate = pDelegate;
	}

	@Override
	public void accept(T pT) {
		if (mSpanContinuation == null) {
			mDelegate.accept(pT);
			return;
		}
		try (@SuppressWarnings("null")
		ActiveSpan span = mSpanContinuation.activate()) {
			mDelegate.accept(pT);
		}
	}

}

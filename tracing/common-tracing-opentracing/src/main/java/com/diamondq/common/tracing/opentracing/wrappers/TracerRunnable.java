package com.diamondq.common.tracing.opentracing.wrappers;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerRunnable implements Runnable {

	private final @Nullable Continuation	mSpanContinuation;

	private final Runnable					mDelegate;

	public TracerRunnable(Runnable pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerRunnable(Tracer pTracer, Runnable pDelegate) {
		ActiveSpan activeSpan = pTracer.activeSpan();
		if (activeSpan != null)
			mSpanContinuation = activeSpan.capture();
		else
			mSpanContinuation = null;
		mDelegate = pDelegate;
	}

	@Override
	public void run() {
		if (mSpanContinuation == null) {
			mDelegate.run();
		}
		try (@SuppressWarnings("null")
		ActiveSpan span = mSpanContinuation.activate()) {
			mDelegate.run();
		}
	}

}

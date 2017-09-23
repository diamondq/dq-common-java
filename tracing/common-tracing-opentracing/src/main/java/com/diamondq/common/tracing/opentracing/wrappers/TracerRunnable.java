package com.diamondq.common.tracing.opentracing.wrappers;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerRunnable extends AbstractTracerWrapper implements Runnable, AbortableContinuation {

	private final Runnable mDelegate;

	public TracerRunnable(Runnable pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerRunnable(Tracer pTracer, Runnable pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c == null) {
			mDelegate.run();
			return;
		}
		try (ActiveSpan span = c.activate()) {
			mDelegate.run();
		}
	}

}

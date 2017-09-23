package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.Consumer;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerConsumer<T> extends AbstractTracerWrapper implements Consumer<T>, AbortableContinuation {

	private final Consumer<T> mDelegate;

	public TracerConsumer(Consumer<T> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerConsumer(Tracer pTracer, Consumer<T> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	@Override
	public void accept(T pT) {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c == null) {
			mDelegate.accept(pT);
			return;
		}
		try (ActiveSpan span = c.activate()) {
			mDelegate.accept(pT);
		}
	}

}

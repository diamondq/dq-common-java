package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.Supplier;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerSupplier<A> extends AbstractTracerWrapper implements Supplier<A>, AbortableContinuation {

	private final Supplier<A> mDelegate;

	public TracerSupplier(Supplier<A> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerSupplier(Tracer pTracer, Supplier<A> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.Supplier#get()
	 */
	@Override
	public A get() {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c == null)
			return mDelegate.get();
		try (ActiveSpan span = c.activate()) {
			return mDelegate.get();
		}
	}

}

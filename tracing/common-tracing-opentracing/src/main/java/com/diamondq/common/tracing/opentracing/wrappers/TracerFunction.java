package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableFunction;

import java.util.function.Function;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerFunction<A, B> extends AbstractTracerWrapper
	implements Function<A, B>, AbortableContinuation, CancelableFunction<A, B> {

	private final Function<A, B> mDelegate;

	public TracerFunction(Function<A, B> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerFunction(Tracer pTracer, Function<A, B> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public B apply(A pT) {
		Continuation c = mSpanContinuation.getAndSet(null);
		if (c == null)
			return mDelegate.apply(pT);
		try (ActiveSpan span = c.activate()) {
			return mDelegate.apply(pT);
		}
	}

	/**
	 * @see com.diamondq.common.lambda.interfaces.CancelableFunction#cancel()
	 */
	@Override
	public void cancel() {
		abortContinuation();
	}
}

package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerBiConsumer<A, B> extends AbstractTracerWrapper implements BiConsumer<A, B>, AbortableContinuation {

	private static final Logger		sLogger	= LoggerFactory.getLogger(TracerBiConsumer.class);

	private final BiConsumer<A, B>	mDelegate;

	public TracerBiConsumer(BiConsumer<A, B> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerBiConsumer(Tracer pTracer, BiConsumer<A, B> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void accept(A pA, B pB) {
		boolean inAccept = false;
		try {
			Continuation c = mSpanContinuation.getAndSet(null);
			if (c == null) {
				inAccept = true;
				mDelegate.accept(pA, pB);
				return;
			}
			try (ActiveSpan span = c.activate()) {
				inAccept = true;
				mDelegate.accept(pA, pB);
				inAccept = false;
			}
		}
		catch (RuntimeException ex) {
			if (inAccept == false)
				sLogger.error("Error during span activation or shutdown", ex);
			throw ex;
		}
	}

}

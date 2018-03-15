package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableBiFunction;

import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerBiFunction<A, B, C> extends AbstractTracerWrapper
	implements BiFunction<A, B, C>, CancelableBiFunction<A, B, C> {

	private static final Logger			sLogger	= LoggerFactory.getLogger(TracerBiFunction.class);

	private final BiFunction<A, B, C>	mDelegate;

	public TracerBiFunction(BiFunction<A, B, C> pDelegate) {
		this(GlobalTracer.get(), pDelegate);
	}

	public TracerBiFunction(Tracer pTracer, BiFunction<A, B, C> pDelegate) {
		super(pTracer);
		mDelegate = pDelegate;
	}

	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public C apply(A pA, B pB) {
		boolean inApply = false;
		try {
			Span c = mSpan;
			if (c == null) {
				inApply = true;
				return mDelegate.apply(pA, pB);
			}
			try (Scope scope = mScopeManager.activate(mSpan, false)) {
				inApply = true;
				C result = mDelegate.apply(pA, pB);
				inApply = false;
				return result;
			}
		}
		catch (RuntimeException ex) {
			if (inApply == false)
				sLogger.error("Error during span activation or shutdown", ex);
			throw ex;
		}
	}

	/**
	 * @see com.diamondq.common.lambda.interfaces.CancelableBiFunction#cancel()
	 */
	@Override
	public void cancel() {
		abortContinuation();
	}
}

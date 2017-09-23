package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

@Decorator
@Priority(1)
public abstract class TracerDecorator implements Tracer {

	private final Tracer			mTracer;

	private final TraceIdExtractor	mExtractor;

	@Inject
	public TracerDecorator(@Delegate Tracer pTracer, TraceIdExtractor pExtractor) {
		mTracer = pTracer;
		mExtractor = pExtractor;
		GlobalTracer.register(this);
	}

	/**
	 * @see io.opentracing.Tracer#buildSpan(java.lang.String)
	 */
	@Override
	public SpanBuilder buildSpan(String pOperationName) {
		return new MDCSpanBuilder(mTracer.buildSpan(pOperationName), mExtractor);
	}

	@Override
	public @Nullable ActiveSpan activeSpan() {
		ActiveSpan result = mTracer.activeSpan();
		if (result == null)
			return result;
		return new MDCActiveSpan(result, mExtractor);
	}
}

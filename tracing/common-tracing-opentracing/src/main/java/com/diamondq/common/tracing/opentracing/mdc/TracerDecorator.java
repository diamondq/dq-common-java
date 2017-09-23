package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;

@Decorator
@Priority(1)
public abstract class TracerDecorator implements Tracer {

	private final Tracer						mTracer;

	private final @Nullable TraceIdExtractor	mExtractor;

	@Inject
	public TracerDecorator(@Delegate Tracer pTracer, Instance<TraceIdExtractor> pExtractor) {
		mTracer = pTracer;
		mExtractor = pExtractor.isResolvable() == true ? pExtractor.get() : null;
	}

	/**
	 * @see io.opentracing.Tracer#buildSpan(java.lang.String)
	 */
	@Override
	public SpanBuilder buildSpan(String pOperationName) {
		TraceIdExtractor extractor = mExtractor;
		if (extractor == null)
			return mTracer.buildSpan(pOperationName);
		else
			return new MDCSpanBuilder(mTracer.buildSpan(pOperationName), extractor);
	}

	/**
	 * @see io.opentracing.ActiveSpanSource#activeSpan()
	 */
	@Override
	public @Nullable ActiveSpan activeSpan() {
		ActiveSpan result = mTracer.activeSpan();
		if (result == null)
			return null;
		TraceIdExtractor extractor = mExtractor;
		if (extractor == null)
			return result;
		else
			return new MDCActiveSpan(result, extractor);
	}
}

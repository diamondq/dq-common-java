package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer.SpanBuilder;

public class MDCSpanBuilder implements SpanBuilder {

	private final SpanBuilder		mDelegate;

	private final TraceIdExtractor	mExtractor;

	public MDCSpanBuilder(SpanBuilder pDelegate, TraceIdExtractor pExtractor) {
		mDelegate = pDelegate;
		mExtractor = pExtractor;
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#asChildOf(io.opentracing.SpanContext)
	 */
	@Override
	public SpanBuilder asChildOf(@Nullable SpanContext pParent) {
		SpanBuilder result = mDelegate.asChildOf(pParent);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#asChildOf(io.opentracing.Span)
	 */
	@Override
	public SpanBuilder asChildOf(@Nullable Span pParent) {
		SpanBuilder result = mDelegate.asChildOf(pParent);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#addReference(java.lang.String, io.opentracing.SpanContext)
	 */
	@Override
	public SpanBuilder addReference(String pReferenceType, @Nullable SpanContext pReferencedContext) {
		SpanBuilder result = mDelegate.addReference(pReferenceType, pReferencedContext);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#ignoreActiveSpan()
	 */
	@Override
	public SpanBuilder ignoreActiveSpan() {
		SpanBuilder result = mDelegate.ignoreActiveSpan();
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#withTag(java.lang.String, java.lang.String)
	 */
	@Override
	public SpanBuilder withTag(String pKey, @Nullable String pValue) {
		SpanBuilder result = mDelegate.withTag(pKey, pValue);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#withTag(java.lang.String, boolean)
	 */
	@Override
	public SpanBuilder withTag(String pKey, boolean pValue) {
		SpanBuilder result = mDelegate.withTag(pKey, pValue);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#withTag(java.lang.String, java.lang.Number)
	 */
	@Override
	public SpanBuilder withTag(String pKey, @Nullable Number pValue) {
		SpanBuilder result = mDelegate.withTag(pKey, pValue);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#withStartTimestamp(long)
	 */
	@Override
	public SpanBuilder withStartTimestamp(long pMicroseconds) {
		SpanBuilder result = mDelegate.withStartTimestamp(pMicroseconds);
		if (result == mDelegate)
			return this;
		return new MDCSpanBuilder(result, mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#startActive(boolean)
	 */
	@Override
	public Scope startActive(boolean pFinishSpanOnClose) {
		return new MDCScope(mDelegate.startActive(pFinishSpanOnClose), mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#startManual()
	 */
	@Override
	public Span startManual() {
		return new MDCSpan(mDelegate.startManual(), mExtractor);
	}

	/**
	 * @see io.opentracing.Tracer.SpanBuilder#start()
	 */
	@Deprecated
	@Override
	public Span start() {
		return new MDCSpan(mDelegate.start(), mExtractor);
	};

}

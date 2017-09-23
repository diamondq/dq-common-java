package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import io.opentracing.Span;

public class MDCSpan extends MDCBaseSpan<Span> implements Span {

	private final Span mDelegate;

	@SuppressWarnings("null")
	public MDCSpan(Span pDelegate, TraceIdExtractor pExtractor) {
		super(pDelegate, pExtractor);
		mDelegate = pDelegate;
	}

	/**
	 * @see io.opentracing.Span#finish()
	 */
	@Override
	public void finish() {
		mDelegate.finish();
	}

	/**
	 * @see io.opentracing.Span#finish(long)
	 */
	@Override
	public void finish(long pFinishMicros) {
		mDelegate.finish(pFinishMicros);
	}

}

package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import org.slf4j.MDC;

import io.opentracing.ActiveSpan;

public class MDCActiveSpan extends MDCBaseSpan<ActiveSpan> implements ActiveSpan {

	private final ActiveSpan mDelegate;

	@SuppressWarnings("null")
	public MDCActiveSpan(ActiveSpan pDelegate, TraceIdExtractor pExtractor) {
		super(pDelegate, pExtractor);
		mDelegate = pDelegate;
	}

	@Override
	public void deactivate() {
		mDelegate.deactivate();
	}

	@Override
	public void close() {
		mDelegate.close();
		MDC.remove("traceId");
	}

	public void cleanup() {
		MDC.remove("traceId");
	}

	@Override
	public Continuation capture() {
		return new MDCContinuation(mDelegate.capture(), mExtractor);
	}

}

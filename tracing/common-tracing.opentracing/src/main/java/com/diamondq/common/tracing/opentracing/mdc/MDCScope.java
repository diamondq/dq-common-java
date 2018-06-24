package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import org.slf4j.MDC;

import io.opentracing.Scope;
import io.opentracing.Span;

public class MDCScope implements Scope {

	private final Scope mDelegate;

	public MDCScope(Scope pDelegate, TraceIdExtractor pExtractor) {
		mDelegate = pDelegate;
	}

	@Override
	public void close() {
		mDelegate.close();
		MDC.remove("traceId");
	}

	public void cleanup() {
		MDC.remove("traceId");
	}

	/**
	 * @see io.opentracing.Scope#span()
	 */
	@Override
	public Span span() {
		return mDelegate.span();
	}

}

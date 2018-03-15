package com.diamondq.common.tracing.jaeger;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import javax.enterprise.context.ApplicationScoped;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.Span;
import io.opentracing.SpanContext;

@ApplicationScoped
public class JaegarExtractor implements TraceIdExtractor {

	public JaegarExtractor() {
	}

	@Override
	public @Nullable String getTraceId(@Nullable Span pSpan) {
		if (pSpan == null)
			return null;
		SpanContext context = pSpan.context();
		if ((context instanceof com.uber.jaeger.SpanContext) == false)
			return null;
		long traceId = ((com.uber.jaeger.SpanContext) context).getTraceId();
		return String.format("%x", traceId);
	}

}

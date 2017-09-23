package com.diamondq.common.tracing.jaeger;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import javax.enterprise.context.ApplicationScoped;

import io.opentracing.BaseSpan;

@ApplicationScoped
public class JaegarExtractor implements TraceIdExtractor {

	public JaegarExtractor() {
	}

	@Override
	public String getTraceId(BaseSpan<?> pSpan) {
		long traceId = ((com.uber.jaeger.SpanContext) pSpan.context()).getTraceId();
		return String.format("%x", traceId);
	}

}

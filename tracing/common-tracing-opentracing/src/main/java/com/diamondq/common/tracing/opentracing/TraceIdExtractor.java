package com.diamondq.common.tracing.opentracing;

import io.opentracing.BaseSpan;

public interface TraceIdExtractor {

	/**
	 * Get's a trace id from a given span
	 *
	 * @param pSpan the span
	 * @return the id
	 */
	public String getTraceId(BaseSpan<?> pSpan);
}

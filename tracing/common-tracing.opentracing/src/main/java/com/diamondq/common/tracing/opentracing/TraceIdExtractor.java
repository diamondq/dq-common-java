package com.diamondq.common.tracing.opentracing;

import io.opentracing.Span;
import org.jspecify.annotations.Nullable;

public interface TraceIdExtractor {

  /**
   * Get a trace id from a given span
   *
   * @param pSpan the span
   * @return the id
   */
  @Nullable
  String getTraceId(@Nullable Span pSpan);
}

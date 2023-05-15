package com.diamondq.common.tracing.opentracing;

import io.opentracing.Span;
import org.jetbrains.annotations.Nullable;

public interface TraceIdExtractor {

  /**
   * Get's a trace id from a given span
   *
   * @param pSpan the span
   * @return the id
   */
  public @Nullable String getTraceId(@Nullable Span pSpan);
}

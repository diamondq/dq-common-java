package com.diamondq.common.tracing.opentracing;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.Span;

public interface TraceIdExtractor {

  /**
   * Get's a trace id from a given span
   *
   * @param pSpan the span
   * @return the id
   */
  public @Nullable String getTraceId(@Nullable Span pSpan);
}

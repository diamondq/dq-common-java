package com.diamondq.common.tracing.jaeger;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import jakarta.enterprise.context.ApplicationScoped;
import org.jetbrains.annotations.Nullable;

@ApplicationScoped
public class JaegarExtractor implements TraceIdExtractor {

  public JaegarExtractor() {
  }

  @Override
  public @Nullable String getTraceId(@Nullable Span pSpan) {
    if (pSpan == null) return null;
    SpanContext context = pSpan.context();
    if ((context instanceof io.jaegertracing.internal.JaegerSpanContext) == false) return null;
    return ((io.jaegertracing.internal.JaegerSpanContext) context).getTraceId();
  }

}

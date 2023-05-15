package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Decorator
@Priority(1)
public abstract class TracerDecorator implements Tracer {

  private final Tracer mTracer;

  private final @Nullable TraceIdExtractor mExtractor;

  @Inject
  public TracerDecorator(@Delegate Tracer pTracer, Instance<TraceIdExtractor> pExtractor) {
    mTracer = pTracer;
    mExtractor = (pExtractor.isUnsatisfied() == false) && (pExtractor.isAmbiguous() == false) ? pExtractor.get() : null;
  }

  /**
   * @see io.opentracing.Tracer#buildSpan(java.lang.String)
   */
  @Override
  public SpanBuilder buildSpan(String pOperationName) {
    TraceIdExtractor extractor = mExtractor;
    if (extractor == null) return mTracer.buildSpan(pOperationName);
    else return new MDCSpanBuilder(mTracer.buildSpan(pOperationName), extractor);
  }

  /**
   * @see io.opentracing.Tracer#activeSpan()
   */
  @Override
  public @Nullable Span activeSpan() {
    Span result = mTracer.activeSpan();
    if (result == null) return null;
    TraceIdExtractor extractor = mExtractor;
    if (extractor == null) return result;
    else return new MDCSpan(result, extractor);
  }
}

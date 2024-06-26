package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.MDC;

import java.util.Map;

public class MDCSpan implements Span {

  private final Span mDelegate;

  protected final TraceIdExtractor mExtractor;

  public MDCSpan(Span pDelegate, TraceIdExtractor pExtractor) {
    mDelegate = pDelegate;
    mExtractor = pExtractor;
    String traceId = pExtractor.getTraceId(mDelegate);
    if (traceId != null) MDC.put("traceId", traceId);
  }

  @Override
  public SpanContext context() {
    return mDelegate.context();
  }

  @Override
  public Span setTag(@Nullable String pKey, @Nullable String pValue) {
    return mDelegate.setTag(pKey, pValue);
  }

  @Override
  public Span setTag(@Nullable String pKey, boolean pValue) {
    return mDelegate.setTag(pKey, pValue);
  }

  @Override
  public Span setTag(@Nullable String pKey, @Nullable Number pValue) {
    return mDelegate.setTag(pKey, pValue);
  }

  @Override
  public Span log(@Nullable Map<String, @NotNull ?> pFields) {
    return mDelegate.log(pFields);
  }

  @Override
  public Span log(long pTimestampMicroseconds, @Nullable Map<String, @NotNull ?> pFields) {
    return mDelegate.log(pTimestampMicroseconds, pFields);
  }

  @Override
  public Span log(@Nullable String pEvent) {
    return mDelegate.log(pEvent);
  }

  @Override
  public Span log(long pTimestampMicroseconds, @Nullable String pEvent) {
    return mDelegate.log(pTimestampMicroseconds, pEvent);
  }

  @Override
  public Span setBaggageItem(@Nullable String pKey, @Nullable String pValue) {
    return mDelegate.setBaggageItem(pKey, pValue);
  }

  @Override
  public @Nullable String getBaggageItem(@Nullable String pKey) {
    return mDelegate.getBaggageItem(pKey);
  }

  @Override
  public Span setOperationName(@Nullable String pOperationName) {
    return mDelegate.setOperationName(pOperationName);
  }

  /**
   * @see io.opentracing.Span#finish()
   */
  @Override
  public void finish() {
    mDelegate.finish();
  }

  /**
   * @see io.opentracing.Span#finish(long)
   */
  @Override
  public void finish(long pFinishMicros) {
    mDelegate.finish(pFinishMicros);
  }

  @Override
  public <T> Span setTag(Tag<T> pTag, T pValue) {
    return mDelegate.setTag(pTag, pValue);
  }

}

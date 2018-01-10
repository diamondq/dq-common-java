package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.MDC;

import io.opentracing.BaseSpan;
import io.opentracing.SpanContext;

public class MDCBaseSpan<S extends BaseSpan<S>> implements BaseSpan<S> {

	private final BaseSpan<S>			mDelegate;

	protected final TraceIdExtractor	mExtractor;

	public MDCBaseSpan(BaseSpan<S> pDelegate, TraceIdExtractor pExtractor) {
		mDelegate = pDelegate;
		mExtractor = pExtractor;
		String traceId = pExtractor.getTraceId(mDelegate);
		if (traceId != null)
			MDC.put("traceId", traceId);
	}

	@Override
	public SpanContext context() {
		return mDelegate.context();
	}

	@Override
	public S setTag(@Nullable String pKey, @Nullable String pValue) {
		return mDelegate.setTag(pKey, pValue);
	}

	@Override
	public S setTag(@Nullable String pKey, boolean pValue) {
		return mDelegate.setTag(pKey, pValue);
	}

	@Override
	public S setTag(@Nullable String pKey, @Nullable Number pValue) {
		return mDelegate.setTag(pKey, pValue);
	}

	@Override
	public S log(@SuppressWarnings("null") Map<String, ?> pFields) {
		return mDelegate.log(pFields);
	}

	@Override
	public S log(long pTimestampMicroseconds, @SuppressWarnings("null") Map<String, ?> pFields) {
		return mDelegate.log(pTimestampMicroseconds, pFields);
	}

	@Override
	public S log(@Nullable String pEvent) {
		return mDelegate.log(pEvent);
	}

	@Override
	public S log(long pTimestampMicroseconds, @Nullable String pEvent) {
		return mDelegate.log(pTimestampMicroseconds, pEvent);
	}

	@Override
	public S setBaggageItem(@Nullable String pKey, @Nullable String pValue) {
		return mDelegate.setBaggageItem(pKey, pValue);
	}

	@Override
	public @Nullable String getBaggageItem(@Nullable String pKey) {
		return mDelegate.getBaggageItem(pKey);
	}

	@Override
	public S setOperationName(@Nullable String pOperationName) {
		return mDelegate.setOperationName(pOperationName);
	}

	@Deprecated
	@Override
	public S log(@Nullable String pEventName, @Nullable Object pPayload) {
		return mDelegate.log(pEventName, pPayload);
	}

	@Deprecated
	@Override
	public S log(long pTimestampMicroseconds, @Nullable String pEventName, @Nullable Object pPayload) {
		return mDelegate.log(pTimestampMicroseconds, pEventName, pPayload);
	}
}

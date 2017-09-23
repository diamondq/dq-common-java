package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.TraceIdExtractor;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;

public class MDCContinuation implements Continuation {

	private final Continuation		mDelegate;

	private final TraceIdExtractor	mExtractor;

	public MDCContinuation(Continuation pCapture, TraceIdExtractor pExtractor) {
		mDelegate = pCapture;
		mExtractor = pExtractor;
	}

	/**
	 * @see io.opentracing.ActiveSpan.Continuation#activate()
	 */
	@Override
	public ActiveSpan activate() {
		return new MDCActiveSpan(mDelegate.activate(), mExtractor);
	}

}

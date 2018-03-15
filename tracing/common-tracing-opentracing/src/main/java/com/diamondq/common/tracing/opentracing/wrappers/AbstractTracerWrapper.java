package com.diamondq.common.tracing.opentracing.wrappers;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;

public abstract class AbstractTracerWrapper implements AbortableContinuation {

	protected final ScopeManager	mScopeManager;

	protected final @Nullable Span	mSpan;

	public AbstractTracerWrapper(Tracer pTracer) {
		mScopeManager = pTracer.scopeManager();
		Scope scope = pTracer.scopeManager().active();
		mSpan = scope != null ? scope.span() : null;
	}

	/**
	 * @see com.diamondq.common.tracing.opentracing.wrappers.AbortableContinuation#abortContinuation()
	 */
	@Override
	public void abortContinuation() {
		if (mSpan != null)
			mScopeManager.activate(mSpan, true).close();
	}
}

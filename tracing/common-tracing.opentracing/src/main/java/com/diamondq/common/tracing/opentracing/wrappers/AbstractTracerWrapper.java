package com.diamondq.common.tracing.opentracing.wrappers;

import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.jspecify.annotations.Nullable;

public abstract class AbstractTracerWrapper implements AbortableContinuation {

  protected final ScopeManager mScopeManager;

  protected final @Nullable Span mSpan;

  public AbstractTracerWrapper(Tracer pTracer) {
    mScopeManager = pTracer.scopeManager();
    mSpan = mScopeManager.activeSpan();
  }

  /**
   * @see com.diamondq.common.tracing.opentracing.wrappers.AbortableContinuation#abortContinuation()
   */
  @Override
  public void abortContinuation() {
    if (mSpan != null) mScopeManager.activate(mSpan).close();
  }
}

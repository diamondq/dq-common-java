package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.Consumer2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerConsumer2<A, B> extends AbstractTracerWrapper implements Consumer2<A, B>, AbortableContinuation {

  private static final Logger   sLogger = LoggerFactory.getLogger(TracerConsumer2.class);

  private final Consumer2<A, B> mDelegate;

  public TracerConsumer2(Consumer2<A, B> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerConsumer2(Tracer pTracer, Consumer2<A, B> pDelegate) {
    super(pTracer);
    mDelegate = pDelegate;
  }

  /**
   * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
   */
  @Override
  public void accept(A pA, B pB) {
    boolean inAccept = false;
    try {
      Span c = mSpan;
      if (c == null) {
        inAccept = true;
        mDelegate.accept(pA, pB);
        return;
      }
      try (Scope scope = mScopeManager.activate(mSpan)) {
        inAccept = true;
        mDelegate.accept(pA, pB);
        inAccept = false;
      }
    }
    catch (RuntimeException ex) {
      if (inAccept == false)
        sLogger.error("Error during span activation or shutdown", ex);
      throw ex;
    }
  }

}

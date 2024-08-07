package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.Consumer1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerConsumer1<T> extends AbstractTracerWrapper implements Consumer1<T>, AbortableContinuation {

  private static final Logger sLogger = LoggerFactory.getLogger(TracerConsumer1.class);

  private final Consumer1<T>  mDelegate;

  public TracerConsumer1(Consumer1<T> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerConsumer1(Tracer pTracer, Consumer1<T> pDelegate) {
    super(pTracer);
    mDelegate = pDelegate;
  }

  @Override
  public void accept(T pT) {
    boolean inAccept = false;
    try {
      Span c = mSpan;
      if (c == null) {
        inAccept = true;
        mDelegate.accept(pT);
        return;
      }
      try (Scope scope = mScopeManager.activate(c)) {
        inAccept = true;
        mDelegate.accept(pT);
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

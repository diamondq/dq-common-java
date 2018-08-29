package com.diamondq.common.tracing.opentracing.wrappers;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerConsumer<T> extends AbstractTracerWrapper implements Consumer<T>, AbortableContinuation {

  private static final Logger sLogger = LoggerFactory.getLogger(TracerConsumer.class);

  private final Consumer<T>   mDelegate;

  public TracerConsumer(Consumer<T> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerConsumer(Tracer pTracer, Consumer<T> pDelegate) {
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
      try (Scope scope = mScopeManager.activate(mSpan, false)) {
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

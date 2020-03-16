package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableSupplier;
import com.diamondq.common.lambda.interfaces.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerSupplier<A> extends AbstractTracerWrapper
  implements Supplier<A>, AbortableContinuation, CancelableSupplier<A> {

  private static final Logger sLogger = LoggerFactory.getLogger(TracerSupplier.class);

  private final Supplier<A>   mDelegate;

  public TracerSupplier(Supplier<A> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerSupplier(Tracer pTracer, Supplier<A> pDelegate) {
    super(pTracer);
    mDelegate = pDelegate;
  }

  /**
   * @see java.util.function.Supplier#get()
   */
  @Override
  public A get() {
    boolean inGet = false;
    try {
      Span c = mSpan;
      if (c == null) {
        inGet = true;
        return mDelegate.get();
      }
      try (Scope scope = mScopeManager.activate(c)) {
        inGet = true;
        A result = mDelegate.get();
        inGet = false;
        return result;
      }
    }
    catch (RuntimeException ex) {
      if (inGet == false)
        sLogger.error("Error during span activation or shutdown", ex);
      throw ex;
    }
  }

  /**
   * @see com.diamondq.common.lambda.interfaces.CancelableSupplier#cancel()
   */
  @Override
  public void cancel() {
    abortContinuation();
  }
}

package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableFunction2;
import com.diamondq.common.lambda.interfaces.Function2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerFunction2<A, B, C> extends AbstractTracerWrapper
  implements Function2<A, B, C>, CancelableFunction2<A, B, C> {

  private static final Logger      sLogger = LoggerFactory.getLogger(TracerFunction2.class);

  private final Function2<A, B, C> mDelegate;

  public TracerFunction2(Function2<A, B, C> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerFunction2(Tracer pTracer, Function2<A, B, C> pDelegate) {
    super(pTracer);
    mDelegate = pDelegate;
  }

  /**
   * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
   */
  @Override
  public C apply(A pA, B pB) {
    boolean inApply = false;
    try {
      Span c = mSpan;
      if (c == null) {
        inApply = true;
        return mDelegate.apply(pA, pB);
      }
      try (Scope scope = mScopeManager.activate(mSpan, false)) {
        inApply = true;
        C result = mDelegate.apply(pA, pB);
        inApply = false;
        return result;
      }
    }
    catch (RuntimeException ex) {
      if (inApply == false)
        sLogger.error("Error during span activation or shutdown", ex);
      throw ex;
    }
  }

  /**
   * @see com.diamondq.common.lambda.interfaces.CancelableFunction2#cancel()
   */
  @Override
  public void cancel() {
    abortContinuation();
  }
}

package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableFunction1;
import com.diamondq.common.lambda.interfaces.Function1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerFunction1<A, B> extends AbstractTracerWrapper
  implements Function1<A, B>, AbortableContinuation, CancelableFunction1<A, B> {

  private static final Logger   sLogger = LoggerFactory.getLogger(TracerFunction1.class);

  private final Function1<A, B> mDelegate;

  public TracerFunction1(Function1<A, B> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerFunction1(Tracer pTracer, Function1<A, B> pDelegate) {
    super(pTracer);
    mDelegate = pDelegate;
  }

  /**
   * @see java.util.function.Function#apply(java.lang.Object)
   */
  @Override
  public B apply(A pT) {
    boolean inApply = false;
    try {
      Span c = mSpan;
      if (c == null) {
        inApply = true;
        return mDelegate.apply(pT);
      }
      try (Scope scope = mScopeManager.activate(mSpan, false)) {
        inApply = true;
        B result = mDelegate.apply(pT);
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
   * @see com.diamondq.common.lambda.interfaces.CancelableFunction1#cancel()
   */
  @Override
  public void cancel() {
    abortContinuation();
  }
}

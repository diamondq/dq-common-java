package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableFunction;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerFunction<A, B> extends AbstractTracerWrapper
  implements Function<A, B>, AbortableContinuation, CancelableFunction<A, B> {

  private static final Logger  sLogger = LoggerFactory.getLogger(TracerFunction.class);

  private final Function<A, B> mDelegate;

  public TracerFunction(Function<A, B> pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerFunction(Tracer pTracer, Function<A, B> pDelegate) {
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
   * @see com.diamondq.common.lambda.interfaces.CancelableFunction#cancel()
   */
  @Override
  public void cancel() {
    abortContinuation();
  }
}

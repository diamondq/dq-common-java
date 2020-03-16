package com.diamondq.common.tracing.opentracing.wrappers;

import com.diamondq.common.lambda.interfaces.CancelableRunnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class TracerRunnable extends AbstractTracerWrapper
  implements Runnable, AbortableContinuation, CancelableRunnable {
  private static final Logger sLogger = LoggerFactory.getLogger(TracerRunnable.class);

  private final Runnable      mDelegate;

  public TracerRunnable(Runnable pDelegate) {
    this(GlobalTracer.get(), pDelegate);
  }

  public TracerRunnable(Tracer pTracer, Runnable pDelegate) {
    super(pTracer);
    mDelegate = pDelegate;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    boolean inRun = false;
    try {
      Span c = mSpan;
      if (c == null) {
        inRun = true;
        mDelegate.run();
        return;
      }
      try (Scope scope = mScopeManager.activate(c)) {
        inRun = true;
        mDelegate.run();
        inRun = false;
      }
    }
    catch (RuntimeException ex) {
      if (inRun == false)
        sLogger.error("Error during span activation or shutdown", ex);
      throw ex;
    }
  }

  /**
   * @see com.diamondq.common.lambda.interfaces.CancelableRunnable#cancel()
   */
  @Override
  public void cancel() {
    abortContinuation();
  }

}

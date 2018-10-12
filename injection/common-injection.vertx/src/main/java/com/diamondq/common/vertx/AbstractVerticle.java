package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.Future;

public class AbstractVerticle extends io.vertx.core.AbstractVerticle {

  protected boolean                                       mRunning       = false;

  private final ExtendedCompletableFuture<@Nullable Void> mRunningFuture = new ExtendedCompletableFuture<>();

  protected void setRunning(boolean pValue) {
    synchronized (this) {
      mRunning = pValue;
      if (mRunning == true)
        mRunningFuture.complete(null);
    }
  }

  /**
   * @see io.vertx.core.AbstractVerticle#start(io.vertx.core.Future)
   */
  @Override
  public void start(Future<@Nullable Void> startFuture) throws Exception {
    start();
    setRunning(true);
    startFuture.complete();
  }

  /**
   * @see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Future)
   */
  @Override
  public void stop(Future<@Nullable Void> stopFuture) throws Exception {
    stop();
    setRunning(false);
    stopFuture.complete();
  }

  public ExtendedCompletionStage<@Nullable Void> waitUntilRunning() {
    return mRunningFuture;
  }
}

package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import io.vertx.core.Promise;
import org.jetbrains.annotations.Nullable;

public class AbstractVerticle extends io.vertx.core.AbstractVerticle {

  protected boolean mVerticleRunning = false;

  protected final ExtendedCompletableFuture<@Nullable Void> mRunningFuture = new ExtendedCompletableFuture<>();

  protected void setRunning(boolean pValue) {
    synchronized (this) {
      mVerticleRunning = pValue;
      if (mVerticleRunning == true) mRunningFuture.complete(null);
    }
  }

  /**
   * @see io.vertx.core.AbstractVerticle#start(io.vertx.core.Promise)
   */
  @Override
  public void start(Promise<@Nullable Void> startFuture) throws Exception {
    start();
    setRunning(true);
    startFuture.complete();
  }

  /**
   * @see io.vertx.core.AbstractVerticle#stop(io.vertx.core.Promise)
   */
  @Override
  public void stop(Promise<@Nullable Void> stopFuture) throws Exception {
    stop();
    setRunning(false);
    stopFuture.complete();
  }

  public ExtendedCompletionStage<@Nullable Void> waitUntilRunning() {
    return mRunningFuture;
  }
}

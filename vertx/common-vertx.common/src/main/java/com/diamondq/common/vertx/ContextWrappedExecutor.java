package com.diamondq.common.vertx;

import java.util.concurrent.Executor;

import io.vertx.core.Context;

public class ContextWrappedExecutor implements Executor {

  private Context mVertxContext;

  public ContextWrappedExecutor(Context pVertxContext) {
    mVertxContext = pVertxContext;
  }

  @Override
  public void execute(Runnable pCommand) {
    mVertxContext.runOnContext((v) -> {
      pCommand.run();
    });
  }

}

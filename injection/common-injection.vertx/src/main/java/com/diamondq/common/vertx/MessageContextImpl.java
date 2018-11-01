package com.diamondq.common.vertx;

import com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

public class MessageContextImpl implements MessageContext {

  private final ContextExtendedCompletableFuture<@Nullable Void> mMessageProcessed;

  public MessageContextImpl() {
    mMessageProcessed = new VertxContextExtendedCompletableFuture<>();
  }

  /**
   * @see com.diamondq.common.vertx.MessageContext#processComplete()
   */
  @Override
  public void processComplete() {
    mMessageProcessed.complete(null);
  }

  public ContextExtendedCompletableFuture<@Nullable Void> getMessageProcessed() {
    return mMessageProcessed;
  }

}

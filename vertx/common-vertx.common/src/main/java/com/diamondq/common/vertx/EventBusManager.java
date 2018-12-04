package com.diamondq.common.vertx;

import com.diamondq.common.utils.context.ContextExtendedCompletionStage;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.eventbus.DeliveryOptions;

public interface EventBusManager {

  public static DeliveryOptions oneMonthTimeout() {
    return new DeliveryOptions().setSendTimeout(31L * 24L * 60L * 60L * 1000L);
  }

  /**
   * Sends a message to an address. Will queue the send if there is a backlog. Will only allow a fixed number of
   * messages to be pending before queuing.
   * 
   * @param pAddress
   * @param pToSend
   * @param pDeliveryOptions
   * @return a future indicating that the send was queued. The result is a future for the actual result.
   */
  public <I, R> ContextExtendedCompletionStage<ContextExtendedCompletionStage<R>> send(String pAddress, I pToSend,
    @Nullable DeliveryOptions pDeliveryOptions);

}

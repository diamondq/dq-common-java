package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;

import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

public interface EventBusManager {

  public static DeliveryOptions oneMonthTimeout() {
    return new DeliveryOptions().setSendTimeout(31L * 24L * 60L * 60L * 1000L);
  }

  /**
   * Register a message handler against an address and record it for ServiceDiscovery
   * 
   * @param pConsumeAddress the address
   * @param pName the name
   * @param pClass the class
   * @param pMessageHandler the handler
   * @return the registered consumer
   */
  public <T, R> ContextExtendedCompletionStage<MessageConsumer<T>> register(String pConsumeAddress, String pName,
    Class<?> pClass, Function2<T, MessageContext, ExtendedCompletionStage<R>> pMessageHandler);

  public <T, R> ContextExtendedCompletionStage<MessageConsumer<T>> register(String pConsumeAddress, String pName,
    Class<?> pClass, Function<T, ExtendedCompletionStage<R>> pMessageHandler);

  public <I, MI, MO, R> ContextExtendedCompletionStage<ContextExtendedCompletionStage<R>> send(String pAddress,
    I pToSend, @Nullable Function<I, MI> pConvertToMessage, @Nullable Function<Message<MO>, R> pConvertFromMessage,
    @Nullable DeliveryOptions pDeliveryOptions);

  public <I, MI, MO, R> ContextExtendedCompletionStage<ContextExtendedCompletionStage<List<R>>> sendList(
    String pAddress, Iterable<I> pToSend, @Nullable Function<I, MI> pConvertToMessage,
    @Nullable Function<Message<MO>, R> pConvertFromMessage, @Nullable DeliveryOptions pDeliveryOptions);
}

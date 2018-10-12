package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;

public class EventBusUtils {

  private static final long sONE_MONTH = 31L * 24L * 60L * 60L * 1000L;

  public static DeliveryOptions oneMonthTimeout() {
    return new DeliveryOptions().setSendTimeout(sONE_MONTH);
  }

  /**
   * Register a message handler against an address and record it for ServiceDiscovery
   * 
   * @param pVertx the Vertx
   * @param pServiceDiscovery the ServiceDiscovery
   * @param pConsumeAddress the address
   * @param pName the name
   * @param pClass the class
   * @param pMessageHandler the handler
   * @return the registered consumer
   */
  public static <T> ExtendedCompletionStage<MessageConsumer<T>> register(Vertx pVertx,
    ServiceDiscovery pServiceDiscovery, String pConsumeAddress, String pName, Class<?> pClass,
    Handler<Message<T>> pMessageHandler) {

    synchronized (EventBusUtils.class) {

      /* Register the handler as a consumer */

      MessageConsumer<T> consumer = pVertx.eventBus().consumer(pConsumeAddress, pMessageHandler);

      /* Create a ServiceDiscovery Record for the scanfolder */

      Record record = EventBusService.createRecord(pName, pConsumeAddress, pClass);

      JsonObject filter = new JsonObject().put("name", pName);

      /* See if the record has already been published */

      return VertxUtils.<JsonObject, @Nullable Record> callWithNull(pServiceDiscovery::getRecord, filter)
        .thenCompose((r) -> {

          /* If the record hasn't been published, then publish it */

          if (r == null) {

            return VertxUtils.<Record, Record> call(pServiceDiscovery::publish, record).thenApply((v) -> consumer);
          }
          else
            return ExtendedCompletableFuture.completedFuture(consumer);
        });
    }

  }

  public static <I, MI, MO, R> ExtendedCompletionStage<R> send(Vertx pVertx, String pAddress, I pToSend,
    @Nullable Function<I, MI> pConvertToMessage, @Nullable Function<Message<MO>, R> pConvertFromMessage,
    @Nullable DeliveryOptions pDeliveryOptions) {
    return EventBusUtils.<I, MI, MO, R> sendList(pVertx, pAddress, Collections.singletonList(pToSend),
      pConvertToMessage, pConvertFromMessage, pDeliveryOptions).thenApply((List<R> results) -> {
        R result = results.get(0);
        return result;
      });
  }

  public static <I, MI, MO, R> ExtendedCompletionStage<List<R>> sendList(Vertx pVertx, String pAddress,
    Iterable<I> pToSend, @Nullable Function<I, MI> pConvertToMessage,
    @Nullable Function<Message<MO>, R> pConvertFromMessage, @Nullable DeliveryOptions pDeliveryOptions) {

    /* Add to the queue */

    List<ExtendedCompletionStage<R>> resultList = new ArrayList<>();
    for (I toSend : pToSend) {

      Object message;
      if (pConvertToMessage == null)
        message = toSend;
      else
        message = pConvertToMessage.apply(toSend);

      ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
      resultList.add(future);

      DeliveryOptions options = pDeliveryOptions;
      if (options == null)
        options = new DeliveryOptions();
      pVertx.eventBus().send(pAddress, message, options, (ar) -> {
        if (ar.succeeded() == false) {
          Throwable cause = ar.cause();
          if (cause == null)
            cause = new RuntimeException();
          future.completeExceptionally(cause);
          // ContextFactory.staticReportTrace(EventBusUtils.class, EventBusUtils.class,
          // "Received error for ({}) address {}: {}", messageId, sendQueue.address, cause);
        }
        else {
          @SuppressWarnings("unchecked")
          Message<MO> replyMessage = (Message<MO>) ar.result();
          if (replyMessage == null)
            future.completeExceptionally(new IllegalArgumentException());
          else {
            R result;
            if (pConvertFromMessage == null) {
              @SuppressWarnings("unchecked")
              R tempResult = (R) replyMessage;
              result = tempResult;
            }
            else
              result = pConvertFromMessage.apply(replyMessage);
            // ContextFactory.staticReportTrace(EventBusUtils.class, EventBusUtils.class,
            // "Received reply ({}) from address {}: {}", messageId, sendQueue.address, result);
            future.complete(result);
          }
        }

        // ContextFactory.staticReportTrace(EventBusUtils.class, EventBusUtils.class,
        // "end sendOneMessage -> {} in flight / {} queue size", sendQueue.inflight.get(), sendQueue.queueSize.get());

      });
    }

    // ContextFactory.staticReportTrace(EventBusUtils.class, EventBusUtils.class,
    // "sendList -> {} in flight / {} queue size", sendQueue.inflight.get(), sendQueue.queueSize.get());
    return ExtendedCompletionStage.listOf(resultList);
  }
}

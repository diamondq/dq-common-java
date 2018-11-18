package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

public class OldEventBusUtils {

  private static final ConcurrentMap<String, SendQueue> sSendQueues         = new ConcurrentHashMap<>();

  private static final int                              sDEFAULT_QUEUE_SIZE = 100;

  private static int                                    sDEFAULT_INFLIGHT   =
    Runtime.getRuntime().availableProcessors() * 2;

  private static final AtomicLong                       sMessagesSent       = new AtomicLong();

  private static class TransitMessage<I, MI, MO, R> {
    public final I                                  toSend;

    public final @Nullable Function<I, MI>          convertToMessage;

    public final @Nullable Function<Message<MO>, R> convertFromMessage;

    public final ExtendedCompletableFuture<R>       resultFuture;

    public TransitMessage(I pToSend, @Nullable Function<I, MI> pConvertToMessage,
      @Nullable Function<Message<MO>, R> pConvertFromMessage, ExtendedCompletableFuture<R> pResultFuture) {
      super();
      toSend = pToSend;
      convertToMessage = pConvertToMessage;
      convertFromMessage = pConvertFromMessage;
      resultFuture = pResultFuture;
    }

  }

  private static class SendQueue {
    public final String                            address;

    public final Queue<TransitMessage<?, ?, ?, ?>> queue;

    public final AtomicInteger                     queueSize;

    public final AtomicInteger                     inflight;

    @SuppressWarnings("unused")
    public final int                               maxQueueSize;

    public final int                               maxInflight;

    public SendQueue(String pAddress, int pMaxQueueSize, int pMaxInflight) {
      address = pAddress;
      queue = new ConcurrentLinkedQueue<>();
      queueSize = new AtomicInteger(0);
      inflight = new AtomicInteger(0);
      maxQueueSize = pMaxQueueSize;
      maxInflight = pMaxInflight;
    }

  }

  public static <I, MI, MO, R> ExtendedCompletionStage<R> send(Vertx pVertx, String pAddress, I pToSend,
    @Nullable Function<I, MI> pConvertToMessage, @Nullable Function<Message<MO>, R> pConvertFromMessage) {
    return OldEventBusUtils.<I, MI, MO, R> sendList(pVertx, pAddress, Collections.singletonList(pToSend),
      pConvertToMessage, pConvertFromMessage).thenApply((List<R> results) -> {
        R result = results.get(0);
        return result;
      });
  }

  private static <I, MI, MO, R> void sendOneMessage(Vertx pVertx, SendQueue sendQueue) {
    /*
     * NOTE: This may cause the number of inflight to temporarily exceed the max in flight due to a race condition. This
     * isn't a major deal, and attempting to fix this just creates much more contention.
     */
    sendQueue.inflight.incrementAndGet();
    @SuppressWarnings("unchecked")
    TransitMessage<I, MI, MO, R> toProcess = (TransitMessage<I, MI, MO, R>) sendQueue.queue.poll();
    if (toProcess == null) {
      sendQueue.inflight.decrementAndGet();
    }
    else {
      sendQueue.queueSize.decrementAndGet();
      Object message;
      Function<I, MI> convertToMessage = toProcess.convertToMessage;
      if (convertToMessage == null)
        message = toProcess.toSend;
      else
        message = convertToMessage.apply(toProcess.toSend);
      long messageId = sMessagesSent.incrementAndGet();
      ContextFactory.staticReportTrace(OldEventBusUtils.class, OldEventBusUtils.class,
        "Vertx eventBus sending ({}) to {} -> {}", messageId, sendQueue.address, message);
      pVertx.eventBus().send(sendQueue.address, message, (ar) -> {
        sendQueue.inflight.decrementAndGet();
        if (ar.succeeded() == false) {
          Throwable cause = ar.cause();
          if (cause == null)
            cause = new RuntimeException();
          toProcess.resultFuture.completeExceptionally(cause);
          ContextFactory.staticReportTrace(OldEventBusUtils.class, OldEventBusUtils.class,
            "Received error for ({}) address {}: {}", messageId, sendQueue.address, cause);
        }
        else {
          @SuppressWarnings("unchecked")
          Message<MO> replyMessage = (Message<MO>) ar.result();
          if (replyMessage == null)
            toProcess.resultFuture.completeExceptionally(new IllegalArgumentException());
          else {
            R result;
            Function<Message<MO>, R> convertFromMessage = toProcess.convertFromMessage;
            if (convertFromMessage == null) {
              @SuppressWarnings("unchecked")
              R tempResult = (R) replyMessage;
              result = tempResult;
            }
            else
              result = convertFromMessage.apply(replyMessage);
            ContextFactory.staticReportTrace(OldEventBusUtils.class, OldEventBusUtils.class,
              "Received reply ({}) from address {}: {}", messageId, sendQueue.address, result);
            toProcess.resultFuture.complete(result);
          }
        }

        /* At this point, if there are still jobs to process, we can trigger them */

        while ((sendQueue.inflight.get() < sendQueue.maxInflight) && (sendQueue.queueSize.get() > 0)) {
          sendOneMessage(pVertx, sendQueue);
        }

        ContextFactory.staticReportTrace(OldEventBusUtils.class, OldEventBusUtils.class,
          "end sendOneMessage -> {} in flight / {} queue size", sendQueue.inflight.get(), sendQueue.queueSize.get());

      });
    }
  }

  public static <I, MI, MO, R> ExtendedCompletionStage<List<R>> sendList(Vertx pVertx, String pAddress,
    Iterable<I> pToSend, @Nullable Function<I, MI> pConvertToMessage,
    @Nullable Function<Message<MO>, R> pConvertFromMessage) {
    SendQueue tempQueue = sSendQueues.get(pAddress);
    if (tempQueue == null) {
      SendQueue newSendQueue = new SendQueue(pAddress, sDEFAULT_QUEUE_SIZE, sDEFAULT_INFLIGHT);
      if ((tempQueue = sSendQueues.putIfAbsent(pAddress, newSendQueue)) == null)
        tempQueue = newSendQueue;
    }
    final SendQueue sendQueue = tempQueue;

    /* Add to the queue */

    List<ExtendedCompletionStage<R>> resultList = new ArrayList<>();
    for (I toSend : pToSend) {
      sendQueue.queueSize.incrementAndGet();
      ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
      resultList.add(future);
      sendQueue.queue.add(new TransitMessage<I, MI, MO, R>(toSend, pConvertToMessage, pConvertFromMessage, future));
    }

    /* If there are not a maximum number of outstanding requests, then fill those */

    while ((sendQueue.inflight.get() < sendQueue.maxInflight) && (sendQueue.queueSize.get() > 0)) {
      sendOneMessage(pVertx, sendQueue);
    }

    ContextFactory.staticReportTrace(OldEventBusUtils.class, OldEventBusUtils.class,
      "sendList -> {} in flight / {} queue size", sendQueue.inflight.get(), sendQueue.queueSize.get());
    return ExtendedCompletionStage.listOf(resultList);
  }
}

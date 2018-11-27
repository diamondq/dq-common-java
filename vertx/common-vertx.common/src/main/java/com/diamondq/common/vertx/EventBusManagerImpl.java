package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletableFuture;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.Verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;

public class EventBusManagerImpl implements EventBusManager {

  private static final int sDEFAULT_QUEUE_SIZE = 100;

  private static final int sDEFAULT_INFLIGHT   = Runtime.getRuntime().availableProcessors() * 2;

  private static class TransitMessage<I, MI, MO, R> {
    public final I                                  toSend;

    public final @Nullable Function<I, MI>          convertToMessage;

    public final @Nullable Function<Message<MO>, R> convertFromMessage;

    public final ExtendedCompletableFuture<R>       resultFuture;

    public final MessageConsumer<MO>                processingComplete;

    public final @Nullable DeliveryOptions          deliveryOptions;

    public TransitMessage(I pToSend, @Nullable Function<I, MI> pConvertToMessage,
      @Nullable Function<Message<MO>, R> pConvertFromMessage, ExtendedCompletableFuture<R> pResultFuture,
      MessageConsumer<MO> pProcessingComplete, @Nullable DeliveryOptions pDeliveryOptions) {
      super();
      toSend = pToSend;
      convertToMessage = pConvertToMessage;
      convertFromMessage = pConvertFromMessage;
      resultFuture = pResultFuture;
      processingComplete = pProcessingComplete;
      deliveryOptions = pDeliveryOptions;
    }

  }

  private static class SendQueue {
    public final String                            address;

    public final Queue<TransitMessage<?, ?, ?, ?>> queue;

    public final AtomicInteger                     queueSize;

    public final AtomicInteger                     inflight;

    public final AtomicInteger                     pendingResults;

    @SuppressWarnings("unused")
    public final int                               maxQueueSize;

    public final int                               maxInflight;

    public SendQueue(MeterRegistry pMeterRegistry, String pAddress, int pMaxQueueSize, int pMaxInflight) {
      address = pAddress;
      queue = new ConcurrentLinkedQueue<>();
      queueSize = new AtomicInteger(0);
      inflight = new AtomicInteger(0);
      pendingResults = new AtomicInteger(0);
      pMeterRegistry.gauge("eventbus.queueSize." + pAddress, "", (v) -> queueSize.get());
      pMeterRegistry.gauge("eventbus.inflight." + pAddress, "", (v) -> inflight.get());
      pMeterRegistry.gauge("eventbus.pending." + pAddress, "", (v) -> pendingResults.get());
      maxQueueSize = pMaxQueueSize;
      maxInflight = pMaxInflight;
    }

  }

  private final ConcurrentMap<String, SendQueue> mSendQueues   = new ConcurrentHashMap<>();

  private final AtomicLong                       mMessagesSent = new AtomicLong();

  private ContextFactory                         mContextFactory;

  private Vertx                                  mVertx;

  private ServiceDiscovery                       mServiceDiscovery;

  private MeterRegistry                          mMeterRegistry;

  @SuppressWarnings("null")
  public EventBusManagerImpl() {
  }

  public void setVertx(Vertx pVertx) {
    ContextFactory.staticReportTrace(EventBusManagerImpl.class, this, pVertx);
    mVertx = pVertx;
  }

  public void setMeterRegistry(MeterRegistry pMeterRegistry) {
    ContextFactory.staticReportTrace(EventBusManagerImpl.class, this, pMeterRegistry);
    mMeterRegistry = pMeterRegistry;
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(EventBusManagerImpl.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  public void setServiceDiscovery(ServiceDiscovery pServiceDiscovery) {
    ContextFactory.staticReportTrace(EventBusManagerImpl.class, this, pServiceDiscovery);
    mServiceDiscovery = pServiceDiscovery;
  }

  public void onActivate() {
    Verify.notNullArg(mContextFactory, VertxMessages.EVENTBUSMANAGER_MISSING_DEPENDENCY, "contextFactory");
    try (Context ctx = mContextFactory.newContext(EventBusManagerImpl.class, this)) {
      Verify.notNullArg(mVertx, VertxMessages.EVENTBUSMANAGER_MISSING_DEPENDENCY, "vertx");
      Verify.notNullArg(mMeterRegistry, VertxMessages.EVENTBUSMANAGER_MISSING_DEPENDENCY, "meterRegistry");
      Verify.notNullArg(mServiceDiscovery, VertxMessages.EVENTBUSMANAGER_MISSING_DEPENDENCY, "serviceDiscovery");
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(EventBusManagerImpl.class, this, ex);
    }
  }

  /**
   * @see com.diamondq.common.vertx.EventBusManager#register(java.lang.String, java.lang.String, java.lang.Class,
   *      com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <T, R> ContextExtendedCompletionStage<MessageConsumer<T>> register(String pConsumeAddress, String pName,
    Class<?> pClass, Function2<T, MessageContext, ExtendedCompletionStage<R>> pMessageHandler) {
    try (Context ctx =
      mContextFactory.newContext(EventBusManagerImpl.class, this, pConsumeAddress, pName, pClass, pMessageHandler)) {

      /* Register the handler as a consumer */

      Handler<Message<T>> handler = (m) -> {
        try (Context ctx2 = mContextFactory.newContext(EventBusManagerImpl.class, EventBusManagerImpl.this, m)) {
          MessageContextImpl messageContext = new MessageContextImpl();
          ExtendedCompletableFuture<@Nullable Void> messageProcessed = messageContext.getMessageProcessed();
          messageProcessed.thenAccept((v) -> {
            m.reply(Boolean.TRUE);
          });
          @Nullable
          T data = m.body();
          if (data == null)
            throw new IllegalArgumentException();
          pMessageHandler.apply(data, messageContext).thenApply((r) -> {

            /*
             * If the overall handler is complete, and we haven't yet seen a message complete, then just indicate now
             * that the message is complete
             */

            if (messageProcessed.isDone() == false)
              messageProcessed.complete(null);

            return r;
          })

            .whenComplete((r, ex) -> {
              String replyAddress = m.headers().get("dq-reply-address");
              if (replyAddress == null)
                throw new IllegalArgumentException();
              if (ex != null) {
                ContextFactory.currentContext().reportThrowable(ex);
                mVertx.eventBus().send(replyAddress, ex);
              }
              else {
                mVertx.eventBus().send(replyAddress, r);
              }
            });
        }
      };
      return internalRegister(pConsumeAddress, pName, pClass, handler);
    }
  }

  /**
   * @see com.diamondq.common.vertx.EventBusManager#register(java.lang.String, java.lang.String, java.lang.Class,
   *      java.util.function.Function)
   */
  @Override
  public <T, R> ContextExtendedCompletionStage<MessageConsumer<T>> register(String pConsumeAddress, String pName,
    Class<?> pClass, Function<T, ExtendedCompletionStage<R>> pMessageHandler) {
    try (Context ctx =
      mContextFactory.newContext(EventBusManagerImpl.class, this, pConsumeAddress, pName, pClass, pMessageHandler)) {
      /* Register the handler as a consumer */

      Handler<Message<T>> handler = (m) -> {
        try (Context ctx2 = mContextFactory.newContext(EventBusManagerImpl.class, EventBusManagerImpl.this, m)) {
          @Nullable
          T data = m.body();
          if (data == null)
            throw new IllegalArgumentException();
          pMessageHandler.apply(data).whenComplete((r, ex) -> {
            if (ex != null) {
              ContextFactory.currentContext().reportThrowable(ex);
              m.fail(-1, ex.getMessage());
            }
            else {
              try {
                m.reply(Boolean.TRUE);
                String replyAddress = m.headers().get("dq-reply-address");
                if (replyAddress != null)
                  mVertx.eventBus().send(replyAddress, r);
              }
              catch (RuntimeException ex2) {
                ContextFactory.currentContext().reportThrowable(ex2);
                m.fail(-1, ex2.getMessage());
              }
            }
          });
        }
      };
      return internalRegister(pConsumeAddress, pName, pClass, handler);
    }
  }

  private <T, R> ContextExtendedCompletionStage<MessageConsumer<T>> internalRegister(String pConsumeAddress,
    String pName, Class<?> pClass, Handler<Message<T>> pHandler) {
    try (Context ctx =
      mContextFactory.newContext(EventBusManagerImpl.class, this, pConsumeAddress, pName, pClass, pHandler)) {
      synchronized (EventBusManagerImpl.class) {

        MessageConsumer<T> consumer = mVertx.eventBus().consumer(pConsumeAddress, pHandler);

        /* Create a ServiceDiscovery Record for the consumer */

        Record record = EventBusService.createRecord(pName, pConsumeAddress, pClass);

        JsonObject filter = new JsonObject().put("name", pName);

        /* See if the record has already been published */

        return VertxUtils.<JsonObject, @Nullable Record> callReturnsNullable(mServiceDiscovery::getRecord, filter)
          .thenCompose((r) -> {

            /* If the record hasn't been published, then publish it */

            if (r == null) {

              return VertxUtils.<Record, Record> call(mServiceDiscovery::publish, record).thenApply((v) -> consumer);
            }
            else
              return ExtendedCompletableFuture.completedFuture(consumer);
          });
      }
    }
  }

  @Override
  public <I, MI, MO, R> ContextExtendedCompletionStage<ContextExtendedCompletionStage<R>> send(String pAddress,
    I pToSend, @Nullable Function<I, MI> pConvertToMessage, @Nullable Function<Message<MO>, R> pConvertFromMessage,
    @Nullable DeliveryOptions pDeliveryOptions) {

    try (Context ctx = mContextFactory.newContext(EventBusManagerImpl.class, this, pAddress, pToSend, pConvertToMessage,
      pConvertFromMessage, pDeliveryOptions)) {

      /* This simply calls the sendList with the body as a single valued list */

      ctx.prepareForAlternateThreads();
      return this
        .<I, MI, MO, R> sendList(pAddress, Collections.singletonList(pToSend), pConvertToMessage, pConvertFromMessage,
          pDeliveryOptions)

        /* Convert the return */

        .thenApply((futureResults) -> {
          try (Context ctx2 = ctx.activateOnThread("after sentList")) {
            ctx2.prepareForAlternateThreads();
            ctx2.prepareForAlternateThreads();
            return futureResults.thenApply((List<R> results) -> {
              try (Context ctx3 = ctx2.activateOnThread("With reply", results)) {
                R result = results.get(0);
                return result;
              }
            });
          }

        })

        /* Make sure the context is closed when we're done */

        .whenComplete((v, ex) -> {
          try (Context ctx3 = ctx.activateOnThread("send complete: {} or {}", v, ex)) {
            if (ex != null)
              ctx.forceClose();
          }
        });
    }
  }

  private <I, MI, MO, R> void sendOneMessage(SendQueue sendQueue) {
    try (Context ctx = mContextFactory.newContext(EventBusManagerImpl.class, this, sendQueue)) {

      /*
       * NOTE: This may cause the number of inflight to temporarily exceed the max in flight due to a race condition.
       * This isn't a major deal, and attempting to fix this just creates much more contention.
       */
      sendQueue.inflight.incrementAndGet();
      @SuppressWarnings("unchecked")
      TransitMessage<I, MI, MO, R> toProcess = (TransitMessage<I, MI, MO, R>) sendQueue.queue.poll();
      if (toProcess == null) {
        sendQueue.inflight.decrementAndGet();
      }
      else {
        sendQueue.queueSize.decrementAndGet();

        /* Build the actual message to send */

        Object message;
        Function<I, MI> convertToMessage = toProcess.convertToMessage;
        if (convertToMessage == null)
          message = toProcess.toSend;
        else
          message = convertToMessage.apply(toProcess.toSend);
        long messageId = mMessagesSent.incrementAndGet();
        DeliveryOptions options = toProcess.deliveryOptions;
        if (options == null)
          options = new DeliveryOptions();
        options.addHeader("dq-reply-address", toProcess.processingComplete.address());

        sendQueue.pendingResults.incrementAndGet();

        toProcess.processingComplete.handler((mr) -> {

          sendQueue.pendingResults.decrementAndGet();

          R result;
          Function<Message<MO>, R> convertFromMessage = toProcess.convertFromMessage;
          if (convertFromMessage == null) {
            @SuppressWarnings("unchecked")
            R tempResult = (R) mr.body();
            result = tempResult;
          }
          else
            result = convertFromMessage.apply(mr);
          ContextFactory.staticReportTrace(EventBusManagerImpl.class, EventBusManagerImpl.class,
            "Received reply ({}) from address {}: {}", messageId, sendQueue.address, result);
          toProcess.processingComplete.unregister();
          toProcess.resultFuture.complete(result);
        });

        /* Send the message */

        ctx.prepareForAlternateThreads();
        mVertx.eventBus().<Boolean> send(sendQueue.address, message, options, (ar) -> {
          try (Context ctx2 = ctx.activateOnThread("after Vertx.send: {}", ar)) {
            sendQueue.inflight.decrementAndGet();
            if (ar.succeeded() == false) {
              Throwable cause = ar.cause();
              if (cause == null)
                cause = new RuntimeException();
              sendQueue.pendingResults.decrementAndGet();
              toProcess.processingComplete.unregister();
              toProcess.resultFuture.completeExceptionally(cause);
              ctx2.error("Received error for ({}) address {}: {}", messageId, sendQueue.address, cause);
            }

            /* At this point, if there are still jobs to process, we can trigger them */

            while ((sendQueue.inflight.get() < sendQueue.maxInflight) && (sendQueue.queueSize.get() > 0)) {
              sendOneMessage(sendQueue);
            }

            ctx2.trace("end sendOneMessage -> {} in flight / {} queue size", sendQueue.inflight.get(),
              sendQueue.queueSize.get());
          }
        });
      }
    }

  }

  @Override
  public <I, MI, MO, R> ContextExtendedCompletionStage<ContextExtendedCompletionStage<List<R>>> sendList(
    String pAddress, Iterable<I> pToSend, @Nullable Function<I, MI> pConvertToMessage,
    @Nullable Function<Message<MO>, R> pConvertFromMessage, @Nullable DeliveryOptions pDeliveryOptions) {
    try (Context ctx = mContextFactory.newContext(EventBusManagerImpl.class, this, pAddress, pToSend, pConvertToMessage,
      pConvertFromMessage, pDeliveryOptions)) {

      /* Get the SendQueue for the given address */

      SendQueue tempQueue = mSendQueues.get(pAddress);
      if (tempQueue == null) {

        /* Create a new SendQueue */

        SendQueue newSendQueue = new SendQueue(mMeterRegistry, pAddress, sDEFAULT_QUEUE_SIZE, sDEFAULT_INFLIGHT);
        if ((tempQueue = mSendQueues.putIfAbsent(pAddress, newSendQueue)) == null)
          tempQueue = newSendQueue;
      }
      final SendQueue sendQueue = tempQueue;

      /* Add to the queue */

      List<ExtendedCompletionStage<R>> resultList = new ArrayList<>();
      for (I toSend : pToSend) {
        sendQueue.queueSize.incrementAndGet();
        ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
        resultList.add(future);
        String processingCompleteAddress = "DQ-" + UUID.randomUUID().toString();
        sendQueue.queue.add(new TransitMessage<I, MI, MO, R>(toSend, pConvertToMessage, pConvertFromMessage, future,
          mVertx.eventBus().consumer(processingCompleteAddress), pDeliveryOptions));
      }

      /* If there are not a maximum number of outstanding requests, then fill those */

      while ((sendQueue.inflight.get() < sendQueue.maxInflight) && (sendQueue.queueSize.get() > 0)) {
        sendOneMessage(sendQueue);
      }

      ctx.trace("sendList -> {} in flight / {} queue size", sendQueue.inflight.get(), sendQueue.queueSize.get());
      ContextExtendedCompletableFuture<Object> holderForRelated = FutureUtils.newCompletableFuture();
      return holderForRelated.relatedCompletedFuture(holderForRelated.relatedListOf(resultList));
    }
  }
}

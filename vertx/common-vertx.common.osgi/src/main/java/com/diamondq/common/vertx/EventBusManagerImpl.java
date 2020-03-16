package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletableFuture;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.Verify;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.servicediscovery.ServiceDiscovery;

public class EventBusManagerImpl implements EventBusManager {

  @SuppressWarnings("unused")
  private static final int sDEFAULT_QUEUE_SIZE = 100;

  private static final int sDEFAULT_INFLIGHT   = Runtime.getRuntime().availableProcessors() * 2;

  private static class TransitMessage<I, R> {
    @SuppressWarnings("unused")
    public final I                                                                   toSend;

    @SuppressWarnings("unused")
    public final @Nullable DeliveryOptions                                           deliveryOptions;

    @SuppressWarnings("unused")
    public final ContextExtendedCompletableFuture<ContextExtendedCompletionStage<R>> sendFuture;

    public TransitMessage(I pToSend, @Nullable DeliveryOptions pDeliveryOptions,
      ContextExtendedCompletableFuture<ContextExtendedCompletionStage<R>> pSendFuture) {
      super();
      toSend = pToSend;
      sendFuture = pSendFuture;
      deliveryOptions = pDeliveryOptions;
    }

  }

  private static class SendQueue {
    public final String                      address;

    public final Queue<TransitMessage<?, ?>> queue;

    /**
     * We'll track the number of entries in queue separately to make it easier to report metrics
     */
    public int                               queueSize;

    /**
     * How many messages are actually inflight (ie. waiting for results). If this number exceeds maxInFlight, then we
     * start queuing the requests.
     */
    public int                               inflight;

    public final int                         maxInflight;

    public SendQueue(MeterRegistry pMeterRegistry, String pAddress, int pMaxInflight) {
      address = pAddress;
      queue = new ConcurrentLinkedQueue<>();
      queueSize = 0;
      inflight = 0;
      pMeterRegistry.gauge("eventbus.queueSize." + pAddress, "", (v) -> {
        synchronized (SendQueue.this) {
          return SendQueue.this.queueSize;
        }
      });
      pMeterRegistry.gauge("eventbus.inflight." + pAddress, "", (v) -> {
        synchronized (SendQueue.this) {
          return SendQueue.this.inflight;
        }
      });
      maxInflight = pMaxInflight;
    }

  }

  private final ConcurrentMap<String, SendQueue> mSendQueues   = new ConcurrentHashMap<>();

  @SuppressWarnings("unused")
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
   * @see com.diamondq.common.vertx.EventBusManager#send(java.lang.String, java.lang.Object,
   *      io.vertx.core.eventbus.DeliveryOptions)
   */
  @Override
  public <I, R> ContextExtendedCompletionStage<ContextExtendedCompletionStage<R>> send(String pAddress, I pToSend,
    @Nullable DeliveryOptions pDeliveryOptions) {

    try (
      Context ctx = mContextFactory.newContext(EventBusManagerImpl.class, this, pAddress, pToSend, pDeliveryOptions)) {

      /* Get the SendQueue for the given address */

      SendQueue sendQueue = mSendQueues.get(pAddress);
      if (sendQueue == null) {

        /* Create a new SendQueue */

        SendQueue newSendQueue = new SendQueue(mMeterRegistry, pAddress, sDEFAULT_INFLIGHT);
        if ((sendQueue = mSendQueues.putIfAbsent(pAddress, newSendQueue)) == null)
          sendQueue = newSendQueue;
      }

      ContextExtendedCompletableFuture<ContextExtendedCompletionStage<R>> result = FutureUtils.newCompletableFuture();

      @Nullable
      SendQueue toSendQueue = null;

      synchronized (sendQueue) {

        /* If the number of inflight exceeds the maxInFlight, then queue */

        if (sendQueue.inflight >= sendQueue.maxInflight) {

          /* Add to the queue */

          sendQueue.queueSize++;
          sendQueue.queue.add(new TransitMessage<I, R>(pToSend, pDeliveryOptions, result));

        }
        else {

          /* Otherwise, just send it */

          sendQueue.inflight++;
          toSendQueue = sendQueue;
        }

        ctx.trace("send -> {} in flight / {} queue size", sendQueue.inflight, sendQueue.queueSize);
      }

      if (toSendQueue != null)
        result.complete(sendOneMessage(toSendQueue, pToSend, pDeliveryOptions));

      return result;
    }
  }

  @SuppressWarnings({"deprecation", "null"})
  private <I, R> ContextExtendedCompletionStage<R> sendOneMessage(SendQueue sendQueue, I pToSend,
    @Nullable DeliveryOptions pDeliveryOptions) {
    try (Context ctx = mContextFactory.newContext(EventBusManagerImpl.class, this, sendQueue)) {

      DeliveryOptions options = pDeliveryOptions;
      if (options == null)
        options = new DeliveryOptions();

      //
      // toProcess.processingComplete.handler((mr) -> {
      //
      // sendQueue.pendingResults.decrementAndGet();
      //
      // @SuppressWarnings("null")
      // R result = mr.body();
      //
      // ContextFactory.staticReportTrace(EventBusManagerImpl.class, EventBusManagerImpl.class,
      // "Received reply ({}) from address {}: {}", messageId, sendQueue.address, result);
      // toProcess.processingComplete.unregister();
      // toProcess.resultFuture.complete(result);
      // });
      //
      /* Send the message */

      VertxUtils.<String, I, DeliveryOptions, Message<R>> call(mVertx.eventBus()::send, sendQueue.address, pToSend,
        options);

      ctx.prepareForAlternateThreads();
      mVertx.eventBus().<Boolean> send(sendQueue.address, pToSend, options, (ar) -> {
        try (Context ctx2 = ctx.activateOnThread("after Vertx.send: {}", ar)) {
          synchronized (sendQueue) {
            sendQueue.inflight--;
          }
          if (ar.succeeded() == false) {
            Throwable cause = ar.cause();
            if (cause == null)
              cause = new RuntimeException();
//            sendQueue.pendingResults.decrementAndGet();
//            toProcess.processingComplete.unregister();
//            toProcess.resultFuture.completeExceptionally(cause);
//            ctx2.error("Received error for ({}) address {}: {}", messageId, sendQueue.address, cause);
          }

          /* At this point, if there are still jobs to process, we can trigger them */

//          while ((sendQueue.inflight.get() < sendQueue.maxInflight) && (sendQueue.queueSize.get() > 0)) {
//            sendOneMessage(sendQueue);
//          }

//          ctx2.trace("end sendOneMessage -> {} in flight / {} queue size", sendQueue.inflight.get(),
//            sendQueue.queueSize.get());
        }
      });
      return null;
    }

  }

}

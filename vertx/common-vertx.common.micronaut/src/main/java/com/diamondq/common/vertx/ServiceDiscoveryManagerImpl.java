package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletableFuture;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@Singleton
public class ServiceDiscoveryManagerImpl implements ServiceDiscoveryManager {

  private final ConcurrentMap<String, Record> mCachedRecords = new ConcurrentHashMap<>();

  private final ContextFactory                mContextFactory;

  private final ServiceDiscovery              mServiceDiscovery;

  private final List<Consumer<Record>>        mCallbacks     = new CopyOnWriteArrayList<>();

  private final ScheduledExecutorService      mScheduledExecutorService;

  @Inject
  public ServiceDiscoveryManagerImpl(ScheduledExecutorService pScheduledExecutorService, ContextFactory pContextFactory,
    ServiceDiscovery pServiceDiscovery, Vertx pVertx) {

    mContextFactory = pContextFactory;
    try (Context ctx = mContextFactory.newContext(ServiceDiscoveryManagerImpl.class, this)) {
      mServiceDiscovery = pServiceDiscovery;
      mScheduledExecutorService = pScheduledExecutorService;

      /* Register a consumer for any new record events */

      pVertx.eventBus().localConsumer("vertx.discovery.announce", (message) -> {
        JsonObject body = (JsonObject) message.body();
        Record record = new Record(body);

        Status status = record.getStatus();
        String id = record.getRegistration();
        if (id != null) {
          if (status == Status.UP) {
            mCachedRecords.put(id, record);
            for (Consumer<Record> callback : mCallbacks) {
              callback.accept(record);
            }
          }
          else {
            mCachedRecords.remove(id);
          }
        }
      });
    }
  }

  /**
   * @see com.diamondq.common.vertx.ServiceDiscoveryManager#lookupService(io.vertx.core.Vertx, java.lang.Class,
   *      java.lang.String)
   */
  @Override
  public <T> ContextExtendedCompletionStage<T> lookupService(Vertx pVertx, Class<T> pClass, String pName) {
    try (Context ctx = mContextFactory.newContext(ServiceDiscoveryManagerImpl.class, this, pClass, pName)) {
      try {
        ContextExtendedCompletableFuture<T> holderFuture = FutureUtils.newCompletableFuture();

        /* First, lookup directly */

        ContextExtendedCompletableFuture<Record> recordFuture = FutureUtils.newCompletableFuture();
        Consumer<Record> callback = (r) -> {
          String name = r.getName();
          if ((name != null) && (name.equals(pName)))
            recordFuture.complete(r);
        };
        mCallbacks.add(callback);
        JsonObject filter = new JsonObject().put("name", pName);
        VertxUtils.<JsonObject, @Nullable Record> callReturnsNullable(mServiceDiscovery::getRecord, filter)

          /* Check the result */

          .thenCompose((r) -> {

            if (r == null) {

              /* If it wasn't found, then we'll wait until timeout for the announcement to occur */

              return recordFuture.orTimeoutAsync(30, TimeUnit.SECONDS, mScheduledExecutorService);
            }
            else
              return FutureUtils.completedFuture(r);
          })

          .thenApply((r) -> {

            /* Now resolve the reference */

            String address = Objects.requireNonNull(r.getLocation()).getString("endpoint");

            T result = new ServiceProxyBuilder(pVertx).setAddress(address).build(pClass);
            return result;
          })

          /* Handle an exception */

          .whenComplete((result, ex, ctx2) -> {
            if (ex != null) {
              holderFuture.completeExceptionally(ex);
              return;
            }
            holderFuture.complete(result);
          });

        return holderFuture;
      }
      catch (RuntimeException ex) {
        throw ctx.reportThrowable(ex);
      }
    }
  }

}

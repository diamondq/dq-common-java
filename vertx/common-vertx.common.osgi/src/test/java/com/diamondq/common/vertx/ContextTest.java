package com.diamondq.common.vertx;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.context.impl.ContextFactoryImpl;
import com.diamondq.common.context.impl.logging.LoggingContextHandler;
import com.diamondq.common.injection.osgi.impl.ExecutorServiceProvider;
import com.diamondq.common.injection.osgi.impl.ScheduledExecutorServiceProvider;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.metrics.micrometer.SLF4JReporter;
import com.diamondq.common.metrics.micrometer.SimpleMeterRegistryProvider;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

public class ContextTest {

  private OsgiContext mContext;

  @SuppressWarnings("null")
  public ContextTest() {
  }

  @SuppressWarnings("null")
  @Before
  public void setup() {
    mContext = new OsgiContext();
    mContext.registerInjectActivateService(new LoggingContextHandler());
    ContextFactoryImpl contextFactory = new ContextFactoryImpl();
    mContext.registerInjectActivateService(contextFactory);
    mContext.registerInjectActivateService(new ScheduledExecutorServiceProvider());
    mContext.registerInjectActivateService(new ExecutorServiceProvider());
    mContext.registerInjectActivateService(new SimpleMeterRegistryProvider());
    Map<String, Object> props = new HashMap<>();
    props.put(".blockedThreadCheckInterval", "5");
    props.put(".blockedThreadCheckIntervalUnit", TimeUnit.MINUTES.toString());
    mContext.registerInjectActivateService(new VertxProvider(), props);
    mContext.registerInjectActivateService(new ServiceDiscoveryProvider());
    mContext.registerInjectActivateService(new EventBusRecordRegistrator());
    mContext.registerInjectActivateService(new EventBusManagerImpl());
    mContext.registerInjectActivateService(new SLF4JReporter());
  }

  @Test
  public void test() throws InterruptedException, ExecutionException {
    ContextFactory contextFactory = mContext.getService(ContextFactory.class);
    assertNotNull(contextFactory);
    ExtendedCompletableFuture<@Nullable Void> f;
    try (Context ctx = contextFactory.newContext(ContextTest.class, this)) {
      // ContextClass ctxClass = (ContextClass) ctx;

      ctx.trace("Within outer");
      ExtendedCompletableFuture<String> initial = new VertxContextExtendedCompletableFuture<String>();
      f = initial.thenAccept((s) -> {
        ctx.trace("within Apply");

        try (Context ctx2 = contextFactory.newContext(ContextTest.class, this, "Internal context")) {
          ctx2.trace("within internal context");
        }
      });

      ExecutorService executorService = mContext.getService(ExecutorService.class);
      assertNotNull(executorService);
      executorService.submit(() -> {
        try {
          Thread.sleep(1000L);
        }
        catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
        initial.complete("Test");
      });

    }
    f.get();
  }

}

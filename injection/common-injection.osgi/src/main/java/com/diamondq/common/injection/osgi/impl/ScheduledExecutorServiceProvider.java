package com.diamondq.common.injection.osgi.impl;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * NOTE: Scheduled Executor Service's are not intended for long running tasks. The service will generally only run the
 * core-pool-size # of threads at a time. Additional tasks will simply queue until one is available.
 */
public class ScheduledExecutorServiceProvider extends AbstractOSGiConstructor {

  public ScheduledExecutorServiceProvider() {
    super(ConstructorInfoBuilder.builder().constructorClass(ScheduledExecutorServiceProvider.class) //
      .register(ScheduledExecutorService.class).register(ExecutorService.class) //
      .factoryMethod("create").cArg().type(Integer.class).prop(".executors.core-pool-size").optional().build());
  }

  public ScheduledExecutorService create(Integer pCorePoolSize) {
    int corePoolSize = (pCorePoolSize == null ? Runtime.getRuntime().availableProcessors() : pCorePoolSize);
    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize, threadFactory);

    /* See if the Guava MoreExecutors is present */

    try {
      Class<?> moreExecutorsClass = Class.forName("com.google.common.util.concurrent.MoreExecutors");
      Method decorateMethod = moreExecutorsClass.getMethod("listeningDecorator", ScheduledExecutorService.class);
      ScheduledExecutorService decoratedExecutor =
        (ScheduledExecutorService) decorateMethod.invoke(null, scheduledExecutorService);
      if (decoratedExecutor != null)
        scheduledExecutorService = decoratedExecutor;
    }
    catch (ClassNotFoundException ex) {
    }
    catch (NoSuchMethodException ex) {
    }
    catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
    return scheduledExecutorService;
  }
}

package com.diamondq.common.injection.micronaut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

@Factory
public class ExecutorsProvider {

  public @Singleton ExecutorService create() {
    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    ExecutorService executorService = Executors.newCachedThreadPool(threadFactory);

    /* See if the Guava MoreExecutors is present */

    try {
      Class<?> moreExecutorsClass = Class.forName("com.google.common.util.concurrent.MoreExecutors");
      Method decorateMethod = moreExecutorsClass.getMethod("listeningDecorator", ExecutorService.class);
      ExecutorService decoratedExecutor = (ExecutorService) decorateMethod.invoke(null, executorService);
      if (decoratedExecutor != null)
        executorService = decoratedExecutor;
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
    return executorService;
  }

  @Singleton
  public ScheduledExecutorService create(@Value("${scheduledexecutorservice.corepoolsize:-1}") int pCorePoolSize) {
    int corePoolSize = (pCorePoolSize == -1 ? Runtime.getRuntime().availableProcessors() : pCorePoolSize);
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

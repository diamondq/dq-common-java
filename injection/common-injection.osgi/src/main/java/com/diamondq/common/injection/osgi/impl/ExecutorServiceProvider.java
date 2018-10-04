package com.diamondq.common.injection.osgi.impl;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * NOTE: Executor Service's intended for long running tasks. Each submission will start a new thread if one is not
 * idling.
 */
public class ExecutorServiceProvider extends AbstractOSGiConstructor {

  public ExecutorServiceProvider() {
    super(ConstructorInfoBuilder.builder().constructorClass(ExecutorServiceProvider.class) //
      .register(ExecutorService.class) //
      .factoryMethod("create"));
  }

  public ExecutorService create() {
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
}

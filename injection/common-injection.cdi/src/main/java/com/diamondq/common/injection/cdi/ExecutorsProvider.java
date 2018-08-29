package com.diamondq.common.injection.cdi;

import com.diamondq.common.config.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.checkerframework.checker.nullness.qual.Nullable;

@ApplicationScoped
public class ExecutorsProvider {

  /**
   * Creates 'long-lived' executor service that will generate threads on demand.
   *
   * @param pConfig the config
   * @return the long lived executor service
   */
  @Produces
  @ApplicationScoped
  @Named("long-lived")
  public ScheduledExecutorService createScheduledExecutorServiceViaInjection(Instance<Config> pConfig) {
    @Nullable
    Config config = null;
    if ((pConfig.isAmbiguous() == false) && (pConfig.isUnsatisfied() == false))
      config = pConfig.get();
    return createScheduledExecutorService(config);
  }

  /**
   * Shutdowns the executor service when the Application is shutdown
   *
   * @param service the service
   */
  public void close(@Disposes @Named("long-lived") ScheduledExecutorService service) {
    service.shutdown();
  }

  /**
   * Creates 'long-lived' executor service that will generate threads on demand.
   *
   * @param pConfig the config
   * @return the login-lived executor service
   */
  public static ScheduledExecutorService createScheduledExecutorService(@Nullable Config pConfig) {

    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    Integer corePoolSize = null;
    if (pConfig != null)
      corePoolSize = pConfig.bind("executors.core-pool-size", Integer.class);
    if (corePoolSize == null)
      corePoolSize = Runtime.getRuntime().availableProcessors();
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

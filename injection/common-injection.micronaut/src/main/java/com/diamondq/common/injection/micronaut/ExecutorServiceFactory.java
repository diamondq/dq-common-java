package com.diamondq.common.injection.micronaut;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Factory
public class ExecutorServiceFactory {
  private ThreadFactory daemonThreadFactory = new ThreadFactory() {

    private final ThreadFactory baseThreadFactory = Executors.defaultThreadFactory();

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public @Nullable Thread newThread(Runnable pR) {
      Thread thread = baseThreadFactory.newThread(pR);
      if (thread != null) thread.setDaemon(true);
      return thread;
    }
  };

  public @Named("DiamondQ")
  @Singleton ExecutorService getExecutorService() {
    return Executors.newCachedThreadPool(daemonThreadFactory);
  }

  public @Named("DiamondQ")
  @Singleton ScheduledExecutorService getScheduledExecutorService() {
    return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2, daemonThreadFactory);
  }
}

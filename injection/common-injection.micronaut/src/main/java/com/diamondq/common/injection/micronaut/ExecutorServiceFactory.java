package com.diamondq.common.injection.micronaut;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;

@Factory
public class ExecutorServiceFactory {
  public @Named("DiamondQ") @Singleton ExecutorService getExecutorService() {
    return Executors.newCachedThreadPool();
  }

  public @Named("DiamondQ") @Singleton ScheduledExecutorService getScheduledExecutorService() {
    return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
  }
}

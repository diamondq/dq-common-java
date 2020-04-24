package com.diamondq.common.injection.micronaut;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;

@Factory
public class InstanceFactory {

  @SuppressWarnings({"null"})
  @Singleton
  public <T> Instance<T> getInstance() {
    return new InstanceWrapper<T>(null);
  }
}

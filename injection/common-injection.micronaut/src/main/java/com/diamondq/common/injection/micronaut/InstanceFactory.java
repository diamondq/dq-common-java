package com.diamondq.common.injection.micronaut;

import io.micronaut.context.annotation.Factory;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;

@Factory
public class InstanceFactory {

  @SuppressWarnings({ "null" })
  @Singleton
  public <T> Instance<T> getInstance() {
    return new InstanceWrapper<T>(null);
  }
}

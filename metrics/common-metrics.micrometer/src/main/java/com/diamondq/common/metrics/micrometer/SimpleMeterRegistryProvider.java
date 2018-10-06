package com.diamondq.common.metrics.micrometer;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class SimpleMeterRegistryProvider extends AbstractOSGiConstructor {

  public SimpleMeterRegistryProvider() {
    super(ConstructorInfoBuilder.builder().constructorClass(SimpleMeterRegistryProvider.class) //
      .register(MeterRegistry.class) //
      .factoryMethod("create").factoryDelete("delete"));
  }

  public MeterRegistry create() {
    return new SimpleMeterRegistry();
  }

  public void delete(MeterRegistry pRegistry) {
    pRegistry.close();
  }
}

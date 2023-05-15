package com.diamondq.common.metrics.micrometer;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class GraphiteMeterRegistryProvider extends AbstractOSGiConstructor {

  public GraphiteMeterRegistryProvider() {
    super(ConstructorInfoBuilder.builder().constructorClass(GraphiteMeterRegistryProvider.class) //
      .register(MeterRegistry.class) //
      .factoryMethod("create").factoryDelete("delete") //
      .cArg().type(String.class).prop(".host").required().build() //
      .cArg().type(Integer.TYPE).prop(".port").value(2004).build() //
      .cArg().type(Long.TYPE).prop(".step").value(60L * 1000L).build() //
    );
  }

  public MeterRegistry create(String pHost, int pPort, long pStep) {
    GraphiteConfig graphiteConfig = new GraphiteConfig() {

      /**
       * @see io.micrometer.graphite.GraphiteConfig#host()
       */
      @Override
      public String host() {
        return pHost;
      }

      /**
       * @see io.micrometer.graphite.GraphiteConfig#port()
       */
      @Override
      public int port() {
        return pPort;
      }

      @Override
      public Duration step() {
        return Duration.ofMillis(pStep);
      }

      @Override
      public @Nullable String get(@Nullable String k) {
        return null; // accept the rest of the defaults
      }
    };
    return new GraphiteMeterRegistry(graphiteConfig, Clock.SYSTEM, HierarchicalNameMapper.DEFAULT);
  }

  public void delete(MeterRegistry pRegistry) {
    pRegistry.close();
  }
}

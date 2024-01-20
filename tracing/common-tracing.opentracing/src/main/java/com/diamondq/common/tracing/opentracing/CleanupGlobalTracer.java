package com.diamondq.common.tracing.opentracing;

import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeforeShutdown;
import jakarta.inject.Inject;

import java.lang.reflect.Field;

@ApplicationScoped
public class CleanupGlobalTracer {

  @Inject
  public CleanupGlobalTracer() {
  }

  public void cleanupGlobal(@Observes BeforeShutdown pEvent) {
    cleanup();
  }

  public static void cleanup() {
    try {
      Field field = GlobalTracer.class.getDeclaredField("tracer");
      field.setAccessible(true);
      field.set(null, NoopTracerFactory.create());

      Field registeredField = GlobalTracer.class.getDeclaredField("isRegistered");
      registeredField.setAccessible(true);
      registeredField.set(null, false);
    }
    catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
}

package com.diamondq.common.tracing.opentracing;

import java.lang.reflect.Field;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.inject.Inject;

import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;

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

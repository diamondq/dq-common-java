package com.diamondq.common.tracing.opentracing;

import java.lang.reflect.Field;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import io.opentracing.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;

@ApplicationScoped
public class CleanupGlobalTracer implements Extension {

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
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}
}

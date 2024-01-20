package com.diamondq.common.tracing.jaeger;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(WeldJunit5Extension.class)

public class SimpleTest {

  private static final Logger sLogger = LoggerFactory.getLogger(SimpleTest.class);

  @WeldSetup public WeldInitiator weld = WeldInitiator.of(new Weld());

  @Test
  public void testFoo() {
    Tracer tracer = weld.select(Tracer.class).get();
    Assertions.assertNotNull(tracer);
    Span span = tracer.buildSpan("testFoo").start();
    try (Scope scope = tracer.scopeManager().activate(span)) {
      sLogger.info("Test logging First");
      Span childSpan = tracer.buildSpan("childFoo").asChildOf(span).start();
      try (Scope childScope = tracer.scopeManager().activate(childSpan)) {
        sLogger.info("Test logging Inner");
      }
      finally {
        childSpan.finish();
      }
      sLogger.info("Test logging Outer");
    }
    finally {
      span.finish();
    }
    sLogger.info("Complete");
  }
}

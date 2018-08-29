package com.diamondq.common.tracing.jaeger;

import static org.junit.Assert.assertNotNull;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Tracer;

public class SimpleTest {

  private static final Logger sLogger = LoggerFactory.getLogger(SimpleTest.class);

  @Rule
  public WeldInitiator        weld    = WeldInitiator.of(new Weld());

  @Test
  public void testFoo() {
    Tracer tracer = weld.select(Tracer.class).get();
    assertNotNull(tracer);
    try (Scope scope = tracer.buildSpan("testFoo").startActive(true)) {
      sLogger.info("Test logging First");
      try (Scope childSpan = tracer.buildSpan("childFoo").asChildOf(scope.span()).startActive(true)) {
        sLogger.info("Test logging Inner");
      }
      sLogger.info("Test logging Outer");
    }
    sLogger.info("Complete");
  }
}

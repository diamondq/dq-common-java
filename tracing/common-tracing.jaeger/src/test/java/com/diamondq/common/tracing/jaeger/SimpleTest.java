package com.diamondq.common.tracing.jaeger;

import static org.junit.Assert.assertNotNull;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

public class SimpleTest {

  private static final Logger sLogger = LoggerFactory.getLogger(SimpleTest.class);

  @Rule
  public WeldInitiator        weld    = WeldInitiator.of(new Weld());

  @Test
  public void testFoo() {
    Tracer tracer = weld.select(Tracer.class).get();
    assertNotNull(tracer);
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

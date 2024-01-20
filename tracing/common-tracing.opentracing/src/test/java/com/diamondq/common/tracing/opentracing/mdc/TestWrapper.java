package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestWrapper {

  @SuppressWarnings("null") private MockTracer mockTracker;

  @BeforeEach
  public void setup() {
    mockTracker = MockTracing.before();
  }

  @AfterEach
  public void cleanup() {
    MockTracing.after();
  }

  @Test
  public void testActiveSpan() {
    TracingAssertions.assertNoActiveSpan("No span at startup");
    Span span = GlobalTracer.get().buildSpan("testActiveSpan").start();
    try (Scope scope = GlobalTracer.get().scopeManager().activate(span)) {
      TracingAssertions.assertActiveSpan("Span must be active");
    }
    finally {
      span.finish();
    }
    TracingAssertions.assertNoActiveSpan("No span at end");
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
  }

  @Test
  public void testAssertionCounts() {
    TracingAssertions.assertNoActiveSpan("No span at startup");
    Span span = GlobalTracer.get().buildSpan("testActiveSpan").start();
    try (Scope scope = GlobalTracer.get().scopeManager().activate(span)) {
      TracingAssertions.assertActiveSpan("Span must be active");
    }
    TracingAssertions.assertNoActiveSpan("No active span after block");
    TracingAssertions.assertCompletedSpans("Span hasn't been completed yet", 0, mockTracker);
    span.finish();
    TracingAssertions.assertNoActiveSpan("No span at end");
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
  }
}

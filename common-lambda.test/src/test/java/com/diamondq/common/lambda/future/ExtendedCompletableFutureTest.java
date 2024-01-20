package com.diamondq.common.lambda.future;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

@ExtendWith(WeldJunit5Extension.class)
public class ExtendedCompletableFutureTest {

  private static final Logger sLogger = LoggerFactory.getLogger(ExtendedCompletableFutureTest.class);

  @WeldSetup public WeldInitiator weld = WeldInitiator.of(new Weld());

  @SuppressWarnings("null") private MockTracer mockTracker;

  @BeforeEach
  public void setup() {
    mockTracker = MockTracing.before();
  }

  @AfterEach
  public void cleanup() {
    MockTracing.afterNoCDI();
  }

  @Test
  public void testThenApply() {
    ExtendedCompletableFuture<Boolean> f;
    Span span = mockTracker.buildSpan("testThenApply").start();
    try (Scope scope = mockTracker.scopeManager().activate(span)) {
      sLogger.info("Before future");
      f = new ExtendedCompletableFuture<>();
      f.thenApply((b) -> {
        TracingAssertions.assertActiveSpan("Should have active span");
        sLogger.info("Inside apply");
        return true;
      });
    }
    finally {
      span.finish();
    }
    TracingAssertions.assertNoActiveSpan("No active span after block");
    sLogger.info("Outside span");
    f.complete(true);
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
  }

  @Test
  public void testThenCombine() throws Exception {
    sLogger.info("***** testThenCombine");
    ExtendedCompletableFuture<Boolean> f1;
    ExtendedCompletableFuture<Boolean> f2;
    ExtendedCompletableFuture<Boolean> f3;
    Span capturedSpan = mockTracker.buildSpan("testThenCombine").start();
    try (Scope scope = mockTracker.activateSpan(capturedSpan)) {
      sLogger.info("Before future");
      f1 = new ExtendedCompletableFuture<>();
      f2 = new ExtendedCompletableFuture<>();
      f3 = f1.thenCombine(f2, (b1, b2) -> {
        sLogger.info("   +++ Inside combine");
        Assertions.assertEquals(true, b1);
        Assertions.assertEquals(true, b2);
        TracingAssertions.assertActiveSpan("Should have active span");
        sLogger.info("   --- Inside combine");
        return false;
      });
    }
    TracingAssertions.assertNoActiveSpan("No active span after block");
    TracingAssertions.assertCompletedSpans("No spans should have completed", 0, mockTracker);
    sLogger.info("Outside span");
    f1.complete(true);
    TracingAssertions.assertNoActiveSpan("No active span after block");
    TracingAssertions.assertCompletedSpans("No spans should have completed", 0, mockTracker);
    f2.complete(true);
    Boolean result = f3.get();
    TracingAssertions.assertNoActiveSpan("No active span after block");
    // mockTracker.scopeManager().activate(capturedSpan, true).close();
    capturedSpan.finish();
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
    Assertions.assertEquals(false, result, "Should have gotten the combine");
    sLogger.info("----- testThenCombine");
  }

  @SuppressWarnings("null")
  @Test
  public void testRunAsync() {
    Span span = mockTracker.buildSpan("testRunAsync").start();
    try (Scope scope = mockTracker.scopeManager().activate(span)) {
      try {
        ExtendedCompletableFuture.runAsync(() -> {
          Assertions.fail("Should never reach here");
        }, null);
        Assertions.fail("An exception should have occurred");
      }
      catch (RuntimeException ex) {
      }
    }
    finally {
      span.finish();
    }
    TracingAssertions.assertNoActiveSpan("No active span after block");
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
  }

  @Test
  public void testRunAsyncExecutor() throws Exception {
    Executor executor = weld.select(Executor.class).get();
    ExtendedCompletableFuture<@Nullable Void> f;
    Span span = mockTracker.buildSpan("testRunAsyncExecutor").start();
    try (Scope scope = mockTracker.activateSpan(span)) {
      final String threadName = Thread.currentThread().getName();
      f = ExtendedCompletableFuture.runAsync(() -> {
        TracingAssertions.assertActiveSpan("Should be within the span");
        Assertions.assertNotEquals("Threads should be different", threadName, Thread.currentThread().getName());
      }, executor);
      TracingAssertions.assertCompletedSpans("Span should not have completed", 0, mockTracker);
    }
    finally {
      span.finish();
    }
    TracingAssertions.assertNoActiveSpan("No active span after block");
    f.get();
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
  }
}

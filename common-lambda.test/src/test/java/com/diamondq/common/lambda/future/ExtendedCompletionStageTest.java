package com.diamondq.common.lambda.future;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public class ExtendedCompletionStageTest {

  private static final Logger sLogger = LoggerFactory.getLogger(ExtendedCompletionStageTest.class);

  @Rule public WeldInitiator weld = WeldInitiator.of(new Weld());

  @SuppressWarnings("null") private MockTracer mockTracker;

  @Before
  public void setup() {
    mockTracker = MockTracing.before();
  }

  @After
  public void cleanup() {
    MockTracing.afterNoCDI();
  }

  @Test
  public void testThenApply() {
    sLogger.info("***** testThenApply");
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
    sLogger.info("----- testThenApply");
  }

  @Test
  public void testThenCombine() throws Exception {
    sLogger.info("***** testThenCombine");
    ExtendedCompletableFuture<Boolean> f1;
    ExtendedCompletableFuture<Boolean> f2;
    ExtendedCompletableFuture<Boolean> f3;
    Span capturedSpan = mockTracker.buildSpan("testThenCombine").start();
    try (Scope scope = mockTracker.scopeManager().activate(capturedSpan)) {
      sLogger.info("Before future");
      f1 = new ExtendedCompletableFuture<>();
      f2 = new ExtendedCompletableFuture<>();
      f3 = new ExtendedCompletableFuture<Boolean>().applyToEither(f1.thenCombine(f2, (b1, b2) -> {
        sLogger.info("   +++ Inside combine");
        Assert.assertEquals(true, b1);
        Assert.assertEquals(true, b2);
        TracingAssertions.assertActiveSpan("Should have active span");
        sLogger.info("   --- Inside combine");
        return false;
      }), (a) -> a);
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
    capturedSpan.finish();
    TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
    Assert.assertEquals("Should have gotten the combine", false, result);
    sLogger.info("----- testThenCombine");
  }

  @SuppressWarnings("null")
  @Test
  public void testRunAsync() {
    Span span = mockTracker.buildSpan("testRunAsync").start();
    try (Scope scope = mockTracker.scopeManager().activate(span)) {
      try {
        ExtendedCompletionStage.runAsync(() -> {
          Assert.fail("Should never reach here");
        }, null);
        Assert.fail("An exception should have occurred");
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
    try (Scope scope = mockTracker.scopeManager().activate(span)) {
      final String threadName = Thread.currentThread().getName();
      f = new ExtendedCompletableFuture<@Nullable Void>().applyToEither(ExtendedCompletionStage.runAsync(() -> {
        TracingAssertions.assertActiveSpan("Should be within the span");
        Assert.assertNotEquals("Threads should be different", threadName, Thread.currentThread().getName());
      }, executor), (a) -> a);
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

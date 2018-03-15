package com.diamondq.common.lambda.future;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;

import java.util.concurrent.Executor;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;

public class ExtendedCompletableFutureTest {

	private static final Logger	sLogger	= LoggerFactory.getLogger(ExtendedCompletableFutureTest.class);

	@Rule
	public WeldInitiator		weld	= WeldInitiator.of(new Weld());

	@SuppressWarnings("null")
	private MockTracer			mockTracker;

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
		ExtendedCompletableFuture<Boolean> f;
		try (Scope scope = mockTracker.buildSpan("testThenApply").startActive(true)) {
			sLogger.info("Before future");
			f = new ExtendedCompletableFuture<>();
			f.thenApply((b) -> {
				TracingAssertions.assertActiveSpan("Should have active span");
				sLogger.info("Inside apply");
				return true;
			});
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
		Span capturedSpan;
		try (Scope scope = mockTracker.buildSpan("testThenCombine").startActive(false)) {
			capturedSpan = scope.span();
			sLogger.info("Before future");
			f1 = new ExtendedCompletableFuture<>();
			f2 = new ExtendedCompletableFuture<>();
			f3 = f1.thenCombine(f2, (b1, b2) -> {
				sLogger.info("   +++ Inside combine");
				Assert.assertEquals(true, b1);
				Assert.assertEquals(true, b2);
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
		mockTracker.scopeManager().activate(capturedSpan, true).close();
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
		Assert.assertEquals("Should have gotten the combine", false, result);
		sLogger.info("----- testThenCombine");
	}

	@SuppressWarnings("null")
	@Test
	public void testRunAsync() {
		try (Scope scope = mockTracker.buildSpan("testRunAsync").startActive(false)) {
			try {
				ExtendedCompletableFuture.runAsync(() -> {
					Assert.fail("Should never reach here");
				}, null);
				Assert.fail("An exception should have occurred");
			}
			catch (RuntimeException ex) {
			}
		}
		TracingAssertions.assertNoActiveSpan("No active span after block");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}

	@Test
	public void testRunAsyncExecutor() throws Exception {
		Executor executor = weld.select(Executor.class).get();
		ExtendedCompletableFuture<@Nullable Void> f;
		try (Scope scope = mockTracker.buildSpan("testRunAsyncExecutor").startActive(true)) {
			final String threadName = Thread.currentThread().getName();
			f = ExtendedCompletableFuture.runAsync(() -> {
				TracingAssertions.assertActiveSpan("Should be within the span");
				Assert.assertNotEquals("Threads should be different", threadName, Thread.currentThread().getName());
			}, executor);
			TracingAssertions.assertCompletedSpans("Span should not have completed", 0, mockTracker);
		}
		TracingAssertions.assertNoActiveSpan("No active span after block");
		f.get();
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}
}

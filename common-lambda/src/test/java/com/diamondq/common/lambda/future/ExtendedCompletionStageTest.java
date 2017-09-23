package com.diamondq.common.lambda.future;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;

import java.util.concurrent.Executor;

public class ExtendedCompletionStageTest {

	private static final Logger	sLogger	= LoggerFactory.getLogger(ExtendedCompletionStageTest.class);

	@Rule
	public WeldInitiator		weld	= WeldInitiator.of(new Weld());

	private MockTracer mockTracker;

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
		try (ActiveSpan span = mockTracker.buildSpan("testThenApply").startActive()) {
			sLogger.info("Before future");
			f = new ExtendedCompletableFuture<>();
			ExtendedCompletionStage<Boolean> r = ExtendedCompletionStage.of(f);
			r.thenApply((b) -> {
				TracingAssertions.assertActiveSpan("Should have active span");
				sLogger.info("Inside apply");
				return true;
			});
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
		try (ActiveSpan span = mockTracker.buildSpan("testThenCombine").startActive()) {
			sLogger.info("Before future");
			f1 = new ExtendedCompletableFuture<>();
			f2 = new ExtendedCompletableFuture<>();
			f3 = new ExtendedCompletableFuture<Boolean>().applyToEither(
				ExtendedCompletionStage.of(f1).thenCombine(ExtendedCompletionStage.of(f2), (b1, b2) -> {
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
		boolean result = f3.get();
		TracingAssertions.assertNoActiveSpan("No active span after block");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
		Assert.assertEquals("Should have gotten the combine", false, result);
		sLogger.info("----- testThenCombine");
	}
	
	@Test
	public void testRunAsync() {
		try (ActiveSpan span = mockTracker.buildSpan("testRunAsync").startActive()) {
			try {
				ExtendedCompletionStage.runAsync(()-> { Assert.fail("Should never reach here"); }, null);
				Assert.fail("An exception should have occurred");
			} catch (RuntimeException ex) {
			}
		}
		TracingAssertions.assertNoActiveSpan("No active span after block");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}
	
	@Test
	public void testRunAsyncExecutor() throws Exception {
		Executor executor = weld.select(Executor.class).get();
		ExtendedCompletableFuture<@Nullable Void> f;		
		try (ActiveSpan span = mockTracker.buildSpan("testRunAsyncExecutor").startActive()) {
			final String threadName = Thread.currentThread().getName();
			f = new ExtendedCompletableFuture<@Nullable Void>().applyToEither(ExtendedCompletionStage.runAsync(()-> {
				TracingAssertions.assertActiveSpan("Should be within the span");
				Assert.assertNotEquals("Threads should be different", threadName, Thread.currentThread().getName());
			}, executor), (a)->a);
			TracingAssertions.assertCompletedSpans("Span should not have completed", 0, mockTracker);
		}
		TracingAssertions.assertNoActiveSpan("No active span after block");
		f.get();
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}
}

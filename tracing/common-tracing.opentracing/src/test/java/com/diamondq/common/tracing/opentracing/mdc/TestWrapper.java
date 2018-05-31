package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;

public class TestWrapper {

	@SuppressWarnings("null")
	private MockTracer mockTracker;

	@Before
	public void setup() {
		mockTracker = MockTracing.before();
	}

	@After
	public void cleanup() {
		MockTracing.after();
	}

	@Test
	public void testActiveSpan() {
		TracingAssertions.assertNoActiveSpan("No span at startup");
		try (Scope scope = GlobalTracer.get().buildSpan("testActiveSpan").startActive(true)) {
			TracingAssertions.assertActiveSpan("Span must be active");
		}
		TracingAssertions.assertNoActiveSpan("No span at end");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}

	@Test
	public void testAssertionCounts() {
		TracingAssertions.assertNoActiveSpan("No span at startup");
		Span capture;
		try (Scope scope = GlobalTracer.get().buildSpan("testActiveSpan").startActive(false)) {
			TracingAssertions.assertActiveSpan("Span must be active");
			capture = scope.span();
		}
		TracingAssertions.assertNoActiveSpan("No active span after block");
		TracingAssertions.assertCompletedSpans("Span hasn't been completed yet", 0, mockTracker);
		GlobalTracer.get().scopeManager().activate(capture, true).close();
		TracingAssertions.assertNoActiveSpan("No span at end");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}
}

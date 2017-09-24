package com.diamondq.common.tracing.opentracing.mdc;

import com.diamondq.common.tracing.opentracing.testhelpers.MockTracing;
import com.diamondq.common.tracing.opentracing.testhelpers.TracingAssertions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpan.Continuation;
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
		try (ActiveSpan span = GlobalTracer.get().buildSpan("testActiveSpan").startActive()) {
			TracingAssertions.assertActiveSpan("Span must be active");
		}
		TracingAssertions.assertNoActiveSpan("No span at end");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}

	@Test
	public void testAssertionCounts() {
		TracingAssertions.assertNoActiveSpan("No span at startup");
		Continuation capture;
		try (ActiveSpan span = GlobalTracer.get().buildSpan("testActiveSpan").startActive()) {
			TracingAssertions.assertActiveSpan("Span must be active");
			capture = span.capture();
		}
		TracingAssertions.assertNoActiveSpan("No active span after block");
		TracingAssertions.assertCompletedSpans("Span hasn't been completed yet", 0, mockTracker);
		capture.activate().close();
		TracingAssertions.assertNoActiveSpan("No span at end");
		TracingAssertions.assertCompletedSpans("Span should have completed", 1, mockTracker);
	}
}

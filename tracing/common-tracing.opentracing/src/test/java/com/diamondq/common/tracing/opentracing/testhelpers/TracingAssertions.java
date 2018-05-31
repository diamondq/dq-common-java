package com.diamondq.common.tracing.opentracing.testhelpers;

import org.junit.Assert;

import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;

public class TracingAssertions {

	/**
	 * Asserts that there is no active span
	 *
	 * @param pMessage the identifying message for the {@link AssertionError} (<code>null</code> okay)
	 */
	public static void assertNoActiveSpan(String pMessage) {
		Assert.assertNull(pMessage, GlobalTracer.get().activeSpan());
	}

	/**
	 * Asserts that there is an active span
	 *
	 * @param pMessage the identifying message for the {@link AssertionError} (<code>null</code> okay)
	 */
	public static void assertActiveSpan(String pMessage) {
		Assert.assertNotNull(pMessage, GlobalTracer.get().activeSpan());
	}

	public static void assertCompletedSpans(String pMessage, int pExpected, MockTracer pTracker) {
		Assert.assertEquals(pMessage, pExpected, pTracker.finishedSpans().size());
	}
}

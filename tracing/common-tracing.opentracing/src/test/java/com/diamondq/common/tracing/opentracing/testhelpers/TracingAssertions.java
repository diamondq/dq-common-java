package com.diamondq.common.tracing.opentracing.testhelpers;

import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.junit.jupiter.api.Assertions;

public class TracingAssertions {

  /**
   * Asserts that there is no active span
   *
   * @param pMessage the identifying message for the {@link AssertionError} (<code>null</code> okay)
   */
  public static void assertNoActiveSpan(String pMessage) {
    Assertions.assertNull(GlobalTracer.get().activeSpan(), pMessage);
  }

  /**
   * Asserts that there is an active span
   *
   * @param pMessage the identifying message for the {@link AssertionError} (<code>null</code> okay)
   */
  public static void assertActiveSpan(String pMessage) {
    Assertions.assertNotNull(GlobalTracer.get().activeSpan(), pMessage);
  }

  public static void assertCompletedSpans(String pMessage, int pExpected, MockTracer pTracker) {
    Assertions.assertEquals(pExpected, pTracker.finishedSpans().size(), pMessage);
  }
}

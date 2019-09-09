package com.diamondq.common.tracing.opentracing.testhelpers;

import com.diamondq.common.tracing.opentracing.CleanupGlobalTracer;

import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.util.ThreadLocalScopeManager;

public class MockTracing {

  public static MockTracer before() {
    MockTracer mockTracker = new MockTracer(new ThreadLocalScopeManager());
    CleanupGlobalTracer.cleanup();
    if (GlobalTracer.registerIfAbsent(mockTracker) == false)
      throw new IllegalStateException("Unable to register replacement GlobalTracer");
    return mockTracker;
  }

  public static void after() {
    CleanupGlobalTracer.cleanup();
  }

  public static void afterNoCDI() {
  }
}

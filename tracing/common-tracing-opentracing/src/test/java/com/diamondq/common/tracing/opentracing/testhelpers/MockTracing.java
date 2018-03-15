package com.diamondq.common.tracing.opentracing.testhelpers;

import com.diamondq.common.tracing.opentracing.CleanupGlobalTracer;

import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import io.opentracing.util.ThreadLocalScopeManager;

public class MockTracing {

	public static MockTracer before() {
		MockTracer mockTracker = new MockTracer(new ThreadLocalScopeManager());
		CleanupGlobalTracer.cleanup();
		GlobalTracer.register(mockTracker);
		return mockTracker;
	}

	public static void after() {
		CleanupGlobalTracer.cleanup();
	}

	public static void afterNoCDI() {
	}
}

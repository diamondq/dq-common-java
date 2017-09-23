package com.diamondq.common.lambda.future;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;

public class ExtendedCompletableFutureTest {

	private static final Logger	sLogger	= LoggerFactory.getLogger(ExtendedCompletableFutureTest.class);

	@Rule
	public WeldInitiator		weld	= WeldInitiator.of(new Weld());

	@Test
	public void testThenApply() {
		Tracer tracer = weld.select(Tracer.class).get();
		ExtendedCompletableFuture<Boolean> f;
		try (ActiveSpan span = tracer.buildSpan("testThenApply").startActive()) {
			sLogger.info("Before future");
			f = new ExtendedCompletableFuture<>();
			f.thenApply((b) -> {
				sLogger.info("Inside apply");
				return true;
			});
		}
		sLogger.info("Outside span");
		f.complete(true);
	}
}

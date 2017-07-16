package com.diamondq.common.asyncjobs.tests.simple;

import com.diamondq.common.asyncjobs.api.Engine;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.concurrent.ExecutionException;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit4.WeldInitiator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSubmit {

	private static Logger	sLogger	= LoggerFactory.getLogger(TestSubmit.class);

	@Rule
	public WeldInitiator	weld	= WeldInitiator.of(new Weld());

	@Test
	public void submit() throws InterruptedException, ExecutionException {
		sLogger.warn("Here");
		Engine engine = weld.select(Engine.class).get();
		ExtendedCompletableFuture<String> result = engine.submit(TestJob.class, String.class);
		String response = result.get();
		Assert.assertEquals("Test Value", response);
	}
}

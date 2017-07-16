package com.diamondq.common.asyncjobs.tests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@ApplicationScoped
public class TestExecutors {

	@Produces
	@Singleton
	public ExecutorService getExecutor() {
		return Executors.newScheduledThreadPool(0);
	}
}

package com.diamondq.common.asyncjobs.tests.simple;

import com.diamondq.common.asyncjobs.api.AsyncJob;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestJobDependent {

	@Inject
	public TestJobDependent() {

	}

	@AsyncJob("TestJobCreateDependent")
	public Dependent createDependent() {
		return new Dependent("Test Value");
	}
}

package com.diamondq.common.asyncjobs.tests.simple;

import com.diamondq.common.asyncjobs.api.AsyncJob;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TestJob {

	@Inject
	public TestJob() {

	}

	@AsyncJob("TestJobExecute")
	public String execute(Dependent pDependent) {
		return pDependent.getValue();
	}

}

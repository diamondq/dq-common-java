package com.diamondq.common.asyncjobs.api.tests;

import com.diamondq.common.asyncjobs.api.JobSetup;

import org.junit.Test;

public class ConstructionTest {

	public String simpleFunc(String pInput) {
		return pInput + "--";
	}

	public String simpleSupplier() {
		return "Basic";
	}

	@Test
	public void test() {
		// @formatter:off
		JobSetup.builder().method(this::simpleFunc)
			.param(String.class)
				.state("State1")
				.build()
			.result(String.class)
				.state("State2")
				.build()
			.build();
		
		JobSetup.builder().method(this::simpleSupplier)
			.result(String.class)
				.state("State1")
				.build()
			.build();
		//@formatter:on
	}

}

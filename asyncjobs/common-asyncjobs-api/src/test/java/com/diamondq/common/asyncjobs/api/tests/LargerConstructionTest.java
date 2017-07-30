package com.diamondq.common.asyncjobs.api.tests;

import com.diamondq.common.asyncjobs.api.Action;
import com.diamondq.common.asyncjobs.api.JobSetup;

import org.junit.Test;

public class LargerConstructionTest {

	public String simpleFunc(String pInput) {
		return pInput + "--";
	}

	public Boolean check(Object pInput) {
		return false;
	}

	public String simpleSupplier() {
		return "Basic";
	}

	public static class Credentials {

	}

	public static class CredentialForm {

	}

	public static class TimeRecord {

	}

	@Test
	public void test() {
		// @formatter:off
		JobSetup.builder().method(this::simpleFunc)
			.triggerCollection(TimeRecord.class).action(Action.CHANGE).variable("Employer").variable("Employee").build()
			.param(Credentials.class).name("Dropbox Credentials").state("Stored").stateByVariable("Employer").build()
			.param(String.class).name("Dropbox Folder").state("Stored").stateByVariable("Employer").build()
			.param(TimeRecord.class).name("Timesheet").stateByVariable("Employer").stateByVariable("Employee").build()
		.build();
		
		/* Retrieve the credentials from the database */
		
		JobSetup.builder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").state("Stored").variable("Employer").build()
			.guard(this::check)
		.build();
		
		/* Otherwise, store new credentials after getting them from the user */
		
		JobSetup.builder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").state("Stored").variable("Employer").build()
			.param(Credentials.class).name("Dropbox Credentials").state("Transient").state("Validated").stateByVariable("Employer").build()
		.build();

		/* Validate that the user provided credentials are 'good' */
		
		JobSetup.builder().method(this::simpleFunc)
			.result(Credentials.class).name("Dropbox Credentials").state("Transient").state("Validated").variable("Employer").asParam().build()
			.param(Credentials.class).name("Dropbox Credentials").state("Transient").missingState("Validated").stateByVariable("Employer").build()
		.build();
		
		/* Send Dropbox Credentials Request Data Form to User */
		
		JobSetup.builder().method(this::simpleFunc)
			.result(CredentialForm.class).variable("Employer").state("Transient").build()
			.param(String.class).state("Online").state("Subscribed").stateByVariable("Employer").name("Jid").build()
			.param(String.class).state("LoggedIn").name("XMPP Client").build()
			.prepResult(Credentials.class).name("Dropbox Credentials").state("Transient").stateByVariable("Employer").build()
		.build();

		//@formatter:on
	}

}

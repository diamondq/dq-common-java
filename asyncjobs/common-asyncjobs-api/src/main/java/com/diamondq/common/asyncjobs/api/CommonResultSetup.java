package com.diamondq.common.asyncjobs.api;

public class CommonResultSetup<RT, AS extends CommonResultSetup<RT, AS>> extends CommonSetup<RT, AS> {

	private boolean mResultIsParam = false;

	protected CommonResultSetup(JobSetup pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
	}

	@SuppressWarnings("unchecked")
	public AS asParam() {
		mResultIsParam = true;
		return (AS) this;
	}

}

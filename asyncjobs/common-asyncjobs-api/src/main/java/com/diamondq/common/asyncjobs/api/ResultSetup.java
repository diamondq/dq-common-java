package com.diamondq.common.asyncjobs.api;

public class ResultSetup<RT> extends CommonResultSetup<RT, ResultSetup<RT>> {

	protected ResultSetup(JobSetup pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
	}

	public static <NPT> ResultSetup<NPT> builder(JobSetup pJobSetup, Class<NPT> pClass) {
		return new ResultSetup<NPT>(pJobSetup, pClass);
	}

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobSetup build() {
		mJobSetup.addResult(this);
		return mJobSetup;
	}
}

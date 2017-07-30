package com.diamondq.common.asyncjobs.api;

public class PrepResultSetup<RT> extends CommonResultSetup<RT, PrepResultSetup<RT>> {

	private PrepResultSetup(JobSetup pJobSetup, Class<RT> pClass) {
		super(pJobSetup, pClass);
	}

	public static <NPT> PrepResultSetup<NPT> builder(JobSetup pJobSetup, Class<NPT> pClass) {
		return new PrepResultSetup<NPT>(pJobSetup, pClass);
	}

	public PrepResultSetup<RT> stateByVariable(String pVariable) {
		mRequiredStates.add(new StateVariableCriteria(pVariable, true));
		return this;
	}

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobSetup build() {
		mJobSetup.addPrepResult(this);
		return mJobSetup;
	}
}

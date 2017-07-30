package com.diamondq.common.asyncjobs.api;

import java.util.HashSet;
import java.util.Set;

public class ParamSetup<PT> extends CommonSetup<PT, ParamSetup<PT>> {

	private Set<StateCriteria> mMissingStates;

	private ParamSetup(JobSetup pJobSetup, Class<PT> pClass) {
		super(pJobSetup, pClass);
		mMissingStates = new HashSet<>();
	}

	public static <NPT> ParamSetup<NPT> builder(JobSetup pJobSetup, Class<NPT> pClass) {
		return new ParamSetup<NPT>(pJobSetup, pClass);
	}

	/**
	 * Defines a missing state for this param
	 * 
	 * @param pState the state
	 * @return the param builder
	 */
	public ParamSetup<PT> missingState(String pState) {
		mMissingStates.add(new StateValueCriteria(pState, true, "true"));
		return this;
	}

	/**
	 * Defines a missing state for this param
	 * 
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the param builder
	 */
	public ParamSetup<PT> missingStateEquals(String pState, String pValue) {
		mMissingStates.add(new StateValueCriteria(pState, true, pValue));
		return this;
	}

	public ParamSetup<PT> stateByVariable(String pVariable) {
		mRequiredStates.add(new StateVariableCriteria(pVariable, true));
		return this;
	}

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobSetup build() {
		mJobSetup.addParam(this);
		return mJobSetup;
	}
}

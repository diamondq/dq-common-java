package com.diamondq.common.reaction.api.impl;

import com.diamondq.common.reaction.api.JobBuilder;
import com.diamondq.common.reaction.api.ParamBuilder;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ParamBuilderImpl<PT> extends CommonBuilderImpl<PT, ParamBuilder<PT>> implements ParamBuilder<PT> {

	private Set<StateCriteria>	mMissingStates;

	private @Nullable String	mValueByVariable;

	public ParamBuilderImpl(JobBuilderImpl pJobSetup, Class<PT> pClass) {
		super(pJobSetup, pClass);
		mMissingStates = new HashSet<>();
	}

	/**
	 * Defines a missing state for this param
	 * 
	 * @param pState the state
	 * @return the param builder
	 */
	@Override
	public ParamBuilderImpl<PT> missingState(String pState) {
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
	@Override
	public ParamBuilderImpl<PT> missingStateEquals(String pState, String pValue) {
		mMissingStates.add(new StateValueCriteria(pState, true, pValue));
		return this;
	}

	@Override
	public ParamBuilderImpl<PT> stateByVariable(String pVariable) {
		mRequiredStates.add(new StateVariableCriteria(pVariable, true));
		return this;
	}

	/**
	 * @see com.diamondq.common.reaction.api.ParamBuilder#valueByVariable(java.lang.String)
	 */
	@Override
	public ParamBuilder<PT> valueByVariable(String pVariableName) {
		mValueByVariable = pVariableName;
		return this;
	}

	public @Nullable String getValueByVariable() {
		return mValueByVariable;
	}

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	@Override
	public JobBuilder build() {
		mJobSetup.addParam(this);
		return mJobSetup;
	}

	public Set<StateCriteria> getMissingStates() {
		return mMissingStates;
	}
}

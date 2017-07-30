package com.diamondq.common.asyncjobs.api;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class CommonSetup<T, AS extends CommonSetup<T, AS>> {

	protected JobSetup				mJobSetup;

	protected Class<T>				mClass;

	protected Set<StateCriteria>	mRequiredStates;

	protected Set<VariableCriteria>	mVariables;

	protected @Nullable String		mName;

	protected CommonSetup(JobSetup pJobSetup, Class<T> pClass) {
		mJobSetup = pJobSetup;
		mClass = pClass;
		mRequiredStates = new HashSet<>();
		mVariables = new HashSet<>();
	}

	/**
	 * Defines the name
	 * 
	 * @param pName the name
	 * @return the builder
	 */
	@SuppressWarnings("unchecked")
	public AS name(String pName) {
		mName = pName;
		return (AS) this;
	}

	/**
	 * Defines a required state
	 * 
	 * @param pState the state
	 * @return the builder
	 */
	@SuppressWarnings("unchecked")
	public AS state(String pState) {
		mRequiredStates.add(new StateValueCriteria(pState, true, "true"));
		return (AS) this;
	}

	/**
	 * Defines a required state
	 * 
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the builder
	 */
	@SuppressWarnings("unchecked")
	public AS stateEquals(String pState, String pValue) {
		mRequiredStates.add(new StateValueCriteria(pState, true, pValue));
		return (AS) this;
	}

	@SuppressWarnings("unchecked")
	public AS variable(String pState) {
		mVariables.add(new VariableCriteria(pState, pState));
		return (AS) this;
	}
}

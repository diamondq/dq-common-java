package com.diamondq.common.asyncjobs.api;

public class StateValueCriteria extends StateCriteria {

	public final String value;

	public StateValueCriteria(String pState, boolean pIsEqual, String pValue) {
		super(pState, pIsEqual);
		value = pValue;
	}

}

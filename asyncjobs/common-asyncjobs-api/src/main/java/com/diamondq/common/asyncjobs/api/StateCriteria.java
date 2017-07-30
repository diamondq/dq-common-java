package com.diamondq.common.asyncjobs.api;

public class StateCriteria {

	public final String		state;

	public final boolean	isEqual;

	public StateCriteria(String pState, boolean pIsEqual) {
		state = pState;
		isEqual = pIsEqual;
	}

}

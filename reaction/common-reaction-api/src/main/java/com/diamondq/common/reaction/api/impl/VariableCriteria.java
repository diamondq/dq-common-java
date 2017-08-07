package com.diamondq.common.reaction.api.impl;

public class VariableCriteria extends StateCriteria {

	public final String	variableName;

	public VariableCriteria(String pState, String pVariableName) {
		super(pState, true);
		variableName = pVariableName;
	}

}

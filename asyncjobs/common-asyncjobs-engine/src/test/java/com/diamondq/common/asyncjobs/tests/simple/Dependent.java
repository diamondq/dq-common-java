package com.diamondq.common.asyncjobs.tests.simple;

public class Dependent {

	private String mValue;

	public Dependent(String pValue) {
		super();
		mValue = pValue;
	}

	public String getValue() {
		return mValue;
	}

}
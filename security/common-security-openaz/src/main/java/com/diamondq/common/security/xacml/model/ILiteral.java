package com.diamondq.common.security.xacml.model;

public interface ILiteral extends IFunctionArgument {

	public String getSingleValue();
	
	public boolean isBooleanValue();

}

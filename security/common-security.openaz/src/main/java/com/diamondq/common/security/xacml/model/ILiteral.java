package com.diamondq.common.security.xacml.model;

import org.jetbrains.annotations.Nullable;

public interface ILiteral extends IFunctionArgument {

  public @Nullable String getSingleValue();

  public boolean isBooleanValue();

}

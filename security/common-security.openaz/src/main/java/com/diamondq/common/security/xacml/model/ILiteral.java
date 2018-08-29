package com.diamondq.common.security.xacml.model;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ILiteral extends IFunctionArgument {

  public @Nullable String getSingleValue();

  public boolean isBooleanValue();

}

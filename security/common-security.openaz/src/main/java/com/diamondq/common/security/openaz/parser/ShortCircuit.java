package com.diamondq.common.security.openaz.parser;

import com.diamondq.common.security.xacml.model.ILiteralSingle;
import com.diamondq.common.security.xacml.model.LiteralSingle;

public enum ShortCircuit {

  TRUE, FALSE, NA;

  private static final ILiteralSingle sTRUE  = LiteralSingle.builder().value("true").booleanValue(true).build();

  private static final ILiteralSingle sFALSE = LiteralSingle.builder().value("false").booleanValue(true).build();

  public ILiteralSingle getLiteral() {
    if (this == TRUE)
      return sTRUE;
    else if (this == FALSE)
      return sFALSE;
    else
      throw new IllegalArgumentException();
  }
}

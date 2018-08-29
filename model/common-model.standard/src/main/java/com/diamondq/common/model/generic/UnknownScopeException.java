package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;

public class UnknownScopeException extends RuntimeException {

  private static final long serialVersionUID = 2701948702516077196L;

  public UnknownScopeException(Scope pScope) {
    super("Unknown scope: " + pScope.getName());
  }

}

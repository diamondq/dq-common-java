package com.diamondq.common.utils.scxml.impl;

import java.util.Map;

import org.apache.commons.scxml2.Context;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DQJexlContext extends JexlContext {

  private static final long serialVersionUID = -6746429116329135206L;

  /**
   * Constructor.
   */
  public DQJexlContext() {
    super();
  }

  /**
   * Constructor with initial vars.
   * 
   * @param parent The parent context
   * @param initialVars The initial set of variables.
   */
  public DQJexlContext(final Context parent, final Map<String, Object> initialVars) {
    super(parent, initialVars);
  }

  /**
   * Constructor with parent context.
   *
   * @param parent The parent context.
   */
  public DQJexlContext(final Context parent) {
    super(parent);
  }

  /**
   * @see org.apache.commons.scxml2.env.SimpleContext#has(java.lang.String)
   */
  @SuppressWarnings("null")
  @Override
  public boolean has(String pName) {
    try {
      return super.has(pName) || Class.forName(pName) != null;
    }
    catch (ClassNotFoundException xnf) {
      return false;
    }
  }

  /**
   * @see org.apache.commons.scxml2.env.SimpleContext#get(java.lang.String)
   */
  @Override
  public @Nullable Object get(String pName) {
    try {
      Object found = super.get(pName);
      if (found == null && !super.has(pName)) {
        found = Class.forName(pName);
      }
      return found;
    }
    catch (ClassNotFoundException xnf) {
      return null;
    }
  }
}

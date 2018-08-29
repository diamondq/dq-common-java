package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Toolkit;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class GenericScope implements Scope {

  private final Toolkit mToolkit;

  private final String  mName;

  public GenericScope(Toolkit pToolkit, String pName) {
    mToolkit = pToolkit;
    mName = pName;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Scope#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Scope#getToolkit()
   */
  @Override
  public Toolkit getToolkit() {
    return mToolkit;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mName);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj)
      return true;
    if (pObj == null)
      return false;
    if (getClass() != pObj.getClass())
      return false;
    GenericScope other = (GenericScope) pObj;
    return Objects.equals(mName, other.mName) && Objects.equals(mToolkit, other.mToolkit);
  }

}

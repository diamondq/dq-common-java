package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractRef<A> {

  protected final Scope    mScope;

  protected final Class<A> mActualClass;

  protected final String   mId;

  public AbstractRef(Scope pScope, String pId, Class<A> pActualClass) {
    super();
    mScope = pScope;
    mId = pId;
    mActualClass = pActualClass;
  }

  public String getSerializedString() {
    return mId;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mScope, mId);
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
    @SuppressWarnings("unchecked")
    AbstractRef<A> other = (AbstractRef<A>) pObj;
    return Objects.equals(mScope, other.mScope) && Objects.equals(mId, other.mId)
      && Objects.equals(mActualClass, other.mActualClass);
  }

}

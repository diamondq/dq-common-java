package com.diamondq.common.lambda;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class MemoizedSupplier<TYPE extends @Nullable Object> {

  private final Supplier<TYPE> mSupplier;

  private final transient boolean mIsSupplied;

  private transient @Nullable TYPE mSuppliedValue;

  public MemoizedSupplier(Supplier<TYPE> pSupplier) {
    super();
    mSupplier = pSupplier;
    mIsSupplied = false;
    mSuppliedValue = null;
  }

  @SuppressWarnings("DataFlowIssue")
  public TYPE getValue() {
    synchronized (this) {
      TYPE localSuppliedValue;
      if (mIsSupplied) localSuppliedValue = mSuppliedValue;
      else {
        localSuppliedValue = mSupplier.get();
        mSuppliedValue = localSuppliedValue;
      }
      return localSuppliedValue;
    }
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mSupplier);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj) return true;
    if (pObj == null) return false;
    if (getClass() != pObj.getClass()) return false;
    MemoizedSupplier<?> other = (MemoizedSupplier<?>) pObj;
    return Objects.equals(mSupplier, other.mSupplier);
  }

}

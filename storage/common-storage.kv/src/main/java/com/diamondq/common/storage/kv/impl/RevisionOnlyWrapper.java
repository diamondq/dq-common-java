package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithId;
import com.diamondq.common.storage.kv.IObjectWithIdAndRev;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A wrapper that adds revision information
 *
 * @param <P> the real type
 */
public class RevisionOnlyWrapper<P> implements IObjectWithIdAndRev<RevisionOnlyWrapper<P>> {

  @Nullable
  private String _rev;

  @Nullable
  private P      data;

  /**
   * Default constructor
   */
  public RevisionOnlyWrapper() {
    super();
  }

  /**
   * @see com.diamondq.common.storage.kv.IObjectWithIdAndRev#getObjectRevision()
   */
  @Override
  public @Nullable String getObjectRevision() {
    return _rev;
  }

  /**
   * @see com.diamondq.common.storage.kv.IObjectWithIdAndRev#setObjectRevision(java.lang.String)
   */
  @Override
  public RevisionOnlyWrapper<P> setObjectRevision(String pValue) {
    _rev = pValue;
    return this;
  }

  /**
   * @see com.diamondq.common.storage.kv.IObjectWithId#getObjectId()
   */
  @Override
  public @Nullable String getObjectId() {
    IObjectWithId<?> dataAsObjectWithId = (IObjectWithId<?>) data;
    if (dataAsObjectWithId == null)
      return null;
    return dataAsObjectWithId.getObjectId();
  }

  /**
   * @see com.diamondq.common.storage.kv.IObjectWithId#setObjectId(java.lang.String)
   */
  @Override
  public RevisionOnlyWrapper<P> setObjectId(String pObjectId) {
    IObjectWithId<?> dataAsObjectWithId = (IObjectWithId<?>) data;
    if (dataAsObjectWithId == null)
      return this;
    dataAsObjectWithId.setObjectId(pObjectId);
    return this;
  }

  /**
   * Returns the data
   * 
   * @return the data
   */
  public @Nullable P getData() {
    return data;
  }

  /**
   * Sets the data
   * 
   * @param pValue the data
   * @return the wrapper
   */
  public RevisionOnlyWrapper<P> setData(P pValue) {
    data = pValue;
    return this;
  }
}

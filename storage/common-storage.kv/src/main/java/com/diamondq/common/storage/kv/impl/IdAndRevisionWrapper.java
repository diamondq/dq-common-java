package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithIdAndRev;
import org.jetbrains.annotations.Nullable;

/**
 * Generic implementation of an Id and Revision wrapper
 *
 * @param <P> the actual type
 */
public class IdAndRevisionWrapper<P> implements IObjectWithIdAndRev<IdAndRevisionWrapper<P>> {

  @Nullable private String _id;

  @Nullable private String _rev;

  @Nullable private P data;

  /**
   * Default constructor
   */
  public IdAndRevisionWrapper() {
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
  public IdAndRevisionWrapper<P> setObjectRevision(String pValue) {
    _rev = pValue;
    return this;
  }

  /**
   * @see com.diamondq.common.storage.kv.IObjectWithId#getObjectId()
   */
  @Override
  public @Nullable String getObjectId() {
    return _id;
  }

  /**
   * @see com.diamondq.common.storage.kv.IObjectWithId#setObjectId(java.lang.String)
   */
  @Override
  public IdAndRevisionWrapper<P> setObjectId(String pObjectId) {
    _id = pObjectId;
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
   * Sets the new data
   *
   * @param pValue the data
   * @return the wrapper
   */
  public IdAndRevisionWrapper<P> setData(P pValue) {
    data = pValue;
    return this;
  }
}

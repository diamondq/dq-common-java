package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithId;
import org.jetbrains.annotations.Nullable;

/**
 * Generic implementation for Object with Id
 *
 * @param <P>
 */
public class IdWrapper<P> implements IObjectWithId<IdWrapper<P>> {

  @Nullable private String _id;

  @Nullable private P data;

  /**
   * Default constructor
   */
  public IdWrapper() {
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
  public IdWrapper<P> setObjectId(String pObjectId) {
    _id = pObjectId;
    return this;
  }

  /**
   * Returns the actual data
   *
   * @return the data
   */
  public @Nullable P getData() {
    return data;
  }

  /**
   * Sets the actual data
   *
   * @param pValue the data
   * @return the wrapper
   */
  public IdWrapper<P> setData(P pValue) {
    data = pValue;
    return this;
  }
}

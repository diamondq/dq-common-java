package com.diamondq.common.storage.kv;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This can be attached to a model object to allow the underlying KV store implementation to get access to the id and
 * revision field.
 *
 * @param <O> the class of the actual model object - used in providing type safety during the setObjectId call
 */
public interface IObjectWithIdAndRev<O> extends IObjectWithId<O> {

  /**
   * Returns the object revision.
   * 
   * @return the revision
   */
  public @Nullable String getObjectRevision();

  /**
   * Sets a new object revision
   * 
   * @param pValue the revision
   * @return the updated object (may or may not be the same as the passed in object)
   */
  public O setObjectRevision(String pValue);
}

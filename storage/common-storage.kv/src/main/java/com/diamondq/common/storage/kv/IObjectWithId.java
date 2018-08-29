package com.diamondq.common.storage.kv;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This can be attached to a model object to allow the underlying KV store implementation to get access to the id field.
 *
 * @param <O> the class of the actual model object - used in providing type safety during the setObjectId call
 */
public interface IObjectWithId<O> {

  /**
   * Returns the current object's id value
   * 
   * @return the id (may be null if this object has not yet been persisted, and automatic id generation is in use)
   */
  public @Nullable String getObjectId();

  /**
   * Changes the object's id value. NOTE: Some objects may be read-only, and thus, changing the id generates a new
   * object. In that case, the new object is returned as the result.
   * 
   * @param pObjectId the new object id
   * @return the updated object (may or may not be the same as the passed in object)
   */
  public O setObjectId(String pObjectId);
}

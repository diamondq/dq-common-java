package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

public interface Ref<X> {

  /**
   * Resolves this reference into the object.
   *
   * @return the object
   */
  @Nullable
  X resolve();

  /**
   * Returns a string that can be used to reconstruct this Reference at a later time.
   *
   * @return the serialization of the reference as a string
   */
  String getSerializedString();
}

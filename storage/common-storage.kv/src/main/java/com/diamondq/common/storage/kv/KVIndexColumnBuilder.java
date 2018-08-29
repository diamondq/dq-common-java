package com.diamondq.common.storage.kv;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The Index Column Builder
 *
 * @param <ICB> the actual type of the Index Column Builder
 */
public abstract class KVIndexColumnBuilder<@NonNull ICB extends KVIndexColumnBuilder<@NonNull ICB>> {

  @Nullable
  protected String       mName;

  @Nullable
  protected KVColumnType mType;

  /**
   * Builds the actual Index Column
   * 
   * @return the index column
   */
  public abstract IKVIndexColumn build();

  /**
   * Sets a new name
   * 
   * @param pValue the new name
   * @return the updated builder
   */
  @SuppressWarnings("unchecked")
  public ICB name(String pValue) {
    mName = pValue;
    return (ICB) this;
  }

  /**
   * Sets a new type
   * 
   * @param pValue the new value
   * @return the updated builder
   */
  @SuppressWarnings("unchecked")
  public ICB type(KVColumnType pValue) {
    mType = pValue;
    return (ICB) this;
  }

  protected void validate() {
    if (mName == null)
      throw new IllegalArgumentException(
        "The mandatory field name was not set on the " + this.getClass().getSimpleName());
    if (mType == null)
      throw new IllegalArgumentException(
        "The mandatory field type was not set on the " + this.getClass().getSimpleName());
  }
}

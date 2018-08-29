package com.diamondq.common.storage.kv;

import java.math.BigDecimal;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Abstract Column Definition Builder
 *
 * @param <CDB> the actual type for the Column Definition Builder
 */
public abstract class KVColumnDefinitionBuilder<@NonNull CDB extends KVColumnDefinitionBuilder<@NonNull CDB>> {

  @Nullable
  protected String       mName;

  @Nullable
  protected Integer      mMaxLength;

  @Nullable
  protected BigDecimal   mMinValue;

  @Nullable
  protected BigDecimal   mMaxValue;

  @Nullable
  protected KVColumnType mType;

  /**
   * Sets the name
   * 
   * @param pValue the name
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB name(String pValue) {
    mName = pValue;
    return (CDB) this;
  }

  /**
   * Sets the maximum length
   * 
   * @param pMaxLength the max length
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB maxLength(int pMaxLength) {
    mMaxLength = pMaxLength;
    return (CDB) this;
  }

  /**
   * Sets the type
   * 
   * @param pValue the type
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB type(KVColumnType pValue) {
    mType = pValue;
    return (CDB) this;
  }

  /**
   * Sets the minimum value
   * 
   * @param pValue the min value
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB minValue(BigDecimal pValue) {
    mMinValue = pValue;
    return (CDB) this;
  }

  /**
   * Sets the maximum value
   * 
   * @param pValue the max value
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB maxValue(BigDecimal pValue) {
    mMaxValue = pValue;
    return (CDB) this;
  }

  /**
   * Builds the actual column definition
   * 
   * @return the column definition
   */
  public abstract IKVColumnDefinition build();

}

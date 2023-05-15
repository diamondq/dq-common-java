package com.diamondq.common.storage.kv;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Abstract Column Definition Builder
 *
 * @param <CDB> the actual type for the Column Definition Builder
 */
public abstract class KVColumnDefinitionBuilder<@NotNull CDB extends KVColumnDefinitionBuilder<@NotNull CDB>> {

  protected @Nullable String mName;

  protected @Nullable Integer mMaxLength;

  protected @Nullable BigDecimal mMinValue;

  protected @Nullable BigDecimal mMaxValue;

  protected @Nullable KVColumnType mType;

  protected boolean mIsPrimaryKey = false;

  protected @Nullable BigDecimal mAutoIncrementStart;

  protected @Nullable BigDecimal mAutoIncrementBy;

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
   * Sets that this column is a primary key
   *
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB primaryKey() {
    mIsPrimaryKey = true;
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
   * Sets the autoincrement start value
   *
   * @param pValue the value
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB autoIncrementStart(@Nullable BigDecimal pValue) {
    mAutoIncrementStart = pValue;
    return (CDB) this;
  }

  /**
   * Sets the autoincrement by value
   *
   * @param pValue the value
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public CDB autoIncrementBy(@Nullable BigDecimal pValue) {
    mAutoIncrementBy = pValue;
    return (CDB) this;
  }

  /**
   * Builds the actual column definition
   *
   * @return the column definition
   */
  public abstract IKVColumnDefinition build();

}

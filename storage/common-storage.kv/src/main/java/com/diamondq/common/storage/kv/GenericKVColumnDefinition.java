package com.diamondq.common.storage.kv;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * A generic KVColumnDefinition
 */
public class GenericKVColumnDefinition implements IKVColumnDefinition {

  private final String mName;

  private final KVColumnType mType;

  private final boolean mIsPrimaryKey;

  private final @Nullable Integer mMaxLength;

  private final @Nullable BigDecimal mMinValue;

  private final @Nullable BigDecimal mMaxValue;

  private final @Nullable BigDecimal mAutoIncrementStart;

  private final @Nullable BigDecimal mAutoIncrementBy;

  /**
   * Default constructor
   *
   * @param pName the name
   * @param pType the type
   * @param pIsPrimaryKey true if this is the primary key
   * @param pMaxLength the max length (can be null)
   * @param pMinValue the min value (can be null)
   * @param pMaxValue the max value (can be null)
   * @param pAutoIncrementBy
   * @param pAutoIncrementStart
   */
  public GenericKVColumnDefinition(String pName, KVColumnType pType, boolean pIsPrimaryKey,
    @Nullable Integer pMaxLength, @Nullable BigDecimal pMinValue, @Nullable BigDecimal pMaxValue,
    @Nullable BigDecimal pAutoIncrementStart, @Nullable BigDecimal pAutoIncrementBy) {
    super();
    mName = pName;
    mType = pType;
    mIsPrimaryKey = pIsPrimaryKey;
    mMaxLength = pMaxLength;
    mMinValue = pMinValue;
    mMaxValue = pMaxValue;
    mAutoIncrementStart = pAutoIncrementStart;
    mAutoIncrementBy = pAutoIncrementBy;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#isPrimaryKey()
   */
  @Override
  public boolean isPrimaryKey() {
    return mIsPrimaryKey;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getType()
   */
  @Override
  public KVColumnType getType() {
    return mType;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getMaxLength()
   */
  @Override
  public @Nullable Integer getMaxLength() {
    return mMaxLength;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getMinValue()
   */
  @Override
  public @Nullable BigDecimal getMinValue() {
    return mMinValue;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getMaxValue()
   */
  @Override
  public @Nullable BigDecimal getMaxValue() {
    return mMaxValue;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getAutoIncrementStart()
   */
  @Override
  public @Nullable BigDecimal getAutoIncrementStart() {
    return mAutoIncrementStart;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getAutoIncrementBy()
   */
  @Override
  public @Nullable BigDecimal getAutoIncrementBy() {
    return mAutoIncrementBy;
  }
}

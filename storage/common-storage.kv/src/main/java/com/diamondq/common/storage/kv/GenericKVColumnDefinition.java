package com.diamondq.common.storage.kv;

import java.math.BigDecimal;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A generic KVColumnDefinition
 */
public class GenericKVColumnDefinition implements IKVColumnDefinition {

  private final String       mName;

  private final KVColumnType mType;

  private final boolean      mIsPrimaryKey;

  @Nullable
  private final Integer      mMaxLength;

  @Nullable
  private final BigDecimal   mMinValue;

  @Nullable
  private final BigDecimal   mMaxValue;

  /**
   * Default constructor
   * 
   * @param pName the name
   * @param pType the type
   * @param pIsPrimaryKey true if this is the primary key
   * @param pMaxLength the max length (can be null)
   * @param pMinValue the min value (can be null)
   * @param pMaxValue the max value (can be null)
   */
  public GenericKVColumnDefinition(String pName, KVColumnType pType, boolean pIsPrimaryKey,
    @Nullable Integer pMaxLength, @Nullable BigDecimal pMinValue, @Nullable BigDecimal pMaxValue) {
    super();
    mName = pName;
    mType = pType;
    mIsPrimaryKey = pIsPrimaryKey;
    mMaxLength = pMaxLength;
    mMinValue = pMinValue;
    mMaxValue = pMaxValue;
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

}

package com.diamondq.common.storage.kv;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

/**
 * The definition of a column
 */
public interface IKVColumnDefinition {

  /**
   * Returns the name of the column
   *
   * @return the name
   */
  String getName();

  /**
   * Returns the type of the column
   *
   * @return the type
   */
  KVColumnType getType();

  /**
   * Returns whether this column is part of the primary key
   *
   * @return true or false
   */
  boolean isPrimaryKey();

  /**
   * Returns the maximum length of the column
   *
   * @return the max length (or null if it doesn't apply)
   */
  @Nullable
  Integer getMaxLength();

  /**
   * Returns the minimum value of this column
   *
   * @return the min value (or null if it doesn't apply)
   */
  @Nullable
  BigDecimal getMinValue();

  /**
   * Returns the maximum value of this column
   *
   * @return the max value (or null if it doesn't apply)
   */
  @Nullable
  BigDecimal getMaxValue();

  /**
   * Returns the autoincrement start value
   *
   * @return the value (or null if it doesn't apply)
   */
  @Nullable
  BigDecimal getAutoIncrementStart();

  /**
   * Returns the autoincrement by value
   *
   * @return the value (or null if it doesn't apply)
   */
  @Nullable
  BigDecimal getAutoIncrementBy();

}

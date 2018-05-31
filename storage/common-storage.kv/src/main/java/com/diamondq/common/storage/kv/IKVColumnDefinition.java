package com.diamondq.common.storage.kv;

import java.math.BigDecimal;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The definition of a column
 */
public interface IKVColumnDefinition {

	/**
	 * Returns the name of the column
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the type of the column
	 * 
	 * @return the type
	 */
	public KVColumnType getType();

	/**
	 * Returns the maximum length of the column
	 * 
	 * @return the max length (or null if it doesn't apply)
	 */
	public @Nullable Integer getMaxLength();

	/**
	 * Returns the minimum value of this column
	 * 
	 * @return the min value (or null if it doesn't apply)
	 */
	public @Nullable BigDecimal getMinValue();

	/**
	 * Returns the maximum value of this column
	 * 
	 * @return the max value (or null if it doesn't apply)
	 */
	public @Nullable BigDecimal getMaxValue();
}

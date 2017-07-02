package com.diamondq.common.storage.kv;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Defines a table
 */
public interface IKVTableDefinition {

	/**
	 * Returns the name of the table
	 * 
	 * @return the name
	 */
	public String getTableName();

	/**
	 * Returns the list of columns
	 * 
	 * @return the list of columns
	 */
	public List<@NonNull IKVColumnDefinition> getColumnDefinitions();

}

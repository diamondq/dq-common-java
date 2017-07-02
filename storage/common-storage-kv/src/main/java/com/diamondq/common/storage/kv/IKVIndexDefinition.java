package com.diamondq.common.storage.kv;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Defines a specific index
 */
public interface IKVIndexDefinition {

	/**
	 * The name of the index
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the list of columns in the index in order
	 * 
	 * @return the ordered list of index columns
	 */
	public List<@NonNull IKVIndexColumn> getColumns();
}

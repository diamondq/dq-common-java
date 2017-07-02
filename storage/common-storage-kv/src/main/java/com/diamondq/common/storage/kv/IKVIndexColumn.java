package com.diamondq.common.storage.kv;

/**
 * Defines an index column
 */
public interface IKVIndexColumn {

	/**
	 * The name of the column
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * The type of the column
	 * 
	 * @return the type of column
	 */
	public KVColumnType getType();

}

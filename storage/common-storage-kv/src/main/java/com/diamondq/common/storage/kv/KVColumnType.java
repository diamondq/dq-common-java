package com.diamondq.common.storage.kv;

/**
 * The different supported types of columns within the KV
 */
public enum KVColumnType {
	/**
	 * A string
	 */
	String,
	/**
	 * A boolean
	 */
	Boolean,
	/**
	 * A decimal
	 */
	Decimal,
	/**
	 * An integer (32bit)
	 */
	Integer,
	/**
	 * A full timestamp
	 */
	Timestamp

}

package com.diamondq.common.storage.kv;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An interface representing support for defining tables
 * 
 * @param <TDB> the actual table definition builder type
 * @param <CDB> the actual column definition builder type
 */
public interface IKVTableDefinitionSupport<@NonNull TDB extends KVTableDefinitionBuilder<@NonNull TDB>, @NonNull CDB extends KVColumnDefinitionBuilder<@NonNull CDB>> {

	/**
	 * Returns a table definition builder
	 * 
	 * @return the builder
	 */
	public TDB createTableDefinitionBuilder();

	/**
	 * Returns a new column definition builder
	 * 
	 * @return the builder
	 */
	public CDB createColumnDefinitionBuilder();

	/**
	 * Adds a new table definition to the KV Store
	 * 
	 * @param pDefinition the definition
	 */
	public void addTableDefinition(IKVTableDefinition pDefinition);

}

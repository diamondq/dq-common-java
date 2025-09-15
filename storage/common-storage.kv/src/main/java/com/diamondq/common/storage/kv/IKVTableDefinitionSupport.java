package com.diamondq.common.storage.kv;

/**
 * An interface representing support for defining tables
 *
 * @param <TDB> the actual table definition builder type
 * @param <CDB> the actual column definition builder type
 */
public interface IKVTableDefinitionSupport<TDB extends KVTableDefinitionBuilder<TDB>, CDB extends KVColumnDefinitionBuilder<CDB>> {

  /**
   * Returns a table definition builder
   *
   * @return the builder
   */
  TDB createTableDefinitionBuilder();

  /**
   * Returns a new column definition builder
   *
   * @return the builder
   */
  CDB createColumnDefinitionBuilder();

  /**
   * Adds a new table definition to the KV Store
   *
   * @param pDefinition the definition
   */
  void addTableDefinition(IKVTableDefinition pDefinition);

}

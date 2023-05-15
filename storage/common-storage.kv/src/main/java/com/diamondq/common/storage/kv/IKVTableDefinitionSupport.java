package com.diamondq.common.storage.kv;

import org.jetbrains.annotations.NotNull;

/**
 * An interface representing support for defining tables
 *
 * @param <TDB> the actual table definition builder type
 * @param <CDB> the actual column definition builder type
 */
public interface IKVTableDefinitionSupport<@NotNull TDB extends KVTableDefinitionBuilder<@NotNull TDB>, @NotNull CDB extends KVColumnDefinitionBuilder<@NotNull CDB>> {

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

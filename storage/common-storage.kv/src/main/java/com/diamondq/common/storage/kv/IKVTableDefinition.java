package com.diamondq.common.storage.kv;

import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Defines a table
 */
public interface IKVTableDefinition {

  /**
   * Returns the name of the table
   *
   * @return the name
   */
  String getTableName();

  /**
   * A single primary key is usually stored as the underlying KV stores primary key, and thus the name of the key is
   * lost (since the underlying store likely has it's own fixed name) If there is only a single primary key, then this
   * is the known name for it (or there is no name)
   *
   * @return the name (if available and there is only one primary key)
   */
  @Nullable
  String getSinglePrimaryKeyName();

  /**
   * Returns the list of columns
   *
   * @return the list of columns
   */
  List<IKVColumnDefinition> getColumnDefinitions();

  /**
   * Search for a given definition by name
   *
   * @param pName the name
   * @return the column definition or null
   */
  @Nullable
  IKVColumnDefinition getColumnDefinitionsByName(String pName);

}

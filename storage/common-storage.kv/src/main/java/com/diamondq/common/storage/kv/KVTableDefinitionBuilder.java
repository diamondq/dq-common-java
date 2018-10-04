package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An abstract TableDefinitionBuilder
 *
 * @param <TDB> the actual type of the TableDefinitionBuilder
 */
public abstract class KVTableDefinitionBuilder<@NonNull TDB extends KVTableDefinitionBuilder<@NonNull TDB>> {

  protected @Nullable String                           mTableName;

  protected @Nullable String                           mSinglePrimaryKeyName;

  protected ImmutableList.Builder<IKVColumnDefinition> mColBuilder = ImmutableList.builder();

  /**
   * Builds the actual TableDefinition
   * 
   * @return the table definition
   */
  public abstract IKVTableDefinition build();

  /**
   * Sets the table name
   * 
   * @param pValue the name
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public TDB tableName(String pValue) {
    mTableName = pValue;
    return (TDB) this;
  }

  /**
   * Sets the single primary key column name
   * 
   * @param pValue the name
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public TDB singlePrimaryKeyName(String pValue) {
    mSinglePrimaryKeyName = pValue;
    return (TDB) this;
  }

  /**
   * Adds a column
   * 
   * @param pValue the column definition
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  public TDB addColumn(IKVColumnDefinition pValue) {
    mColBuilder.add(pValue);
    return (TDB) this;
  }
}

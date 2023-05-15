package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder for Index Definitions
 *
 * @param <IDB> the actual type for the Index Definitions builder
 */
public abstract class KVIndexDefinitionBuilder<@NotNull IDB extends KVIndexDefinitionBuilder<IDB>> {

  @Nullable protected String mTableName;

  @Nullable protected String mName;

  protected ImmutableList.Builder<@NotNull IKVIndexColumn> mColumns;

  /**
   * Builds the Index Definition
   *
   * @return the Index definition
   */
  public abstract IKVIndexDefinition build();

  /**
   * Default constructor
   */
  public KVIndexDefinitionBuilder() {
    mColumns = ImmutableList.builder();
  }

  /**
   * Sets the table name
   *
   * @param pValue the table name
   * @return the updated builder
   */
  @SuppressWarnings("unchecked")
  public IDB tableName(String pValue) {
    mTableName = pValue;
    return (IDB) this;
  }

  /**
   * Sets the index name
   *
   * @param pValue the name
   * @return the updated builder
   */
  @SuppressWarnings("unchecked")
  public IDB name(String pValue) {
    mName = pValue;
    return (IDB) this;
  }

  /**
   * Adds a new column to the builder
   *
   * @param pValue the new column
   * @return the updated builder
   */
  @SuppressWarnings("unchecked")
  public IDB addColumn(IKVIndexColumn pValue) {
    mColumns.add(pValue);
    return (IDB) this;
  }

  protected void validate() {
    if (mTableName == null) throw new IllegalArgumentException(
      "The mandatory field table name was not set on the " + this.getClass().getSimpleName());
    if (mName == null) throw new IllegalArgumentException(
      "The mandatory field name was not set on the " + this.getClass().getSimpleName());
  }
}

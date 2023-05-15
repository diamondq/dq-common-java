package com.diamondq.common.storage.kv;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main interface representing a Key/Value store
 */
public interface IKVStore {

  /**
   * Starts a new transaction.
   *
   * @return the transaction
   */
  public IKVTransaction startTransaction();

  /**
   * Starts an asynchronous transaction
   *
   * @return the transaction
   */
  public IKVAsyncTransaction startAsyncTransaction();

  /**
   * Returns a supporting interface for manipulate indexes if this store supports data based indexes.
   *
   * @return returns the interface if it does or null if it does not
   */
  public <@NotNull ICB extends @NotNull KVIndexColumnBuilder<ICB>, @NotNull IDB extends @NotNull KVIndexDefinitionBuilder<IDB>> @Nullable IKVIndexSupport<ICB, IDB> getIndexSupport();

  /**
   * Returns a supporting interface for manipulating table definitions, if this store supports dynamic table
   * definitions.
   *
   * @return returns the interface if it does support it or null if it does not
   */
  public <@NotNull TDB extends @NotNull KVTableDefinitionBuilder<TDB>, @NotNull CDB extends @NotNull KVColumnDefinitionBuilder<CDB>> @Nullable IKVTableDefinitionSupport<TDB, CDB> getTableDefinitionSupport();
}

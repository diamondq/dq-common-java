package com.diamondq.common.storage.kv;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
  public <@NonNull ICB extends @NonNull KVIndexColumnBuilder<ICB>, @NonNull IDB extends @NonNull KVIndexDefinitionBuilder<IDB>> @Nullable IKVIndexSupport<ICB, IDB> getIndexSupport();

  /**
   * Returns a supporting interface for manipulating table definitions, if this store supports dynamic table
   * definitions.
   * 
   * @return returns the interface if it does support it or null if it does not
   */
  public <@NonNull TDB extends @NonNull KVTableDefinitionBuilder<TDB>, @NonNull CDB extends @NonNull KVColumnDefinitionBuilder<CDB>> @Nullable IKVTableDefinitionSupport<TDB, CDB> getTableDefinitionSupport();
}

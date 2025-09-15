package com.diamondq.common.storage.kv;

import org.jspecify.annotations.Nullable;

/**
 * The main interface representing a Key/Value store
 */
public interface IKVStore {

  /**
   * Starts a new transaction.
   *
   * @return the transaction
   */
  IKVTransaction startTransaction();

  /**
   * Starts an asynchronous transaction
   *
   * @return the transaction
   */
  IKVAsyncTransaction startAsyncTransaction();

  /**
   * Returns a supporting interface for manipulate indexes if this store supports data-based indexes.
   *
   * @return returns the interface if it does or null if it does not
   */
  <ICB extends KVIndexColumnBuilder<ICB>, IDB extends KVIndexDefinitionBuilder<IDB>> @Nullable IKVIndexSupport<ICB, IDB> getIndexSupport();

  /**
   * Returns a supporting interface for manipulating table definitions if this store supports dynamic table
   * definitions.
   *
   * @return returns the interface if it does support it or null if it does not
   */
  <TDB extends KVTableDefinitionBuilder<TDB>, CDB extends KVColumnDefinitionBuilder<CDB>> @Nullable IKVTableDefinitionSupport<TDB, CDB> getTableDefinitionSupport();
}

package com.diamondq.common.storage.kv;

import java.util.Collection;

/**
 * An interface that represents Index support for the KV store
 *
 * @param <ICB> the type representing a KVIndexColumnBuilder
 * @param <IDB> the type representing a KVIndexDefinitionBuilder
 */
public interface IKVIndexSupport<ICB extends KVIndexColumnBuilder<ICB>, IDB extends KVIndexDefinitionBuilder<IDB>> {

  /**
   * This tells the store the set of indexes that are required. If these indexes are already created, then nothing
   * occurs.
   *
   * @param pIndexes the collection of index definitions
   */
  void addRequiredIndexes(Collection<IKVIndexDefinition> pIndexes);

  /**
   * Returns a new Index Column Builder
   *
   * @return the builder
   */
  ICB createIndexColumnBuilder();

  /**
   * Returns a new Index Definition Builder
   *
   * @return the builder
   */
  IDB createIndexDefinitionBuilder();
}

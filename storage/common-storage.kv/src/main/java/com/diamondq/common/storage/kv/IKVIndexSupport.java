package com.diamondq.common.storage.kv;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An interface that represents Index support for the KV store
 *
 * @param <ICB> the type representing a KVIndexColumnBuilder
 * @param <IDB> the type representing a KVIndexDefinitionBuilder
 */
public interface IKVIndexSupport<@NotNull ICB extends KVIndexColumnBuilder<@NotNull ICB>, @NotNull IDB extends KVIndexDefinitionBuilder<@NotNull IDB>> {

  /**
   * This tells the store the set of indexes that are required. If these indexes are already created, then nothing
   * occurs.
   *
   * @param pIndexes the collection of index definitions
   */
  public void addRequiredIndexes(Collection<@NotNull IKVIndexDefinition> pIndexes);

  /**
   * Returns a new Index Column Builder
   *
   * @return the builder
   */
  public ICB createIndexColumnBuilder();

  /**
   * Returns a new Index Definition Builder
   *
   * @return the builder
   */
  public IDB createIndexDefinitionBuilder();
}

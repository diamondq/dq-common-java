package com.diamondq.common.storage.kv;

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An interface that represents Index support for the KV store
 * 
 * @param <ICB> the type representing a KVIndexColumnBuilder
 * @param <IDB> the type representing a KVIndexDefinitionBuilder
 */
public interface IKVIndexSupport<@NonNull ICB extends KVIndexColumnBuilder<@NonNull ICB>, @NonNull IDB extends KVIndexDefinitionBuilder<@NonNull IDB>> {

	/**
	 * This tells the store the set of indexes that are required. If these indexes are already created, then nothing
	 * occurs.
	 * 
	 * @param pIndexes the collection of index definitions
	 */
	public void addRequiredIndexes(Collection<@NonNull IKVIndexDefinition> pIndexes);

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

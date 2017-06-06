package com.diamondq.common.storage.kv;

import java.util.Collection;

public interface IKVIndexSupport<ICB extends KVIndexColumnBuilder<ICB>, IDB extends KVIndexDefinitionBuilder<IDB>> {

	/**
	 * This tells the store the set of indexes that are required. If these indexes are already created, then nothing
	 * occurs.
	 * 
	 * @param pIndexes the collection of index definitions
	 */
	public void addRequiredIndexes(Collection<IKVIndexDefinition> pIndexes);

	public ICB createIndexColumnBuilder();

	public IDB createIndexDefinitionBuilder();
}

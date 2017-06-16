package com.diamondq.common.storage.kv;

public interface IKVStore {

	public IKVTransaction startTransaction();

	public IKVAsyncTransaction startAsyncTransaction();

	/**
	 * Returns a supporting interface for manipulate indexes if this store supports data based indexes.
	 * 
	 * @return returns the interface if it does or null if it does not
	 */
	public <ICB extends KVIndexColumnBuilder<ICB>, IDB extends KVIndexDefinitionBuilder<IDB>> IKVIndexSupport<ICB, IDB> getIndexSupport();

	/**
	 * Returns a supporting interface for manipulating table definitions, if this store supports dynamic table
	 * definitions.
	 * 
	 * @return returns the interface if it does support it or null if it does not
	 */
	public IKVTableDefinitionSupport getTableDefinitionSupport();
}

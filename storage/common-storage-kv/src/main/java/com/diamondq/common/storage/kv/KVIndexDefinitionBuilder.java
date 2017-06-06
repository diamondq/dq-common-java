package com.diamondq.common.storage.kv;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class KVIndexDefinitionBuilder<IDB extends KVIndexDefinitionBuilder<IDB>> {

	protected String				mName;

	protected List<IKVIndexColumn>	mColumns;

	public abstract IKVIndexDefinition build();

	public KVIndexDefinitionBuilder() {
		mColumns = Lists.newArrayList();
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
}

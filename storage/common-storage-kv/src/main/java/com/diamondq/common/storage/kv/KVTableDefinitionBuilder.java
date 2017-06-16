package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;

public abstract class KVTableDefinitionBuilder {

	protected String										mTableName;

	protected ImmutableList.Builder<IKVColumnDefinition>	mColBuilder	= ImmutableList.builder();

	public abstract IKVTableDefinition build();

	public KVTableDefinitionBuilder tableName(String pValue) {
		mTableName = pValue;
		return this;
	}

	public KVTableDefinitionBuilder addColumn(IKVColumnDefinition pValue) {
		mColBuilder.add(pValue);
		return this;
	}
}

package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.IKVTableDefinition;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class GenericKVTableDefinition implements IKVTableDefinition {

	private final String					mTableName;

	private final List<IKVColumnDefinition>	mColumnDefinitions;

	public GenericKVTableDefinition(String pTableName, List<IKVColumnDefinition> pColumnDefinitions) {
		super();
		mTableName = pTableName;
		mColumnDefinitions =
			(pColumnDefinitions == null ? ImmutableList.of() : ImmutableList.copyOf(pColumnDefinitions));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTableDefinition#getTableName()
	 */
	@Override
	public String getTableName() {
		return mTableName;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTableDefinition#getColumnDefinitions()
	 */
	@Override
	public List<IKVColumnDefinition> getColumnDefinitions() {
		return mColumnDefinitions;
	}

}

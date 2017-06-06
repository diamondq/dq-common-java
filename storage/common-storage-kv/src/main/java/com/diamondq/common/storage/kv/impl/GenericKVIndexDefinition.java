package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IKVIndexColumn;
import com.diamondq.common.storage.kv.IKVIndexDefinition;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class GenericKVIndexDefinition implements IKVIndexDefinition {

	private final String				mName;

	private final List<IKVIndexColumn>	mColumns;

	public GenericKVIndexDefinition(String pName, List<IKVIndexColumn> pColumns) {
		super();
		mName = pName;
		mColumns = (pColumns == null ? ImmutableList.of() : ImmutableList.copyOf(pColumns));
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public List<IKVIndexColumn> getColumns() {
		return mColumns;
	}

}

package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Generic implementation of an Index Definition
 */
public class GenericKVIndexDefinition implements IKVIndexDefinition {

	private final String						mName;

	private final List<@NonNull IKVIndexColumn>	mColumns;

	/**
	 * Default constructor
	 * 
	 * @param pName the name
	 * @param pColumns the list of columns
	 */
	public GenericKVIndexDefinition(String pName, List<@NonNull IKVIndexColumn> pColumns) {
		super();
		mName = pName;
		mColumns = ImmutableList.copyOf(pColumns);
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexDefinition#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexDefinition#getColumns()
	 */
	@Override
	public List<@NonNull IKVIndexColumn> getColumns() {
		return mColumns;
	}

}

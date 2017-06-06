package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IKVIndexColumn;

public class GenericKVIndexColumn implements IKVIndexColumn {

	private final String	mName;

	private final String	mType;

	public GenericKVIndexColumn(String pName, String pType) {
		mName = pName;
		mType = pType;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexColumn#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexColumn#getType()
	 */
	@Override
	public String getType() {
		return mType;
	}

}

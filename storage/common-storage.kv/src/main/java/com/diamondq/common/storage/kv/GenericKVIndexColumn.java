package com.diamondq.common.storage.kv;

/**
 * Generic implementation of the KVIndexColumn
 */
public class GenericKVIndexColumn implements IKVIndexColumn {

	private final String		mName;

	private final KVColumnType	mType;

	/**
	 * Default constructor
	 * 
	 * @param pName the name
	 * @param pType the type
	 */
	public GenericKVIndexColumn(String pName, KVColumnType pType) {
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
	public KVColumnType getType() {
		return mType;
	}

}

package com.diamondq.common.storage.kv;

public abstract class KVIndexColumnBuilder<ICB extends KVIndexColumnBuilder<ICB>> {

	protected String	mName;

	protected String	mType;

	public abstract IKVIndexColumn build();

	/**
	 * Sets a new name
	 * 
	 * @param pValue the new name
	 * @return the updated builder
	 */
	@SuppressWarnings("unchecked")
	public ICB name(String pValue) {
		mName = pValue;
		return (ICB) this;
	}

	/**
	 * Sets a new type
	 * 
	 * @param pValue the new value
	 * @return the updated builder
	 */
	@SuppressWarnings("unchecked")
	public ICB type(String pValue) {
		mType = pValue;
		return (ICB) this;
	}
}

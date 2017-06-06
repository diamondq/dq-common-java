package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithIdAndRev;

public class IdAndRevisionWrapper<P> implements IObjectWithIdAndRev<IdAndRevisionWrapper<P>> {

	private String	_id;

	private String	_rev;

	private P		data;

	public IdAndRevisionWrapper() {
		super();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IObjectWithIdAndRev#getObjectRevision()
	 */
	@Override
	public String getObjectRevision() {
		return _rev;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IObjectWithIdAndRev#setObjectRevision(java.lang.String)
	 */
	@Override
	public IdAndRevisionWrapper<P> setObjectRevision(String pValue) {
		_rev = pValue;
		return this;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IObjectWithId#getObjectId()
	 */
	@Override
	public String getObjectId() {
		return _id;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IObjectWithId#setObjectId(java.lang.String)
	 */
	@Override
	public IdAndRevisionWrapper<P> setObjectId(String pObjectId) {
		_id = pObjectId;
		return this;
	}

	public P getData() {
		return data;
	}

	public IdAndRevisionWrapper<P> setData(P pValue) {
		data = pValue;
		return this;
	}
}

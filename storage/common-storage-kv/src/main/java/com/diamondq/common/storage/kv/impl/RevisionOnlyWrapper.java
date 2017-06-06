package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithId;
import com.diamondq.common.storage.kv.IObjectWithIdAndRev;

public class RevisionOnlyWrapper<P> implements IObjectWithIdAndRev<RevisionOnlyWrapper<P>> {

	private String	_rev;

	private P		data;

	public RevisionOnlyWrapper() {
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
	public RevisionOnlyWrapper<P> setObjectRevision(String pValue) {
		_rev = pValue;
		return this;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IObjectWithId#getObjectId()
	 */
	@Override
	public String getObjectId() {
		return ((IObjectWithId<?>) data).getObjectId();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IObjectWithId#setObjectId(java.lang.String)
	 */
	@Override
	public RevisionOnlyWrapper<P> setObjectId(String pObjectId) {
		((IObjectWithId<?>) data).setObjectId(pObjectId);
		return this;
	}

	public P getData() {
		return data;
	}

	public RevisionOnlyWrapper<P> setData(P pValue) {
		data = pValue;
		return this;
	}
}

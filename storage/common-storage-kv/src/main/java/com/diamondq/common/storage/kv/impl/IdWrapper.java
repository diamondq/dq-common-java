package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IObjectWithId;

public class IdWrapper<P> implements IObjectWithId<IdWrapper<P>> {

	private String	_id;

	private P		data;

	public IdWrapper() {

	}

	@Override
	public String getObjectId() {
		return _id;
	}

	@Override
	public IdWrapper<P> setObjectId(String pObjectId) {
		_id = pObjectId;
		return this;
	}

	public P getData() {
		return data;
	}

	public IdWrapper<P> setData(P pValue) {
		data = pValue;
		return this;
	}
}

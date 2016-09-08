package com.diamondq.common.storage.kv;

public interface IKVStore {

	public IKVTransaction startTransaction();
}

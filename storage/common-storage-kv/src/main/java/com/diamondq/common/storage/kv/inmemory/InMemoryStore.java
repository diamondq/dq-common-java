package com.diamondq.common.storage.kv.inmemory;

import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTransaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryStore implements IKVStore {

	private final ConcurrentMap<String, ConcurrentMap<String, String>> mData = new ConcurrentHashMap<>();

	public InMemoryStore() {
	}

	@Override
	public IKVTransaction startTransaction() {
		return new InMemoryKVTransaction(mData);
	}

}

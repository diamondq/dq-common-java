package com.diamondq.common.storage.kv.inmemory;

import com.diamondq.common.storage.kv.IKVAsyncTransaction;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.impl.SyncWrapperAsyncKVTransaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryStore implements IKVStore {

	private final ConcurrentMap<String, ConcurrentMap<String, String>> mData = new ConcurrentHashMap<>();

	public InMemoryStore() {
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#startTransaction()
	 */
	@Override
	public IKVTransaction startTransaction() {
		return new InMemoryKVTransaction(mData);
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#startAsyncTransaction()
	 */
	@Override
	public IKVAsyncTransaction startAsyncTransaction() {
		return new SyncWrapperAsyncKVTransaction(startTransaction());
	}

}

package com.diamondq.common.storage.kv.inmemory.impl;

import com.diamondq.common.storage.kv.IKVAsyncTransaction;
import com.diamondq.common.storage.kv.IKVIndexSupport;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTableDefinitionSupport;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.KVColumnDefinitionBuilder;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;
import com.diamondq.common.storage.kv.SyncWrapperAsyncKVTransaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A KVStore where all the information is kept in memory
 */
public class InMemoryStore implements IKVStore {

	private final ConcurrentMap<String, ConcurrentMap<String, @Nullable String>> mData = new ConcurrentHashMap<>();

	/**
	 * Default constructor
	 */
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

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#getIndexSupport()
	 */
	@Override
	public <ICB extends @NonNull KVIndexColumnBuilder<@NonNull ICB>, IDB extends @NonNull KVIndexDefinitionBuilder<@NonNull IDB>> @Nullable IKVIndexSupport<@NonNull ICB, @NonNull IDB> getIndexSupport() {
		return null;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore
	 */
	@Override
	public <TDB extends @NonNull KVTableDefinitionBuilder<@NonNull TDB>, CDB extends @NonNull KVColumnDefinitionBuilder<@NonNull CDB>> @Nullable IKVTableDefinitionSupport<@NonNull TDB, @NonNull CDB> getTableDefinitionSupport() {
		return null;
	}
}

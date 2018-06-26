package com.diamondq.common.storage.kv.inmemory.impl;

import com.diamondq.common.storage.kv.AbstractKVTransaction;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.jdt.annotation.NonNull;

/**
 * The main transaction class for the inmemory store
 */
public class InMemoryKVTransaction extends AbstractKVTransaction implements IKVTransaction {

	private final ConcurrentMap<String, ConcurrentMap<String, @Nullable String>> mData;

	/**
	 * Default constructor
	 * 
	 * @param pData the main data (from the store). This is just a reference
	 */
	public InMemoryKVTransaction(ConcurrentMap<String, ConcurrentMap<String, @Nullable String>> pData) {
		super();
		mData = pData;
	}

	protected ConcurrentMap<String, @Nullable String> getFromTable(String pTable) {
		ConcurrentMap<String, @Nullable String> map = mData.get(pTable);
		if (map == null) {
			ConcurrentMap<String, @Nullable String> newMap = new ConcurrentHashMap<>();
			if ((map = mData.putIfAbsent(pTable, newMap)) == null)
				map = newMap;
		}
		return map;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#getByKey(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	public <@Nullable O> O getByKey(String pTable, String pKey1, @Nullable String pKey2, Class<O> pClass) {
		ConcurrentMap<String, @Nullable String> table = getFromTable(pTable);
		String key = getFlattenedKey(pKey1, pKey2);
		String result = table.get(key);
		return getObjFromString(pTable, pClass, result);
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#putByKey(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public <@Nullable O> void putByKey(String pTable, String pKey1, @Nullable String pKey2, O pObj) {
		ConcurrentMap<String, @Nullable String> table = getFromTable(pTable);
		String key = getFlattenedKey(pKey1, pKey2);
		table.put(key, getStringFromObj(pTable, pObj));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#removeByKey(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean removeByKey(String pTable, String pKey1, @Nullable String pKey2) {
		ConcurrentMap<String, @Nullable String> table = getFromTable(pTable);
		String key = getFlattenedKey(pKey1, pKey2);
		return table.remove(key) != null;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator(java.lang.String)
	 */
	@Override
	public Iterator<@NonNull String> keyIterator(String pTable) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator2(java.lang.String, java.lang.String)
	 */
	@Override
	public Iterator<@NonNull String> keyIterator2(String pTable, String pKey1) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#clear(java.lang.String)
	 */
	@Override
	public void clear(String pTable) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#getCount(java.lang.String)
	 */
	@Override
	public long getCount(String pTable) {
		ConcurrentMap<String, @Nullable String> table = getFromTable(pTable);
		return table.size();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#getTableList()
	 */
	@Override
	public Iterator<@NonNull String> getTableList() {
		return Sets.newHashSet(mData.keySet()).iterator();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#commit()
	 */
	@Override
	public void commit() {
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#rollback()
	 */
	@Override
	public void rollback() {
	}

}

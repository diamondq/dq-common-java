package com.diamondq.common.storage.kv.inmemory;

import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.impl.AbstractKVTransaction;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryKVTransaction extends AbstractKVTransaction implements IKVTransaction {

	private final ConcurrentMap<String, ConcurrentMap<String, String>> mData;

	public InMemoryKVTransaction(ConcurrentMap<String, ConcurrentMap<String, String>> pData) {
		super();
		mData = pData;
	}

	protected ConcurrentMap<String, String> getFromTable(String pTable) {
		ConcurrentMap<String, String> map = mData.get(pTable);
		if (map == null) {
			ConcurrentMap<String, String> newMap = new ConcurrentHashMap<>();
			if ((map = mData.putIfAbsent(pTable, newMap)) == null)
				map = newMap;
		}
		return map;
	}

	@Override
	public <O> O getByKey(String pTable, String pKey1, String pKey2, Class<O> pClass) {
		ConcurrentMap<String, String> table = getFromTable(pTable);
		String key = getFlattenedKey(pKey1, pKey2);
		String result = table.get(key);
		return getObjFromString(pTable, pClass, result);
	}

	@Override
	public <O> void putByKey(String pTable, String pKey1, String pKey2, O pObj) {
		ConcurrentMap<String, String> table = getFromTable(pTable);
		String key = getFlattenedKey(pKey1, pKey2);
		table.put(key, getStringFromObj(pTable, pObj));
	}

	@Override
	public boolean removeByKey(String pTable, String pKey1, String pKey2) {
		ConcurrentMap<String, String> table = getFromTable(pTable);
		String key = getFlattenedKey(pKey1, pKey2);
		return table.remove(key) != null;
	}

	@Override
	public Iterator<String> keyIterator(String pTable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> keyIterator2(String pTable, String pKey1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear(String pTable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getCount(String pTable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> getTableList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void commit() {
	}

	@Override
	public void rollback() {
	}

}

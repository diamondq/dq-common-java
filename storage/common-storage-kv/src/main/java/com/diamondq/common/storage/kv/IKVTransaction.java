package com.diamondq.common.storage.kv;

import java.util.Iterator;

public interface IKVTransaction {

	public <O> O getByKey(String pTable, String pKey1, String pKey2, Class<O> pClass);

	public <O> void putByKey(String pTable, String pKey1, String pKey2, O pObj);

	public boolean removeByKey(String pTable, String pKey1, String pKey2);

	public Iterator<String> keyIterator(String pTable);

	public Iterator<String> keyIterator2(String pTable, String pKey1);

	public void clear(String pTable);

	public long getCount(String pTable);

	public Iterator<String> getTableList();

	public void commit();

	public void rollback();
}

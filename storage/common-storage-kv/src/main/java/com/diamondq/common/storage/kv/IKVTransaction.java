package com.diamondq.common.storage.kv;

import java.util.Iterator;

public interface IKVTransaction {

	/**
	 * Attempts to get the object stored at the given table/key1/key2 location.
	 * 
	 * @param pTable the table
	 * @param pKey1 the first key
	 * @param pKey2 the second key (can be null, but is effectively the same as if it was __NULL__).
	 * @param pClass the class of the object to retrieve
	 * @return the retrieved object or null if it doesn't exist
	 */
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

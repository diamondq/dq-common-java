package com.diamondq.common.storage.kv;

import com.google.common.collect.Iterators;

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

	/**
	 * Returns an iterator that returns all the distinct key 1's within the table. NOTE: It is critically important that
	 * the iterator is fully consumed, otherwise, some implementations may leak heavily (ie. not closing a
	 * PreparedStatement or ResultSet). Use {@link Iterators#size(Iterator)} or something similar to consume if it isn't
	 * already consumed by your code.
	 * 
	 * @param pTable the table
	 * @return an iterator of keys
	 */
	public Iterator<String> keyIterator(String pTable);

	/**
	 * Returns an iterator that returns all the distinct key 2's within the table/key1. NOTE: It is critically important
	 * that the iterator is fully consumed, otherwise, some implementations may leak heavily (ie. not closing a
	 * PreparedStatement or ResultSet). Use {@link Iterators#size(Iterator)} or something similar to consume if it isn't
	 * already consumed by your code.
	 * 
	 * @param pTable the table
	 * @param pKey1 the key 1
	 * @return an iterator of keys
	 */
	public Iterator<String> keyIterator2(String pTable, String pKey1);

	public void clear(String pTable);

	public long getCount(String pTable);

	public Iterator<String> getTableList();

	public void commit();

	public void rollback();
}

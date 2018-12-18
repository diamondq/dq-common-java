package com.diamondq.common.storage.kv;

import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents an ongoing, synchronous, transaction
 */
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
  public <O> @Nullable O getByKey(String pTable, String pKey1, @Nullable String pKey2, Class<O> pClass);

  /**
   * Stores a new value
   * 
   * @param pTable the table
   * @param pKey1 the first key
   * @param pKey2 the second key (can be null, but is effectively the same as if it was __NULL__).
   * @param pObj the value to store (can be null, but it's the same as calling removeByKey)
   */
  public <@Nullable O> void putByKey(String pTable, String pKey1, @Nullable String pKey2, O pObj);

  /**
   * Removes a value
   * 
   * @param pTable the table
   * @param pKey1 the first key
   * @param pKey2 the second key (can be null, but is effectively the same as if it was __NULL__).
   * @return true if the object existed and was removed or false if it never existed to begin with
   */
  public boolean removeByKey(String pTable, String pKey1, @Nullable String pKey2);

  /**
   * Returns an iterator that returns all the distinct key 1's within the table. NOTE: It is critically important that
   * the iterator is fully consumed, otherwise, some implementations may leak heavily (ie. not closing a
   * PreparedStatement or ResultSet). Use {@link Iterators#size(Iterator)} or something similar to consume if it isn't
   * already consumed by your code.
   * 
   * @param pTable the table
   * @return an iterator of keys
   */
  public Iterator<@NonNull String> keyIterator(String pTable);

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
  public Iterator<@NonNull String> keyIterator2(String pTable, String pKey1);

  /**
   * Clears all the contents of the table
   * 
   * @param pTable the table
   */
  public void clear(String pTable);

  /**
   * Returns the number of entries in the table
   * 
   * @param pTable the table
   * @return the count
   */
  public long getCount(String pTable);

  /**
   * Returns the list of tables
   * 
   * @return the tables
   */
  public Iterator<@NonNull String> getTableList();

  /**
   * Commits the changes in this transaction
   */
  public void commit();

  /**
   * Rolls back the changes in this transaction
   */
  public void rollback();

  /**
   * Executes the given query
   * 
   * @param pQuery the query
   * @param pClass the result class
   * @param pParamValues the parameters
   * @return the result list
   */
  public <O> List<O> executeQuery(Query pQuery, Class<O> pClass, Map<String, Object> pParamValues);

  /**
   * Executes the given query but returns the count of records
   * 
   * @param pQuery the query
   * @param pParamValues the parameters
   * @return the count
   */
  public int countQuery(Query pQuery, Map<String, Object> pParamValues);
}

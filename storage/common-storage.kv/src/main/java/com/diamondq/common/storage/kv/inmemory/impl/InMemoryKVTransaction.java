package com.diamondq.common.storage.kv.inmemory.impl;

import com.diamondq.common.storage.kv.AbstractKVTransaction;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.Query;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
      if ((map = mData.putIfAbsent(pTable, newMap)) == null) map = newMap;
    }
    return map;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#getByKey(java.lang.String, java.lang.String, java.lang.String,
   *   java.lang.Class)
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
   *   java.lang.Object)
   */
  @Override
  public <@Nullable O> void putByKey(String pTable, String pKey1, @Nullable String pKey2, O pObj) {
    ConcurrentMap<String, @Nullable String> table = getFromTable(pTable);
    String key = getFlattenedKey(pKey1, pKey2);
    table.put(key, getStringFromObj(pTable, pObj));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#removeByKey(java.lang.String, java.lang.String,
   *   java.lang.String)
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
  public Iterator<@NotNull String> keyIterator(String pTable) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator2(java.lang.String, java.lang.String)
   */
  @Override
  public Iterator<@NotNull String> keyIterator2(String pTable, String pKey1) {
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
  public Iterator<@NotNull String> getTableList() {
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

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#executeQuery(com.diamondq.common.storage.kv.Query,
   *   java.lang.Class, java.util.Map)
   */
  @Override
  public <O> List<O> executeQuery(Query pQuery, Class<O> pClass, Map<String, Object> pParamValues) {
    String tableName = pQuery.getDefinitionName();
    ConcurrentMap<String, @Nullable String> table = getFromTable(tableName);
    ImmutableList.Builder<O> builder = ImmutableList.builder();
    for (@Nullable String value : table.values()) {
      if (value == null) continue;
      @SuppressWarnings("unused") O obj = getObjFromString(tableName, pClass, value);

      /* Not sure how to do the search at this point */

      throw new UnsupportedOperationException();
    }

    return builder.build();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#countQuery(com.diamondq.common.storage.kv.Query, java.util.Map)
   */
  @Override
  public int countQuery(Query pQuery, Map<String, Object> pParamValues) {
    throw new UnsupportedOperationException();
  }
}

package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVTableDefinition;
import com.diamondq.common.storage.kv.Query;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JDBCTableInfo {

  /**
   * Assumes that param 1 is key1 and param 2 is key 2. Assumes that the ResultSet can be handled by the
   * {@link #deserializer}.
   */
  public final String getBySQL;

  public final boolean supportsUpsert;

  /**
   * Assumes that param 1 is key1, param 2 is key2, and that other parameters will be filled by the {@link #serializer}
   */
  public final String putBySQL;

  /**
   * Assumes that param 1 is key1, param 2 is key2. Assumes that the ResultSet returns a row if the record exists and
   * doesn't return anything if it doesn't.
   */
  public final String putQueryBySQL;

  /**
   * Assumes that param 1 is key1, param 2 is key2, and that other parameters will be filled by the {@link #serializer}
   */
  public final String putInsertBySQL;

  /**
   * Assumes that the first set of strings will be filled by the {@link #serializer}, and the last param is key2 and the
   * second-to-last param is key1
   */
  public final String putUpdateBySQL;

  public final IResultSetDeserializer deserializer;

  public final IPreparedStatementSerializer serializer;

  /**
   * Assumes that param 1 is key1, param 2 is key2
   */
  public final String removeBySQL;

  /**
   * No parameters. Assumes that the ResultSet has one parameter and it's a long representing the row count
   */
  public final String getCountSQL;

  /**
   * No parameters
   */
  public final String clearSQL;

  /**
   * No parameters. Assumes that the ResultSet has one parameter and it's a string representing key 1
   */
  public final String keyIteratorSQL;

  /**
   * Assumes that param 1 is key1. Assumes that the ResultSet has one parameter and it's a string representing key2
   */
  public final String keyIterator2SQL;

  /**
   * A Map for caching the query SQL for a given Query object
   */
  public final ConcurrentMap<Query, String> querySQL;

  /**
   * A Map for caching the query SQL for a given Query object
   */
  public final ConcurrentMap<Query, String> countSQL;

  /**
   * The actual munged table name (without schema)
   */
  public final String mungedTableName;

  /**
   * The table schema
   */
  public final @Nullable String tableSchema;

  /**
   * The table definition
   */
  public final IKVTableDefinition definition;

  public JDBCTableInfo(String pGetBySQL, boolean pSupportsUpsert, String pPutBySQL, String pPutQueryBySQL,
    String pPutInsertBySQL, String pPutUpdateBySQL, IResultSetDeserializer pDeserializer,
    IPreparedStatementSerializer pSerializer, String pRemoveBySQL, String pGetCountSQL, String pClearSQL,
    String pKeyIteratorSQL, String pKeyIterator2SQL, String pMungedTableName, @Nullable String pTableSchema,
    IKVTableDefinition pDefinition) {
    super();
    getBySQL = pGetBySQL;
    supportsUpsert = pSupportsUpsert;
    putBySQL = pPutBySQL;
    putQueryBySQL = pPutQueryBySQL;
    putInsertBySQL = pPutInsertBySQL;
    putUpdateBySQL = pPutUpdateBySQL;
    deserializer = pDeserializer;
    serializer = pSerializer;
    removeBySQL = pRemoveBySQL;
    getCountSQL = pGetCountSQL;
    clearSQL = pClearSQL;
    keyIteratorSQL = pKeyIteratorSQL;
    keyIterator2SQL = pKeyIterator2SQL;
    mungedTableName = pMungedTableName;
    tableSchema = pTableSchema;
    definition = pDefinition;
    querySQL = new ConcurrentHashMap<>();
    countSQL = new ConcurrentHashMap<>();
  }

}

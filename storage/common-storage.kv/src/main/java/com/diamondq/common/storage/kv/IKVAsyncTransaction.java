package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

/**
 * An async transaction provides the same functionality as the transaction, but all methods are completed in the future
 */
public interface IKVAsyncTransaction {

  /**
   * Returns the value by key in the future
   * 
   * @param pTable the table
   * @param pKey1 the first key
   * @param pKey2 the second key (optional, can be null)
   * @param pClass the class of the result
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable O, @Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<O, CONTEXT>> getByKey(String pTable,
    String pKey1, @Nullable String pKey2, Class<O> pClass, CONTEXT pContext);

  /**
   * Stores a new value by key in the future
   * 
   * @param pTable the table
   * @param pKey1 the first key
   * @param pKey2 the second key (optional, can be null)
   * @param pObj the value to store (can be null, which is the equivalent of removing the key)
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable O, @Nullable CONTEXT> ExtendedCompletableFuture<CONTEXT> putByKey(String pTable, String pKey1,
    @Nullable String pKey2, O pObj, CONTEXT pContext);

  /**
   * Removes a value by key in the future
   * 
   * @param pTable the table
   * @param pKey1 the first key
   * @param pKey2 the second key (optional, can be null)
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<@NonNull Boolean, CONTEXT>> removeByKey(
    String pTable, String pKey1, @Nullable String pKey2, CONTEXT pContext);

  /**
   * Returns an iterator that goes over all the distinct first keys within the table in the future
   * 
   * @param pTable the table
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<Iterator<String>, CONTEXT>> keyIterator(
    String pTable, CONTEXT pContext);

  /**
   * Returns an iterator that goes over all the second keys for a given first key in the future
   * 
   * @param pTable the table
   * @param pKey1 the first key
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<@NonNull Iterator<@NonNull String>, CONTEXT>> keyIterator2(
    String pTable, String pKey1, CONTEXT pContext);

  /**
   * Clears the entire table of values in the future
   * 
   * @param pTable the table
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<CONTEXT> clear(String pTable, CONTEXT pContext);

  /**
   * Returns the number of entries within the table in the future
   * 
   * @param pTable the table
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<@NonNull Long, CONTEXT>> getCount(String pTable,
    CONTEXT pContext);

  /**
   * Returns the list of tables in the future
   * 
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<@NonNull Iterator<@NonNull String>, CONTEXT>> getTableList(
    CONTEXT pContext);

  /**
   * Commits this transaction in the future
   * 
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<CONTEXT> commit(CONTEXT pContext);

  /**
   * Rolls back the changes to this transaction in the future
   * 
   * @param pContext the context to pass to the future (can be null)
   * @return the future
   */
  public <@Nullable CONTEXT> ExtendedCompletableFuture<CONTEXT> rollback(CONTEXT pContext);

}

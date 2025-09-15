package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.LambdaExceptionUtil;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;

/**
 * Wrapper that takes an existing Synchronous Transaction and provides an Asynchronous Transaction. Of course, all
 * methods actually happen during the call, and the completed (or errored) promise is return.
 */
public class SyncWrapperAsyncKVTransaction extends AbstractKVTransaction implements IKVAsyncTransaction {

  private final IKVTransaction mTransaction;

  /**
   * Default constructor
   *
   * @param pTransaction the synchronous transaction
   */
  public SyncWrapperAsyncKVTransaction(IKVTransaction pTransaction) {
    mTransaction = pTransaction;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#getByKey(java.lang.String, java.lang.String,
   *   java.lang.String, java.lang.Class, java.lang.Object)
   */
  @Override
  public <O extends @Nullable Object, CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<@Nullable O, CONTEXT>> getByKey(
    String pTable, String pKey1, @Nullable String pKey2, Class<O> pClass, CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncSupplierResult(() -> Pair.with(mTransaction.getByKey(pTable,
        pKey1,
        pKey2,
        pClass
      ), pContext
    ));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#putByKey(java.lang.String, java.lang.String,
   *   java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public <O extends @Nullable Object, CONTEXT extends @Nullable Object> ExtendedCompletableFuture<CONTEXT> putByKey(
    String pTable, String pKey1, @Nullable String pKey2, O pObj, CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
      mTransaction.putByKey(pTable, pKey1, pKey2, pObj);
      return pContext;
    });
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#removeByKey(java.lang.String, java.lang.String,
   *   java.lang.String, java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<Boolean, CONTEXT>> removeByKey(String pTable,
    String pKey1, @Nullable String pKey2, CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncNonNullSupplierResult(() -> Pair.with(mTransaction.removeByKey(pTable,
        pKey1,
        pKey2
      ), pContext
    ));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#keyIterator(java.lang.String, java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> keyIterator(
    String pTable, CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncNonNullSupplierResult(() -> Pair.with(mTransaction.keyIterator(pTable),
      pContext
    ));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#keyIterator2(java.lang.String, java.lang.String,
   *   java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> keyIterator2(
    String pTable, String pKey1, CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncNonNullSupplierResult(() -> Pair.with(mTransaction.keyIterator2(pTable, pKey1),
      pContext
    ));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#clear(java.lang.String, java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<CONTEXT> clear(String pTable, CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
      mTransaction.clear(pTable);
      return pContext;
    });
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#getCount(java.lang.String, java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<Long, CONTEXT>> getCount(String pTable,
    CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncNonNullSupplierResult(() -> Pair.with(mTransaction.getCount(pTable), pContext));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#getTableList(java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> getTableList(
    CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncNonNullSupplierResult(() -> Pair.with(mTransaction.getTableList(), pContext));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#commit(java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<CONTEXT> commit(CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
      mTransaction.commit();
      return pContext;
    });
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#rollback(java.lang.Object)
   */
  @Override
  public <CONTEXT extends @Nullable Object> ExtendedCompletableFuture<CONTEXT> rollback(CONTEXT pContext) {
    return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
      mTransaction.rollback();
      return pContext;
    });
  }

}

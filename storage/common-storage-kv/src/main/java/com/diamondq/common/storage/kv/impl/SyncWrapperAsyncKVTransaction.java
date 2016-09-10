package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.lambda.LambdaExceptionUtil;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.storage.kv.IKVAsyncTransaction;
import com.diamondq.common.storage.kv.IKVTransaction;

import java.util.Iterator;

import org.javatuples.Pair;

public class SyncWrapperAsyncKVTransaction extends AbstractKVTransaction implements IKVAsyncTransaction {

	private final IKVTransaction mTransaction;

	public SyncWrapperAsyncKVTransaction(IKVTransaction pTransaction) {
		mTransaction = pTransaction;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#getByKey(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.Class, java.lang.Object)
	 */
	@Override
	public <O, CONTEXT> ExtendedCompletableFuture<Pair<O, CONTEXT>> getByKey(String pTable, String pKey1, String pKey2,
		Class<O> pClass, CONTEXT pContext) {
		return LambdaExceptionUtil
			.wrapSyncSupplierResult(() -> Pair.with(mTransaction.getByKey(pTable, pKey1, pKey2, pClass), pContext));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#putByKey(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <O, CONTEXT> ExtendedCompletableFuture<CONTEXT> putByKey(String pTable, String pKey1, String pKey2, O pObj,
		CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
			mTransaction.putByKey(pTable, pKey1, pKey2, pObj);
			return pContext;
		});
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#removeByKey(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<Pair<Boolean, CONTEXT>> removeByKey(String pTable, String pKey1,
		String pKey2, CONTEXT pContext) {
		return LambdaExceptionUtil
			.wrapSyncSupplierResult(() -> Pair.with(mTransaction.removeByKey(pTable, pKey1, pKey2), pContext));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#keyIterator(java.lang.String, java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> keyIterator(String pTable,
		CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> Pair.with(mTransaction.keyIterator(pTable), pContext));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#keyIterator2(java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> keyIterator2(String pTable,
		String pKey1, CONTEXT pContext) {
		return LambdaExceptionUtil
			.wrapSyncSupplierResult(() -> Pair.with(mTransaction.keyIterator2(pTable, pKey1), pContext));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#clear(java.lang.String, java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<CONTEXT> clear(String pTable, CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
			mTransaction.clear(pTable);
			return pContext;
		});
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#getCount(java.lang.String, java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<Pair<Long, CONTEXT>> getCount(String pTable, CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> Pair.with(mTransaction.getCount(pTable), pContext));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#getTableList(java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> getTableList(CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> Pair.with(mTransaction.getTableList(), pContext));
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#commit(java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<CONTEXT> commit(CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
			mTransaction.commit();
			return pContext;
		});
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVAsyncTransaction#rollback(java.lang.Object)
	 */
	@Override
	public <CONTEXT> ExtendedCompletableFuture<CONTEXT> rollback(CONTEXT pContext) {
		return LambdaExceptionUtil.wrapSyncSupplierResult(() -> {
			mTransaction.rollback();
			return pContext;
		});
	}

}

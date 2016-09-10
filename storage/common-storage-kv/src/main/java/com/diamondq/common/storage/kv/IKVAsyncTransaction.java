package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Iterator;

import org.javatuples.Pair;

public interface IKVAsyncTransaction {

	public <O, CONTEXT> ExtendedCompletableFuture<Pair<O, CONTEXT>> getByKey(String pTable, String pKey1, String pKey2,
		Class<O> pClass, CONTEXT pContext);

	public <O, CONTEXT> ExtendedCompletableFuture<CONTEXT> putByKey(String pTable, String pKey1, String pKey2, O pObj,
		CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<Pair<Boolean, CONTEXT>> removeByKey(String pTable, String pKey1,
		String pKey2, CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> keyIterator(String pTable,
		CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> keyIterator2(String pTable,
		String pKey1, CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<CONTEXT> clear(String pTable, CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<Pair<Long, CONTEXT>> getCount(String pTable, CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<Pair<Iterator<String>, CONTEXT>> getTableList(CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<CONTEXT> commit(CONTEXT pContext);

	public <CONTEXT> ExtendedCompletableFuture<CONTEXT> rollback(CONTEXT pContext);

}

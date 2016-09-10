package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.function.Function;

import org.javatuples.Pair;

public abstract class TransactionUtil {

	private TransactionUtil() {
	}

	public static <T, CONTEXT> ExtendedCompletableFuture<Pair<T, CONTEXT>> runInTransaction(IKVStore pStore,
		Function<IKVAsyncTransaction, ExtendedCompletableFuture<Pair<T, CONTEXT>>> pSupplier) {
		IKVAsyncTransaction transaction = pStore.startAsyncTransaction();
		try {
			return pSupplier.apply(transaction).thenCompose(p -> transaction.commit(p));
		}
		catch (RuntimeException ex) {
			return transaction.rollback(null)
				.thenCompose(a -> ExtendedCompletableFuture.<Pair<T, CONTEXT>> completedFailure(ex));
		}

	}
}

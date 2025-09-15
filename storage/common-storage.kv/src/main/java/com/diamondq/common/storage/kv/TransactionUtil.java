package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * Transaction utilities
 */
public abstract class TransactionUtil {

  private TransactionUtil() {
  }

  /**
   * Runs the function inside the transaction
   *
   * @param pStore the store
   * @param pSupplier the supplier
   * @return the future
   */
  public static <T extends @Nullable Object, CONTEXT extends @Nullable Object> ExtendedCompletableFuture<Pair<T, CONTEXT>> runInTransaction(
    IKVStore pStore, Function<IKVAsyncTransaction, @Nullable ExtendedCompletableFuture<Pair<T, CONTEXT>>> pSupplier) {
    IKVAsyncTransaction transaction = pStore.startAsyncTransaction();
    try {
      ExtendedCompletableFuture<Pair<@Nullable T, @Nullable CONTEXT>> supplierResult = pSupplier.apply(transaction);
      if (supplierResult == null) throw new IllegalArgumentException();
      @SuppressWarnings(
        "null") ExtendedCompletableFuture<Pair<@Nullable T, @Nullable CONTEXT>> composeResult = supplierResult.thenCompose(
        transaction::commit);
      return composeResult;
    }
    catch (RuntimeException ex) {
      @SuppressWarnings(
        "null") ExtendedCompletableFuture<Pair<@Nullable T, @Nullable CONTEXT>> composeResult = transaction.rollback(
          null)
        .thenCompose(a -> ExtendedCompletableFuture.<@Nullable Pair<@Nullable T, @Nullable CONTEXT>>completedFailure(ex));
      return composeResult;
    }

  }
}

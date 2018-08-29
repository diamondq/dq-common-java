package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

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
  public static <@Nullable T, @Nullable CONTEXT> ExtendedCompletableFuture<@NonNull Pair<T, CONTEXT>> runInTransaction(
    IKVStore pStore,
    Function<@NonNull IKVAsyncTransaction, @Nullable ExtendedCompletableFuture<@NonNull Pair<T, CONTEXT>>> pSupplier) {
    IKVAsyncTransaction transaction = pStore.startAsyncTransaction();
    try {
      @Nullable
      ExtendedCompletableFuture<@NonNull Pair<@Nullable T, @Nullable CONTEXT>> supplierResult =
        pSupplier.apply(transaction);
      if (supplierResult == null)
        throw new IllegalArgumentException();
      @SuppressWarnings("null")
      ExtendedCompletableFuture<@NonNull Pair<@Nullable T, @Nullable CONTEXT>> composeResult =
        (ExtendedCompletableFuture<@NonNull Pair<@Nullable T, @Nullable CONTEXT>>) supplierResult
          .thenCompose(p -> transaction.commit(p));
      return composeResult;
    }
    catch (RuntimeException ex) {
      @SuppressWarnings("null")
      ExtendedCompletableFuture<@NonNull Pair<@Nullable T, @Nullable CONTEXT>> composeResult =
        (ExtendedCompletableFuture<@NonNull Pair<@Nullable T, @Nullable CONTEXT>>) transaction.rollback(null)
          .thenCompose(
            a -> ExtendedCompletableFuture.<@Nullable Pair<@Nullable T, @Nullable CONTEXT>> completedFailure(ex));
      return composeResult;
    }

  }
}

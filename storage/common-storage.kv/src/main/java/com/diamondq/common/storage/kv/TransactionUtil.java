package com.diamondq.common.storage.kv;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  public static <@Nullable T, @Nullable CONTEXT> ExtendedCompletableFuture<@NotNull Pair<T, CONTEXT>> runInTransaction(
    IKVStore pStore,
    Function<@NotNull IKVAsyncTransaction, @Nullable ExtendedCompletableFuture<@NotNull Pair<T, CONTEXT>>> pSupplier) {
    IKVAsyncTransaction transaction = pStore.startAsyncTransaction();
    try {
      @Nullable ExtendedCompletableFuture<@NotNull Pair<@Nullable T, @Nullable CONTEXT>> supplierResult = pSupplier.apply(
        transaction);
      if (supplierResult == null) throw new IllegalArgumentException();
      @SuppressWarnings(
        "null") ExtendedCompletableFuture<@NotNull Pair<@Nullable T, @Nullable CONTEXT>> composeResult = (ExtendedCompletableFuture<@NotNull Pair<@Nullable T, @Nullable CONTEXT>>) supplierResult.thenCompose(
        p -> transaction.commit(p));
      return composeResult;
    }
    catch (RuntimeException ex) {
      @SuppressWarnings(
        "null") ExtendedCompletableFuture<@NotNull Pair<@Nullable T, @Nullable CONTEXT>> composeResult = (ExtendedCompletableFuture<@NotNull Pair<@Nullable T, @Nullable CONTEXT>>) transaction.rollback(
          null)
        .thenCompose(a -> ExtendedCompletableFuture.<@Nullable Pair<@Nullable T, @Nullable CONTEXT>>completedFailure(ex));
      return composeResult;
    }

  }
}

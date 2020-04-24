package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

/**
 * This abstract class represents a type that only syncs from a->b.
 *
 * @param <T>
 * @param <T_KEY>
 */
public abstract class AbstractOneWaySyncSimpleSyncInfo<T, T_KEY>
  extends AbstractSameABTypeNoFragTypeSyncInfo<T, T_KEY> {

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#getAStatus(java.lang.Object)
   */
  @Override
  public boolean getAStatus(T_KEY pKey) {

    /* We don't modify A, so the status for A is always true */

    return true;
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#getBStatus(java.lang.Object)
   */
  @Override
  public boolean getBStatus(T_KEY pKey) {

    /* We don't modify A, so the status for B is always false */
    return false;
  }

  public abstract int doCompare(T pA, T pB);

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public final int compare(T pA, T pB) {
    int result = doCompare(pA, pB);

    /* If there is a difference, then we'll always say that B needs to be updated */

    if (result > 0)
      result = -1;

    return result;
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#createA(java.util.stream.Stream)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> createA(Stream<Pair<T_KEY, T>> pStream) {
    if (pStream.count() != 0)
      throw new IllegalStateException();
    return ExtendedCompletableFuture.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#deleteA(java.util.stream.Stream)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> deleteA(Stream<Pair<T_KEY, T>> pStream) {
    if (pStream.count() != 0)
      throw new IllegalStateException();
    return ExtendedCompletableFuture.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#modifyA(java.util.stream.Stream)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> modifyA(Stream<Pair<T_KEY, T>> pStream) {
    if (pStream.count() != 0)
      throw new IllegalStateException();
    return ExtendedCompletableFuture.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.utils.sync.abstracts.AbstractSameABTypeSyncInfo#merge(java.lang.Object, java.lang.Object,
   *      java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
   */
  @Override
  protected T merge(T_KEY pAKey, T pAFrag, T pA, T_KEY pBKey, T pBFrag, T pB) {
    return pA;
  }

}

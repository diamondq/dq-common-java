package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class ComparableSyncInfo<T extends Comparable<T>, T_KEY> extends AbstractOneWaySyncSimpleSyncInfo<T, T_KEY> {

  private final Map<@NonNull T_KEY, @NonNull T> mAMap;

  private final Map<@NonNull T_KEY, @NonNull T> mBMap;

  public ComparableSyncInfo(Map<@NonNull T_KEY, @NonNull T> pAMap, Map<@NonNull T_KEY, @NonNull T> pBMap) {
    mAMap = pAMap;
    mBMap = pBMap;
  }

  public ComparableSyncInfo(Collection<@NonNull T> pACollection, Collection<@NonNull T> pBCollection,
    Function<@NonNull T, @NonNull T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  public ComparableSyncInfo(T[] pACollection, T[] pBCollection, Function<@NonNull T, @NonNull T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  @Override
  public ExtendedCompletableFuture<Map<@NonNull T_KEY, @NonNull T>> getASource() {
    return ExtendedCompletableFuture.completedFuture(mAMap);
  }

  @Override
  public ExtendedCompletableFuture<Map<@NonNull T_KEY, @NonNull T>> getBSource() {
    return ExtendedCompletableFuture.completedFuture(mBMap);
  }

  /**
   * @see com.diamondq.common.utils.sync.abstracts.AbstractOneWaySyncSimpleSyncInfo#doCompare(java.lang.Object,
   *      java.lang.Object)
   */
  @Override
  public int doCompare(T pA, T pB) {
    return pA.compareTo(pB);
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#createB(java.util.stream.Stream)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> createB(Stream<Pair<T_KEY, T>> pStream) {
    return ExtendedCompletableFuture.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#deleteB(java.util.stream.Stream)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> deleteB(Stream<Pair<T_KEY, T>> pStream) {
    return ExtendedCompletableFuture.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.utils.sync.SyncInfo#modifyB(java.util.stream.Stream)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> modifyB(Stream<Pair<T_KEY, T>> pStream) {
    return ExtendedCompletableFuture.completedFuture(null);
  }

}

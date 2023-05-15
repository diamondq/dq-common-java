package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class ComparableSyncInfo<T extends Comparable<T>, T_KEY> extends AbstractOneWaySyncSimpleSyncInfo<T, T_KEY> {

  private final Map<@NotNull T_KEY, @NotNull T> mAMap;

  private final Map<@NotNull T_KEY, @NotNull T> mBMap;

  public ComparableSyncInfo(Map<@NotNull T_KEY, @NotNull T> pAMap, Map<@NotNull T_KEY, @NotNull T> pBMap) {
    mAMap = pAMap;
    mBMap = pBMap;
  }

  public ComparableSyncInfo(Collection<@NotNull T> pACollection, Collection<@NotNull T> pBCollection,
    Function<@NotNull T, @NotNull T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  public ComparableSyncInfo(T[] pACollection, T[] pBCollection, Function<@NotNull T, @NotNull T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  @Override
  public ExtendedCompletableFuture<Map<@NotNull T_KEY, @NotNull T>> getASource() {
    return ExtendedCompletableFuture.completedFuture(mAMap);
  }

  @Override
  public ExtendedCompletableFuture<Map<@NotNull T_KEY, @NotNull T>> getBSource() {
    return ExtendedCompletableFuture.completedFuture(mBMap);
  }

  /**
   * @see com.diamondq.common.utils.sync.abstracts.AbstractOneWaySyncSimpleSyncInfo#doCompare(java.lang.Object,
   *   java.lang.Object)
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

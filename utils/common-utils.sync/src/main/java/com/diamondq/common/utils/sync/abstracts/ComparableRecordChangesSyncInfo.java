package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ComparableRecordChangesSyncInfo<T extends Comparable<T>, T_KEY>
  extends AbstractRecordChangesSimpleSyncInfo<T, T_KEY> {

  private final Map<@NonNull T_KEY, @NonNull T> mAMap;

  private final Map<@NonNull T_KEY, @NonNull T> mBMap;

  public ComparableRecordChangesSyncInfo(Map<@NonNull T_KEY, @NonNull T> pAMap, Map<@NonNull T_KEY, @NonNull T> pBMap) {
    mAMap = pAMap;
    mBMap = pBMap;
  }

  public ComparableRecordChangesSyncInfo(Collection<@NonNull T> pACollection, Collection<@NonNull T> pBCollection,
    Function<@NonNull T, @NonNull T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  public ComparableRecordChangesSyncInfo(T[] pACollection, T[] pBCollection,
    Function<@NonNull T, @NonNull T_KEY> pToKey) {
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

}

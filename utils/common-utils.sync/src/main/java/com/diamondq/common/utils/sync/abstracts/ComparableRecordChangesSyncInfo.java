package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ComparableRecordChangesSyncInfo<T extends Comparable<T>, T_KEY>
  extends AbstractRecordChangesSimpleSyncInfo<T, T_KEY> {

  private final Map<T_KEY, T> mAMap;

  private final Map<T_KEY, T> mBMap;

  public ComparableRecordChangesSyncInfo(Map<T_KEY, T> pAMap, Map<T_KEY, T> pBMap) {
    mAMap = pAMap;
    mBMap = pBMap;
  }

  public ComparableRecordChangesSyncInfo(Collection<T> pACollection, Collection<T> pBCollection,
    Function<T, T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  public ComparableRecordChangesSyncInfo(T[] pACollection, T[] pBCollection, Function<T, T_KEY> pToKey) {
    mAMap = new HashMap<>();
    mBMap = new HashMap<>();
    for (T t : pACollection)
      mAMap.put(pToKey.apply(t), t);
    for (T t : pBCollection)
      mBMap.put(pToKey.apply(t), t);
  }

  @Override
  public ExtendedCompletableFuture<Map<T_KEY, T>> getASource() {
    return ExtendedCompletableFuture.completedFuture(mAMap);
  }

  @Override
  public ExtendedCompletableFuture<Map<T_KEY, T>> getBSource() {
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

}

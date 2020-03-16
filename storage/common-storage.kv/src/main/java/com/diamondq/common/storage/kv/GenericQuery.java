package com.diamondq.common.storage.kv;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class GenericQuery implements Query {

  protected final String                               mDefinitionName;

  protected final String                               mQueryName;

  protected final ImmutableList<WhereInfo>             mWhereList;

  protected final @Nullable String                     mParentParamKey;

  protected final @Nullable String                     mParentName;

  protected final ImmutableList<Pair<String, Boolean>> mSortList;

  protected final @Nullable String                     mLimitKey;

  public GenericQuery(String pDefinitionName, String pQueryName, @Nullable List<WhereInfo> pWhereList,
    @Nullable String pParentParamKey, @Nullable String pParentName, @Nullable List<Pair<String, Boolean>> pSortList,
    @Nullable String pLimitKey) {
    mDefinitionName = pDefinitionName;
    mQueryName = pQueryName;
    mWhereList = (pWhereList == null ? ImmutableList.of() : ImmutableList.copyOf(pWhereList));
    mParentParamKey = pParentParamKey;
    mParentName = pParentName;
    mSortList = (pSortList == null ? ImmutableList.of() : ImmutableList.copyOf(pSortList));
    mLimitKey = pLimitKey;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getQueryName()
   */
  @Override
  public String getQueryName() {
    return mQueryName;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getWhereList()
   */
  @Override
  public List<WhereInfo> getWhereList() {
    return mWhereList;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getParentParamKey()
   */
  @Override
  @Nullable
  public String getParentParamKey() {
    return mParentParamKey;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getSortList()
   */
  @Override
  public List<Pair<String, Boolean>> getSortList() {
    return mSortList;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getLimitKey()
   */
  @Override
  public @Nullable String getLimitKey() {
    return mLimitKey;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getDefinitionName()
   */
  @Override
  public String getDefinitionName() {
    return mDefinitionName;
  }

  /**
   * @see com.diamondq.common.storage.kv.Query#getParentName()
   */
  @Override
  public @Nullable String getParentName() {
    return mParentName;
  }

  @SuppressWarnings("null")
  protected ToStringHelper toStringHelper(ToStringHelper pHelper) {
    return pHelper //
      .add("definitionName", mDefinitionName) //
      .add("queryName", mQueryName) //
      .add("parentParamKey", mParentParamKey) //
      .add("parentName", mParentName) //
      .add("whereList", mWhereList) //
      .add("sortList", mSortList) //
      .add("limitKey", mLimitKey);
  }

  @Override
  public String toString() {
    return toStringHelper(MoreObjects.toStringHelper(this)).toString();
  }

}

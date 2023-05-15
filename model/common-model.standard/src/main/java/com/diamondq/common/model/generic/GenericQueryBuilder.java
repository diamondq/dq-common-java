package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.storage.kv.WhereInfo;
import com.diamondq.common.storage.kv.WhereOperator;
import com.google.common.collect.ImmutableList;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericQueryBuilder implements QueryBuilder {

  private final StructureDefinition mStructureDefinition;

  private final String mQueryName;

  private final ImmutableList<WhereInfo> mWhereList;

  private final @Nullable String mParentParamKey;

  private final @Nullable PropertyDefinition mParentPropertyDefinition;

  private final ImmutableList<Pair<String, Boolean>> mSortList;

  private final @Nullable String mLimitKey;

  public GenericQueryBuilder(StructureDefinition pStructureDefinition, String pQueryName,
    @Nullable List<WhereInfo> pWhereList, @Nullable String pParentParamKey,
    @Nullable PropertyDefinition pParentPropertyDefinition, @Nullable List<Pair<String, Boolean>> pSortList,
    @Nullable String pLimitKey) {
    mStructureDefinition = pStructureDefinition;
    mQueryName = pQueryName;
    mWhereList = (pWhereList == null ? ImmutableList.of() : ImmutableList.copyOf(pWhereList));
    mParentParamKey = pParentParamKey;
    mParentPropertyDefinition = pParentPropertyDefinition;
    mSortList = (pSortList == null ? ImmutableList.of() : ImmutableList.copyOf(pSortList));
    mLimitKey = pLimitKey;
  }

  public StructureDefinition getStructureDefinition() {
    return mStructureDefinition;
  }

  public String getQueryName() {
    return mQueryName;
  }

  /**
   * Returns the where list
   *
   * @return the list
   */
  List<WhereInfo> getWhereList() {
    return mWhereList;
  }

  @Nullable String getParentParamKey() {
    return mParentParamKey;
  }

  @Nullable PropertyDefinition getParentPropertyDefinition() {
    return mParentPropertyDefinition;
  }

  List<Pair<String, Boolean>> getSortList() {
    return mSortList;
  }

  @Nullable String getLimitKey() {
    return mLimitKey;
  }

  /**
   * @see com.diamondq.common.model.interfaces.QueryBuilder#andWhereConstant(java.lang.String,
   *   com.diamondq.common.storage.kv.WhereOperator, java.lang.Object)
   */
  @Override
  public GenericQueryBuilder andWhereConstant(String pKey, WhereOperator pOperator, Object pValue) {
    return new GenericQueryBuilder(mStructureDefinition,
      mQueryName,
      ImmutableList.<WhereInfo>builder().addAll(mWhereList).add(new WhereInfo(pKey, pOperator, pValue, null)).build(),
      mParentParamKey,
      mParentPropertyDefinition,
      mSortList,
      mLimitKey
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.QueryBuilder#andWhereParam(java.lang.String,
   *   com.diamondq.common.storage.kv.WhereOperator, java.lang.String)
   */
  @Override
  public GenericQueryBuilder andWhereParam(String pKey, WhereOperator pOperator, String pParamKey) {
    return new GenericQueryBuilder(mStructureDefinition,
      mQueryName,
      ImmutableList.<WhereInfo>builder()
        .addAll(mWhereList)
        .add(new WhereInfo(pKey, pOperator, null, pParamKey))
        .build(),
      mParentParamKey,
      mParentPropertyDefinition,
      mSortList,
      mLimitKey
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.QueryBuilder#andWhereParentIs(java.lang.String,
   *   com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public QueryBuilder andWhereParentIs(String pParentParamKey, PropertyDefinition pParentPropertyDef) {
    return new GenericQueryBuilder(mStructureDefinition,
      mQueryName,
      mWhereList,
      pParentParamKey,
      pParentPropertyDef,
      mSortList,
      mLimitKey
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.QueryBuilder#orderBy(java.lang.String, boolean)
   */
  @Override
  public QueryBuilder orderBy(String pKey, boolean pIsAscending) {
    return new GenericQueryBuilder(mStructureDefinition,
      mQueryName,
      mWhereList,
      mParentParamKey,
      mParentPropertyDefinition,
      ImmutableList.<Pair<String, Boolean>>builder().addAll(mSortList).add(Pair.with(pKey, pIsAscending)).build(),
      mLimitKey
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.QueryBuilder#limit(java.lang.String)
   */
  @Override
  public QueryBuilder limit(String pParamKey) {
    return new GenericQueryBuilder(mStructureDefinition,
      mQueryName,
      mWhereList,
      mParentParamKey,
      mParentPropertyDefinition,
      mSortList,
      pParamKey
    );
  }
}

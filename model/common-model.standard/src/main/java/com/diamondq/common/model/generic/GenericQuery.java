package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Query;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.WhereOperator;
import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class GenericQuery implements Query {

  public static class GenericWhereInfo {
    public final String        key;

    public final WhereOperator operator;

    @Nullable
    public final Object        constant;

    @Nullable
    public final String        paramKey;

    public GenericWhereInfo(String pKey, WhereOperator pOperator, @Nullable Object pConstant,
      @Nullable String pParamKey) {
      super();
      key = pKey;
      operator = pOperator;
      constant = pConstant;
      paramKey = pParamKey;
    }
  }

  private final StructureDefinition                  mStructureDefinition;

  private final String                               mQueryName;

  private final ImmutableList<GenericWhereInfo>      mWhereList;

  private final @Nullable String                     mParentParamKey;

  private final @Nullable PropertyDefinition         mParentPropertyDefinition;

  private final ImmutableList<Pair<String, Boolean>> mSortList;

  public GenericQuery(StructureDefinition pStructureDefinition, String pQueryName,
    @Nullable List<GenericWhereInfo> pWhereList, @Nullable String pParentParamKey,
    @Nullable PropertyDefinition pParentPropertyDefinition, @Nullable List<Pair<String, Boolean>> pSortList) {
    mStructureDefinition = pStructureDefinition;
    mQueryName = pQueryName;
    mWhereList = (pWhereList == null ? ImmutableList.of() : ImmutableList.copyOf(pWhereList));
    mParentParamKey = pParentParamKey;
    mParentPropertyDefinition = pParentPropertyDefinition;
    mSortList = (pSortList == null ? ImmutableList.of() : ImmutableList.copyOf(pSortList));
  }

  /**
   * @see com.diamondq.common.model.interfaces.Query#getStructureDefinition()
   */
  @Override
  public StructureDefinition getStructureDefinition() {
    return mStructureDefinition;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Query#getQueryName()
   */
  @Override
  public String getQueryName() {
    return mQueryName;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Query#getWhereList()
   */
  @Override
  public List<GenericWhereInfo> getWhereList() {
    return mWhereList;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Query#getParentParamKey()
   */
  @Override
  @Nullable
  public String getParentParamKey() {
    return mParentParamKey;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Query#getParentPropertyDefinition()
   */
  @Override
  @Nullable
  public PropertyDefinition getParentPropertyDefinition() {
    return mParentPropertyDefinition;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Query#getSortList()
   */
  @Override
  public List<Pair<String, Boolean>> getSortList() {
    return mSortList;
  }

}

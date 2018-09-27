package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.GenericQuery.GenericWhereInfo;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Query;
import com.diamondq.common.model.interfaces.StructureDefinition;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class CombinedQuery implements Query {

  private Query                        mFirstQuery;

  private Map<PersistenceLayer, Query> mMappedQueries;

  public CombinedQuery(Query pFirstQuery, Map<PersistenceLayer, Query> pMappedQueries) {
    mFirstQuery = pFirstQuery;
    mMappedQueries = pMappedQueries;
  }

  public Map<PersistenceLayer, Query> getMappedQueries() {
    return mMappedQueries;
  }

  @Override
  public StructureDefinition getStructureDefinition() {
    return mFirstQuery.getStructureDefinition();
  }

  @Override
  public String getQueryName() {
    return mFirstQuery.getQueryName();
  }

  @Override
  public List<GenericWhereInfo> getWhereList() {
    return mFirstQuery.getWhereList();
  }

  @Override
  public @Nullable String getParentParamKey() {
    return mFirstQuery.getParentParamKey();
  }

  @Override
  public @Nullable PropertyDefinition getParentPropertyDefinition() {
    return mFirstQuery.getParentPropertyDefinition();
  }

  @Override
  public List<Pair<String, Boolean>> getSortList() {
    return mFirstQuery.getSortList();
  }

}

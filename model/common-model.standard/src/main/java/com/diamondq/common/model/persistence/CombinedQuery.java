package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.ModelQuery;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.storage.kv.WhereInfo;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class CombinedQuery implements ModelQuery {

  private ModelQuery                        mFirstQuery;

  private Map<PersistenceLayer, ModelQuery> mMappedQueries;

  public CombinedQuery(ModelQuery pFirstQuery, Map<PersistenceLayer, ModelQuery> pMappedQueries) {
    mFirstQuery = pFirstQuery;
    mMappedQueries = pMappedQueries;
  }

  public Map<PersistenceLayer, ModelQuery> getMappedQueries() {
    return mMappedQueries;
  }

  @Override
  public String getDefinitionName() {
    return mFirstQuery.getDefinitionName();
  }

  @Override
  public @Nullable String getParentName() {
    return mFirstQuery.getParentName();
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
  public List<WhereInfo> getWhereList() {
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

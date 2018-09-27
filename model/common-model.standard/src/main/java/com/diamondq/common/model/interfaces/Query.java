package com.diamondq.common.model.interfaces;

import com.diamondq.common.model.generic.GenericQuery.GenericWhereInfo;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public interface Query {

  public StructureDefinition getStructureDefinition();

  public String getQueryName();

  /**
   * Returns the where list
   *
   * @return the list
   */
  public List<GenericWhereInfo> getWhereList();

  @Nullable
  public String getParentParamKey();

  @Nullable
  public PropertyDefinition getParentPropertyDefinition();

  public List<Pair<String, Boolean>> getSortList();

}

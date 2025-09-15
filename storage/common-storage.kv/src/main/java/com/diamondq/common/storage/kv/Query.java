package com.diamondq.common.storage.kv;

import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface Query {

  /**
   * Returns the name of the 'object type' that this Query is attached to (usually the StructureDefinition name)
   *
   * @return the name
   */
  String getDefinitionName();

  /**
   * Returns a name for this query
   *
   * @return the query name
   */
  String getQueryName();

  /**
   * Returns the where list
   *
   * @return the list
   */
  List<WhereInfo> getWhereList();

  /**
   * If this query refers to a parent, then returns the name in the parameters that will contain the parent
   *
   * @return the optional parent param key name
   */
  @Nullable
  String getParentParamKey();

  /**
   * If this query refers to a parent, then returns the name of the parent definition.
   *
   * @return the parent name
   */
  @Nullable
  String getParentName();

  /**
   * Returns the sort order (each pair is the name and true for ascending or false for descending)
   *
   * @return the sort order
   */
  List<Pair<String, Boolean>> getSortList();

  /**
   * Returns the limit key (or null if there is no limit)
   *
   * @return the limit key
   */
  @Nullable
  String getLimitKey();

}

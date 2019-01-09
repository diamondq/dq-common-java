package com.diamondq.common.storage.kv;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public interface Query {

  /**
   * Returns the name of the 'object type' that this Query is attached to (usually the StructureDefinition name)
   * 
   * @return the name
   */
  public String getDefinitionName();

  /**
   * Returns a name for this query
   * 
   * @return the query name
   */
  public String getQueryName();

  /**
   * Returns the where list
   *
   * @return the list
   */
  public List<WhereInfo> getWhereList();

  /**
   * If this query refers to a parent, then returns the name in the parameters that will contain the parent
   * 
   * @return the optional parent param key name
   */
  public @Nullable String getParentParamKey();

  /**
   * If this query refers to a parent, then returns the name of the parent definition.
   * 
   * @return the parent name
   */
  public @Nullable String getParentName();

  /**
   * Returns the sort order (each pair is the name and true for ascending or false for descending)
   * 
   * @return the sort order
   */
  public List<Pair<String, Boolean>> getSortList();

  /**
   * Returns the limit key (or null if there is no limit)
   * 
   * @return the limit key
   */
  public @Nullable String getLimitKey();

}

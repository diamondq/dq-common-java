package com.diamondq.common.model.interfaces;

public interface QueryBuilder {

  public QueryBuilder andWhereConstant(String pKey, WhereOperator pOperator, Object pValue);

  public QueryBuilder andWhereParam(String pKey, WhereOperator pOperator, String pParamKey);

  /**
   * Indicate that the query requires that the object have a parent that matches the provided
   *
   * @param pParentParamKey the param key that is used to look up the parent key (must be a string)
   * @param pParentPropertyDef the PropertyDefinition within the parent
   * @return the builder
   */
  public QueryBuilder andWhereParentIs(String pParentParamKey, PropertyDefinition pParentPropertyDef);

  /**
   * Adds a key to sort by (can be called multiple times)
   * 
   * @param pKey the key to store by
   * @param pIsAscending true sort ascending or false to sort descending
   * @return the builder
   */
  public QueryBuilder orderBy(String pKey, boolean pIsAscending);
}

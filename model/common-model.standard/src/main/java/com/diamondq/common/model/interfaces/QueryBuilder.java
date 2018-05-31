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
}

package com.diamondq.common.model.interfaces;

public interface QueryBuilder {

	public QueryBuilder andWhereConstant(String pKey, WhereOperator pOperator, Object pValue);

	public QueryBuilder andWhereParam(String pKey, WhereOperator pOperator, String pParamKey);

}

package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.WhereOperator;
import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public class GenericQueryBuilder implements QueryBuilder {

	public static class GenericWhereInfo {
		public final String			key;

		public final WhereOperator	operator;

		@Nullable
		public final Object			constant;

		@Nullable
		public final String			paramKey;

		public GenericWhereInfo(String pKey, WhereOperator pOperator, @Nullable Object pConstant,
			@Nullable String pParamKey) {
			super();
			key = pKey;
			operator = pOperator;
			constant = pConstant;
			paramKey = pParamKey;
		}
	}

	private final ImmutableList<GenericWhereInfo>	mWhereList;

	private final @Nullable String					mParentParamKey;

	private final @Nullable PropertyDefinition		mParentPropertyDefinition;

	public GenericQueryBuilder(@Nullable List<GenericWhereInfo> pWhereList, @Nullable String pParentParamKey,
		@Nullable PropertyDefinition pParentPropertyDefinition) {
		mWhereList = (pWhereList == null ? ImmutableList.of() : ImmutableList.copyOf(pWhereList));
		mParentParamKey = pParentParamKey;
		mParentPropertyDefinition = pParentPropertyDefinition;
	}

	/**
	 * Returns the where list
	 *
	 * @return the list
	 */
	List<GenericWhereInfo> getWhereList() {
		return mWhereList;
	}

	@Nullable
	String getParentParamKey() {
		return mParentParamKey;
	}

	@Nullable
	PropertyDefinition getParentPropertyDefinition() {
		return mParentPropertyDefinition;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.QueryBuilder#andWhereConstant(java.lang.String,
	 *      com.diamondq.common.model.interfaces.WhereOperator, java.lang.Object)
	 */
	@Override
	public GenericQueryBuilder andWhereConstant(String pKey, WhereOperator pOperator, Object pValue) {
		return new GenericQueryBuilder(
			ImmutableList.<GenericWhereInfo> builder().addAll(mWhereList)
				.add(new GenericWhereInfo(pKey, pOperator, pValue, null)).build(),
			mParentParamKey, mParentPropertyDefinition);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.QueryBuilder#andWhereParam(java.lang.String,
	 *      com.diamondq.common.model.interfaces.WhereOperator, java.lang.String)
	 */
	@Override
	public GenericQueryBuilder andWhereParam(String pKey, WhereOperator pOperator, String pParamKey) {
		return new GenericQueryBuilder(
			ImmutableList.<GenericWhereInfo> builder().addAll(mWhereList)
				.add(new GenericWhereInfo(pKey, pOperator, null, pParamKey)).build(),
			mParentParamKey, mParentPropertyDefinition);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.QueryBuilder#andWhereParentIs(java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition)
	 */
	@Override
	public QueryBuilder andWhereParentIs(String pParentParamKey, PropertyDefinition pParentPropertyDef) {
		return new GenericQueryBuilder(mWhereList, pParentParamKey, pParentPropertyDef);
	}
}
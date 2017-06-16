package com.diamondq.common.storage.kv;

import java.math.BigDecimal;

public abstract class KVColumnDefinitionBuilder {

	protected String		mName;

	protected Integer		mMaxLength;

	protected BigDecimal	mMinValue;

	protected BigDecimal	mMaxValue;

	protected KVColumnType	mType;

	public KVColumnDefinitionBuilder name(String pValue) {
		mName = pValue;
		return this;
	}

	public KVColumnDefinitionBuilder maxLength(int pMaxLength) {
		mMaxLength = pMaxLength;
		return this;
	}

	public KVColumnDefinitionBuilder type(KVColumnType pValue) {
		mType = pValue;
		return this;
	}

	public KVColumnDefinitionBuilder minValue(BigDecimal pValue) {
		mMinValue = pValue;
		return this;
	}

	public KVColumnDefinitionBuilder maxValue(BigDecimal pValue) {
		mMaxValue = pValue;
		return this;
	}

	public abstract IKVColumnDefinition build();

}

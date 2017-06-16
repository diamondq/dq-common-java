package com.diamondq.common.storage.kv.impl;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.KVColumnType;

import java.math.BigDecimal;

public class GenericKVColumnDefinition implements IKVColumnDefinition {

	private final String		mName;

	private final KVColumnType	mType;

	private final Integer		mMaxLength;

	private final BigDecimal	mMinValue;

	private final BigDecimal	mMaxValue;

	public GenericKVColumnDefinition(String pName, KVColumnType pType, Integer pMaxLength, BigDecimal pMinValue,
		BigDecimal pMaxValue) {
		super();
		mName = pName;
		mType = pType;
		mMaxLength = pMaxLength;
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getType()
	 */
	@Override
	public KVColumnType getType() {
		return mType;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getMaxLength()
	 */
	@Override
	public Integer getMaxLength() {
		return mMaxLength;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getMinValue()
	 */
	@Override
	public BigDecimal getMinValue() {
		return mMinValue;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVColumnDefinition#getMaxValue()
	 */
	@Override
	public BigDecimal getMaxValue() {
		return mMaxValue;
	}

}

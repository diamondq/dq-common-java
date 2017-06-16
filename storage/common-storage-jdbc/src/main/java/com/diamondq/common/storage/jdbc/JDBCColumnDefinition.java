package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.KVColumnType;
import com.diamondq.common.storage.kv.impl.GenericKVColumnDefinition;

import java.math.BigDecimal;

public class JDBCColumnDefinition extends GenericKVColumnDefinition {

	public JDBCColumnDefinition(String pName, KVColumnType pType, Integer pMaxLength, BigDecimal pMinValue,
		BigDecimal pMaxValue) {
		super(pName, pType, pMaxLength, pMinValue, pMaxValue);
	}

}

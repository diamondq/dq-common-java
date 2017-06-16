package com.diamondq.common.storage.kv;

import java.math.BigDecimal;

public interface IKVColumnDefinition {

	public String getName();

	public KVColumnType getType();

	public Integer getMaxLength();
	
	public BigDecimal getMinValue();
	
	public BigDecimal getMaxValue();
}

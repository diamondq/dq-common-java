package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.KVColumnDefinitionBuilder;

public class JDBCColumnDefinitionBuilder extends KVColumnDefinitionBuilder {

	/**
	 * @see com.diamondq.common.storage.kv.KVColumnDefinitionBuilder#build()
	 */
	@Override
	public IKVColumnDefinition build() {
		return new JDBCColumnDefinition(mName, mType, mMaxLength, mMinValue, mMaxValue);
	}

}

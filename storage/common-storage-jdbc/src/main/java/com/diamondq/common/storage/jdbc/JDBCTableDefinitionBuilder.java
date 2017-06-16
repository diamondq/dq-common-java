package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVTableDefinition;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;

public class JDBCTableDefinitionBuilder extends KVTableDefinitionBuilder {

	/**
	 * @see com.diamondq.common.storage.kv.KVTableDefinitionBuilder#build()
	 */
	@Override
	public IKVTableDefinition build() {
		return new JDBCTableDefinition(mTableName, mColBuilder.build());
	}

}

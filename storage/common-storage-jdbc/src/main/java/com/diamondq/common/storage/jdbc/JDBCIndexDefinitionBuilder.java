package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVIndexDefinition;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;
import com.diamondq.common.storage.kv.impl.GenericKVIndexDefinition;

public class JDBCIndexDefinitionBuilder extends KVIndexDefinitionBuilder<JDBCIndexDefinitionBuilder> {

	/**
	 * @see com.diamondq.common.storage.kv.KVIndexDefinitionBuilder#build()
	 */
	@Override
	public IKVIndexDefinition build() {
		return new GenericKVIndexDefinition(mName, mColumns);
	}
}

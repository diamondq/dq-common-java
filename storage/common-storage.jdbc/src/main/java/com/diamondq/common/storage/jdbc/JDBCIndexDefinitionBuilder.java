package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.GenericKVIndexDefinition;
import com.diamondq.common.storage.kv.IKVIndexDefinition;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;

public class JDBCIndexDefinitionBuilder extends KVIndexDefinitionBuilder<JDBCIndexDefinitionBuilder> {

	/**
	 * @see com.diamondq.common.storage.kv.KVIndexDefinitionBuilder#build()
	 */
	@Override
	public IKVIndexDefinition build() {
		String name = mName;
		if (name == null)
			throw new IllegalArgumentException("The name was not set in the JDBCIndexDefinitionBuilder");
		return new GenericKVIndexDefinition(name, mColumns.build());
	}
}

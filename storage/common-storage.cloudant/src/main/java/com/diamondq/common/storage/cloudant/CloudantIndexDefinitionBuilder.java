package com.diamondq.common.storage.cloudant;

import com.diamondq.common.storage.kv.GenericKVIndexDefinition;
import com.diamondq.common.storage.kv.IKVIndexDefinition;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;

public class CloudantIndexDefinitionBuilder extends KVIndexDefinitionBuilder<CloudantIndexDefinitionBuilder> {

	/**
	 * @see com.diamondq.common.storage.kv.KVIndexDefinitionBuilder#build()
	 */
	@Override
	public IKVIndexDefinition build() {
		validate();
		String name = mName;
		if (name == null)
			throw new IllegalStateException();
		return new GenericKVIndexDefinition(name, mColumns.build());
	}
}

package com.diamondq.common.storage.cloudant;

import com.diamondq.common.storage.kv.IKVIndexColumn;
import com.diamondq.common.storage.kv.KVColumnType;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.impl.GenericKVIndexColumn;

public class CloudantIndexColumnBuilder extends KVIndexColumnBuilder<CloudantIndexColumnBuilder> {

	@Override
	public IKVIndexColumn build() {
		validate();
		String name = mName;
		KVColumnType type = mType;
		if ((name == null) || (type == null))
			throw new IllegalStateException();
		return new GenericKVIndexColumn(name, type);
	}

}

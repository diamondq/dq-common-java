package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVIndexColumn;
import com.diamondq.common.storage.kv.KVColumnType;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.impl.GenericKVIndexColumn;

public class JDBCIndexColumnBuilder extends KVIndexColumnBuilder<JDBCIndexColumnBuilder> {

	/**
	 * @see com.diamondq.common.storage.kv.KVIndexColumnBuilder#build()
	 */
	@Override
	public IKVIndexColumn build() {
		validate();
		String name = mName;
		KVColumnType type = mType;
		if ((name == null) || (type == null))
			throw new IllegalArgumentException();
		return new GenericKVIndexColumn(name, type);
	}

}

package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVIndexColumn;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.impl.GenericKVIndexColumn;

public class JDBCIndexColumnBuilder extends KVIndexColumnBuilder<JDBCIndexColumnBuilder> {


	@Override
	public IKVIndexColumn build() {
		return new GenericKVIndexColumn(mName, mType);
	}

}

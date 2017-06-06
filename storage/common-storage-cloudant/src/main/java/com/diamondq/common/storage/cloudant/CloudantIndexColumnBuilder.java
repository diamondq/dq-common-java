package com.diamondq.common.storage.cloudant;

import com.diamondq.common.storage.kv.IKVIndexColumn;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.impl.GenericKVIndexColumn;

public class CloudantIndexColumnBuilder extends KVIndexColumnBuilder<CloudantIndexColumnBuilder> {


	@Override
	public IKVIndexColumn build() {
		return new GenericKVIndexColumn(mName, mType);
	}

}

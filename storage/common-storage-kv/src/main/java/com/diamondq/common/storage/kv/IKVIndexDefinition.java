package com.diamondq.common.storage.kv;

import java.util.List;

public interface IKVIndexDefinition {

	public String getName();

	public List<IKVIndexColumn> getColumns();
}

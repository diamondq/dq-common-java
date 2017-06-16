package com.diamondq.common.storage.kv;

import java.util.List;

public interface IKVTableDefinition {

	public String getTableName();

	public List<IKVColumnDefinition> getColumnDefinitions();

}

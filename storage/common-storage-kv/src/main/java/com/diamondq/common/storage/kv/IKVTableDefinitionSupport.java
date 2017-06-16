package com.diamondq.common.storage.kv;


public interface IKVTableDefinitionSupport {

	public KVTableDefinitionBuilder createTableDefinitionBuilder();

	public void addTableDefinition(IKVTableDefinition pDefinition);

	public KVColumnDefinitionBuilder createColumnDefinitionBuilder();
	
}

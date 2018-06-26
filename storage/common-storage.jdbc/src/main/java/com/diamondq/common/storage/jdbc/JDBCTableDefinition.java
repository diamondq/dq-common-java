package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.GenericKVTableDefinition;
import com.diamondq.common.storage.kv.IKVColumnDefinition;

import java.util.List;

public class JDBCTableDefinition extends GenericKVTableDefinition {

	public JDBCTableDefinition(String pTableName, List<IKVColumnDefinition> pColumnDefinitions) {
		super(pTableName, pColumnDefinitions);
	}

}

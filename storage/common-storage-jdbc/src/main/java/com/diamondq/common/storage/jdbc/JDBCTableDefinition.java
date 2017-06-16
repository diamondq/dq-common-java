package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.impl.GenericKVTableDefinition;

import java.util.List;

public class JDBCTableDefinition extends GenericKVTableDefinition {

	public JDBCTableDefinition(String pTableName, List<IKVColumnDefinition> pColumnDefinitions) {
		super(pTableName, pColumnDefinitions);
	}

}

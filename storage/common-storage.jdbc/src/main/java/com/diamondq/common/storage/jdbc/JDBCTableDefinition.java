package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.GenericKVTableDefinition;
import com.diamondq.common.storage.kv.IKVColumnDefinition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JDBCTableDefinition extends GenericKVTableDefinition {

  public JDBCTableDefinition(String pTableName, @Nullable String pSinglePrimaryKeyName,
    List<IKVColumnDefinition> pColumnDefinitions) {
    super(pTableName, pSinglePrimaryKeyName, pColumnDefinitions);
  }

}

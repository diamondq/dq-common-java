package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.GenericKVTableDefinition;
import com.diamondq.common.storage.kv.IKVColumnDefinition;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JDBCTableDefinition extends GenericKVTableDefinition {

  public JDBCTableDefinition(String pTableName, @Nullable String pSinglePrimaryKeyName,
    List<IKVColumnDefinition> pColumnDefinitions) {
    super(pTableName, pSinglePrimaryKeyName, pColumnDefinitions);
  }

}

package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVTableDefinition;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;

public class JDBCTableDefinitionBuilder extends KVTableDefinitionBuilder<JDBCTableDefinitionBuilder> {

  /**
   * @see com.diamondq.common.storage.kv.KVTableDefinitionBuilder#build()
   */
  @Override
  public IKVTableDefinition build() {
    String tableName = mTableName;
    if (tableName == null)
      throw new IllegalArgumentException("The table name was not set in the JDBCTableDefinitionBuilder");
    return new JDBCTableDefinition(tableName, mColBuilder.build());
  }

}

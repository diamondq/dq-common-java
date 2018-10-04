package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.KVColumnDefinitionBuilder;
import com.diamondq.common.storage.kv.KVColumnType;

public class JDBCColumnDefinitionBuilder extends KVColumnDefinitionBuilder<JDBCColumnDefinitionBuilder> {

  /**
   * @see com.diamondq.common.storage.kv.KVColumnDefinitionBuilder#build()
   */
  @Override
  public IKVColumnDefinition build() {
    String name = mName;
    if (name == null)
      throw new IllegalArgumentException("The name was not set in the JDBCColumnDefinitionBuilder");
    KVColumnType type = mType;
    if (type == null)
      throw new IllegalArgumentException("The type was not set in the JDBCColumnDefinitionBuilder");
    return new JDBCColumnDefinition(name, type, mIsPrimaryKey, mMaxLength, mMinValue, mMaxValue);
  }

}

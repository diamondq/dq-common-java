package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.GenericKVColumnDefinition;
import com.diamondq.common.storage.kv.KVColumnType;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

public class JDBCColumnDefinition extends GenericKVColumnDefinition {

  public JDBCColumnDefinition(String pName, KVColumnType pType, boolean pIsPrimaryKey, @Nullable Integer pMaxLength,
    @Nullable BigDecimal pMinValue, @Nullable BigDecimal pMaxValue, @Nullable BigDecimal pAutoIncrementStart,
    @Nullable BigDecimal pAutoIncrementBy) {
    super(pName, pType, pIsPrimaryKey, pMaxLength, pMinValue, pMaxValue, pAutoIncrementStart, pAutoIncrementBy);
  }

}

package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.IKVTableDefinition;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class JDBCColumnDeserializer implements IResultSetDeserializer {

  private final IKVTableDefinition mDefinition;

  private final IJDBCDialect       mDialect;

  public JDBCColumnDeserializer(IJDBCDialect pDialect, IKVTableDefinition pDefinition) {
    mDialect = pDialect;
    mDefinition = pDefinition;
  }

  @Override
  public <O> O deserializeFromResultSet(ResultSet pRs, Class<O> pClass) throws SQLException {
    if (Map.class.isAssignableFrom(pClass) == false)
      throw new UnsupportedOperationException();
    Map<String, Object> result = Maps.newHashMap();
    int index = 1;
    for (IKVColumnDefinition cd : mDefinition.getColumnDefinitions()) {
      switch (cd.getType()) {
      case Boolean: {
        Boolean value = mDialect.readBoolean(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      case Decimal: {
        BigDecimal minValue = cd.getMinValue();
        BigDecimal maxValue = cd.getMaxValue();
        if ((minValue != null) && (minValue.equals(JDBCKVStore.sLONG_MIN_VALUE)) && (maxValue != null)
          && (maxValue.equals(JDBCKVStore.sLONG_MAX_VALUE))) {
          Long value = mDialect.readLong(pRs, index);
          if (value != null)
            result.put(cd.getName(), BigDecimal.valueOf(value));
        }
        else {
          BigDecimal value = mDialect.readDecimal(pRs, index);
          if (value != null)
            result.put(cd.getName(), value);
        }
        break;
      }
      case Integer: {
        Integer value = mDialect.readInteger(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      case Long: {
        Long value = mDialect.readLong(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      case String: {
        Integer maxLength = cd.getMaxLength();
        String value;
        if (maxLength != null)
          value = mDialect.readText(pRs, index);
        else
          value = mDialect.readUnlimitedTextType(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      case Timestamp: {
        Long value = mDialect.readTimestamp(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      case UUID: {
        UUID value = mDialect.readUUID(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      case Binary: {
        byte[] value = mDialect.readBinary(pRs, index);
        if (value != null)
          result.put(cd.getName(), value);
        break;
      }
      }
      index++;
    }

    @SuppressWarnings("unchecked")
    O finalResult = (O) result;
    return finalResult;
  }

}

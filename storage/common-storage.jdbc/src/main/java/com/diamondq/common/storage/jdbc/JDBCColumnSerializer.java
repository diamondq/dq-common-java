package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.IKVTableDefinition;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JDBCColumnSerializer implements IPreparedStatementSerializer {

  private final IKVTableDefinition mDefinition;

  private final IJDBCDialect       mDialect;

  public JDBCColumnSerializer(IJDBCDialect pDialect, IKVTableDefinition pDefinition) {
    mDialect = pDialect;
    mDefinition = pDefinition;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IPreparedStatementSerializer#serializeColumnToPreparedStatement(java.lang.Object,
   *      com.diamondq.common.storage.kv.IKVColumnDefinition, java.sql.PreparedStatement, int)
   */
  @Override
  public void serializeColumnToPreparedStatement(@Nullable Object obj, IKVColumnDefinition pColDef,
    PreparedStatement pPs, int pParamCount) {
    try {
      switch (pColDef.getType()) {
      case Boolean: {
        Boolean value;
        if (obj == null)
          value = null;
        else if (obj instanceof String)
          value = Boolean.valueOf((String) obj);
        else if (obj instanceof Boolean)
          value = (Boolean) obj;
        else
          throw new IllegalArgumentException("Only Boolean or String supported, but found " + obj.getClass());
        mDialect.writeBoolean(pPs, pParamCount, value);
        break;
      }
      case Decimal: {
        BigDecimal minValue = pColDef.getMinValue();
        BigDecimal maxValue = pColDef.getMaxValue();
        if ((minValue != null) && (minValue.equals(JDBCKVStore.sLONG_MIN_VALUE)) && (maxValue != null)
          && (maxValue.equals(JDBCKVStore.sLONG_MAX_VALUE))) {
          Long value;
          if (obj == null)
            value = null;
          else if (obj instanceof String)
            value = Long.valueOf((String) obj);
          else if (obj instanceof Long)
            value = (Long) obj;
          else if (obj instanceof BigDecimal)
            value = ((BigDecimal) obj).longValue();
          else
            throw new IllegalArgumentException(
              "Only Long, BigDecimal or String supported, but found " + obj.getClass());
          mDialect.writeLong(pPs, pParamCount, value);
        }
        else {
          BigDecimal value;
          if (obj == null)
            value = null;
          else if (obj instanceof String)
            value = new BigDecimal((String) obj);
          else if (obj instanceof BigDecimal)
            value = (BigDecimal) obj;
          else
            throw new IllegalArgumentException("Only BigDecimal or String supported, but found " + obj.getClass());
          mDialect.writeDecimal(pPs, pParamCount, value);
        }
        break;
      }
      case Integer: {
        Integer value;
        if (obj == null)
          value = null;
        else if (obj instanceof String)
          value = Integer.valueOf((String) obj);
        else if (obj instanceof Integer)
          value = (Integer) obj;
        else if (obj instanceof BigDecimal)
          value = ((BigDecimal) obj).intValue();
        else
          throw new IllegalArgumentException(
            "Only Integer, BigDecimal or String supported, but found " + obj.getClass());
        mDialect.writeInteger(pPs, pParamCount, value);
        break;
      }
      case Long: {
        Long value;
        if (obj == null)
          value = null;
        else if (obj instanceof String)
          value = Long.valueOf((String) obj);
        else if (obj instanceof Integer)
          value = ((Integer) obj).longValue();
        else if (obj instanceof Long)
          value = (Long) obj;
        else if (obj instanceof BigDecimal)
          value = ((BigDecimal) obj).longValue();
        else
          throw new IllegalArgumentException(
            "Only Integer, Long, BigDecimal or String supported, but found " + obj.getClass());
        mDialect.writeLong(pPs, pParamCount, value);
        break;
      }
      case String: {
        String value;
        if (obj == null)
          value = null;
        else if (obj instanceof String)
          value = (String) obj;
        else if (obj instanceof UUID)
          value = ((UUID) obj).toString();
        else
          throw new IllegalArgumentException("Only String and UUID are supported, but found " + obj.getClass());
        Integer maxLength = pColDef.getMaxLength();
        if (maxLength != null)
          mDialect.writeText(pPs, pParamCount, value);
        else
          mDialect.writeUnlimitedText(pPs, pParamCount, value);
        break;
      }
      case Timestamp: {
        Long value;
        if (obj == null)
          value = null;
        else if (obj instanceof String)
          value = Long.valueOf((String) obj);
        else if (obj instanceof Long)
          value = (Long) obj;
        else if (obj instanceof BigDecimal)
          value = ((BigDecimal) obj).longValue();
        else
          throw new IllegalArgumentException("Only Long, BigDecimal or String supported, but found " + obj.getClass());
        mDialect.writeTimestamp(pPs, pParamCount, value);
        break;
      }
      case UUID: {
        UUID value;
        if (obj == null)
          value = null;
        else if (obj instanceof UUID)
          value = (UUID) obj;
        else
          throw new IllegalArgumentException("Only UUID supported, but found " + obj.getClass());
        mDialect.writeUUID(pPs, pParamCount, value);
        break;
      }
      case Binary: {
        byte[] value;
        if (obj == null)
          value = null;
        else if (obj instanceof byte[])
          value = (byte[]) obj;
        else
          throw new IllegalArgumentException("Only byte[] supported, but found " + obj.getClass());
        mDialect.writeBinary(pPs, pParamCount, value);
        break;
      }
      }
    }
    catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public <@Nullable O> int serializeToPreparedStatement(O pObj, PreparedStatement pPs, int pStartAtIndex)
    throws SQLException {
    if (pObj == null)
      throw new UnsupportedOperationException();
    if (Map.class.isAssignableFrom(pObj.getClass()) == false)
      throw new UnsupportedOperationException();
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) pObj;
    int index = pStartAtIndex;
    for (IKVColumnDefinition cd : mDefinition.getColumnDefinitions()) {
      Object obj = data.get(cd.getName());
      serializeColumnToPreparedStatement(obj, cd, pPs, index);
      index++;
    }
    return index;
  }

}

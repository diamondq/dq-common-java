package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JDBCJsonSerializer implements IPreparedStatementSerializer {

  private final ObjectMapper mMapper;

  public JDBCJsonSerializer() {
    mMapper = new ObjectMapper();
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IPreparedStatementSerializer#serializeToPreparedStatement(java.lang.Object,
   *      java.sql.PreparedStatement, int)
   */
  @Override
  public <@Nullable O> int serializeToPreparedStatement(O pObj, PreparedStatement pPs, int pStartAtIndex)
    throws SQLException {
    try {
      String str = mMapper.writeValueAsString(pObj);
      pPs.setString(pStartAtIndex, str);
      return pStartAtIndex + 1;
    }
    catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IPreparedStatementSerializer#serializeColumnToPreparedStatement(java.lang.Object,
   *      com.diamondq.common.storage.kv.IKVColumnDefinition, java.sql.PreparedStatement, int)
   */
  @Override
  public void serializeColumnToPreparedStatement(@Nullable Object pValue, IKVColumnDefinition pColDef,
    PreparedStatement pPs, int pParamCount) {
    throw new UnsupportedOperationException();
  }
}

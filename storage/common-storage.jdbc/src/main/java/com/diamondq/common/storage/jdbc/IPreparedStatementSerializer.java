package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface IPreparedStatementSerializer {

  /**
   * Serializes the object into the PreparedStatement
   * 
   * @param pObj the object
   * @param pPs the PreparedStatement
   * @param pStartAtIndex the index to start at (this offset represents the first element to write to)
   * @return the next offset after the last one written by the serializer
   * @throws SQLException an exception
   */
  public <@Nullable O> int serializeToPreparedStatement(O pObj, PreparedStatement pPs, int pStartAtIndex)
    throws SQLException;

  /**
   * Serializes a single column definition to the prepared statement
   * 
   * @param pValue the value
   * @param pColDef the column definition
   * @param pPs the prepared statement
   * @param pParamCount the parameter count
   */
  public void serializeColumnToPreparedStatement(@Nullable Object pValue, IKVColumnDefinition pColDef,
    PreparedStatement pPs, int pParamCount);

}

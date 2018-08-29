package com.diamondq.common.storage.jdbc;

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

}

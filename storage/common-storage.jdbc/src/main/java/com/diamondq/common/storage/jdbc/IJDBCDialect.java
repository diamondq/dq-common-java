package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.KVColumnType;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Interface defining a JDBC Dialect (aka Postgres, MySQL, Oracle, etc.)
 */
public interface IJDBCDialect {

  /**
   * Returns the DDL necessary to create a new schema
   * 
   * @param pSchemaName the schema name
   * @return the DDL SQL
   */
  public String generateCreateSchemaSQL(String pSchemaName);

  /**
   * Returns the data type, used for CREATE TABLE actions to create a column on unlimited text size (such as a CLOB).
   * 
   * @return the type
   */
  public String getUnlimitedTextType();

  public @Nullable String readUnlimitedTextType(ResultSet pRs, int pIndex) throws SQLException;

  public void writeUnlimitedText(PreparedStatement pPs, int pIndex, @Nullable String pValue) throws SQLException;

  public String getBooleanType();

  public @Nullable Boolean readBoolean(ResultSet pResultSet, int pIndex) throws SQLException;

  public void writeBoolean(PreparedStatement pPs, int pIndex, @Nullable Boolean pValue) throws SQLException;

  public String getLongType();

  public @Nullable Long readLong(ResultSet pRs, int pIndex) throws SQLException;

  public void writeLong(PreparedStatement pPs, int pIndex, @Nullable Long pValue) throws SQLException;

  public String getUnlimitedDecimalType();

  public @Nullable BigDecimal readDecimal(ResultSet pRs, int pIndex) throws SQLException;

  public void writeDecimal(PreparedStatement pPs, int pIndex, @Nullable BigDecimal pValue) throws SQLException;

  public String getIntegerType();

  public @Nullable Integer readInteger(ResultSet pRs, int pIndex) throws SQLException;

  public void writeInteger(PreparedStatement pPs, int pIndex, @Nullable Integer pValue) throws SQLException;

  public String getTextType(int pMaxLength);

  public @Nullable String readText(ResultSet pRs, int pIndex) throws SQLException;

  public void writeText(PreparedStatement pPs, int pIndex, @Nullable String pValue) throws SQLException;

  public String getTimestampType();

  public @Nullable Long readTimestamp(ResultSet pRs, int pIndex) throws SQLException;

  public void writeTimestamp(PreparedStatement pPs, int pIndex, @Nullable Long pValue) throws SQLException;

  public String getUUIDType();

  public @Nullable UUID readUUID(ResultSet pRs, int pIndex) throws SQLException;

  public void writeUUID(PreparedStatement pPs, int pIndex, @Nullable UUID pValue) throws SQLException;

  public String getBinaryType(int pMaxLength);

  public byte @Nullable [] readBinary(ResultSet pRs, int pIndex) throws SQLException;

  public void writeBinary(PreparedStatement pPs, int pIndex, byte @Nullable [] pValue) throws SQLException;

  public Set<@NonNull String> getReservedWords();

  public String getAutoIncrement(KVColumnType pType, BigDecimal pAutoIncrementStart,
    @Nullable BigDecimal pAutoIncrementBy);
  
  public String getLimitKeyword();
}

package com.diamondq.common.storage.jdbc.dialects;

import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class H2 extends AbstractDialect {

  private static ThreadLocal<@NonNull Calendar> sCALENDAR =
    ThreadLocal.withInitial(() -> Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH));

  protected static Set<@NonNull String>         sRESERVED_WORDS;

  static {
    String wordsStr =
      "abort,acl,add,aggregate,append,archive,arch_store,backward,binary,boolean,change,cluster,copy,database,delimiter,delimiters,do,extend,explain,forward,heavy,index,inherits,isnull,light,listen,load,merge,nothing,notify,notnull,offset,oids,purge,rename,replace,retrieve,returns,rule,recipe,setof,stdin,stdout,store,vacuum,verbose,version";
    @NonNull
    String[] words = wordsStr.split(",");
    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
    for (String w : words)
      builder.add(w);
    builder.addAll(sSQL_2003_RESERVED_WORDS);
    sRESERVED_WORDS = builder.build();
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#generateCreateSchemaSQL(java.lang.String)
   */
  @Override
  public String generateCreateSchemaSQL(String pSchemaName) {
    return "CREATE SCHEMA " + pSchemaName;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getUnlimitedTextType()
   */
  @Override
  public String getUnlimitedTextType() {
    return "clob";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readUnlimitedTextType(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable String readUnlimitedTextType(ResultSet pRs, int pIndex) throws SQLException {
    String value = pRs.getString(pIndex);
    if (pRs.wasNull() == true)
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeUnlimitedText(java.sql.PreparedStatement, int,
   *      java.lang.String)
   */
  @Override
  public void writeUnlimitedText(PreparedStatement pPs, int pIndex, @Nullable String pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.VARCHAR);
    else
      pPs.setString(pIndex, pValue);
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getBooleanType()
   */
  @Override
  public String getBooleanType() {
    return "boolean";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readBoolean(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable Boolean readBoolean(ResultSet pResultSet, int pIndex) throws SQLException {
    boolean result = pResultSet.getBoolean(pIndex);
    if (pResultSet.wasNull() == true)
      return null;
    return result;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeBoolean(java.sql.PreparedStatement, int, java.lang.Boolean)
   */
  @Override
  public void writeBoolean(PreparedStatement pPs, int pIndex, @Nullable Boolean pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.BOOLEAN);
    else
      pPs.setBoolean(pIndex, pValue);
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getLongType()
   */
  @Override
  public String getLongType() {
    return "bigint";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readLong(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable Long readLong(ResultSet pRs, int pIndex) throws SQLException {
    long value = pRs.getLong(pIndex);
    if (pRs.wasNull() == true)
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeLong(java.sql.PreparedStatement, int, java.lang.Long)
   */
  @Override
  public void writeLong(PreparedStatement pPs, int pIndex, @Nullable Long pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.BIGINT);
    else
      pPs.setLong(pIndex, pValue);
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getUnlimitedDecimalType()
   */
  @Override
  public String getUnlimitedDecimalType() {
    return "numeric";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readDecimal(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable BigDecimal readDecimal(ResultSet pRs, int pIndex) throws SQLException {
    BigDecimal value = pRs.getBigDecimal(pIndex);
    if (pRs.wasNull() == true)
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeDecimal(java.sql.PreparedStatement, int,
   *      java.math.BigDecimal)
   */
  @Override
  public void writeDecimal(PreparedStatement pPs, int pIndex, @Nullable BigDecimal pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.DECIMAL);
    else
      pPs.setBigDecimal(pIndex, pValue);
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getIntegerType()
   */
  @Override
  public String getIntegerType() {
    return "int";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readInteger(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable Integer readInteger(ResultSet pRs, int pIndex) throws SQLException {
    int value = pRs.getInt(pIndex);
    if (pRs.wasNull() == true)
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeInteger(java.sql.PreparedStatement, int, java.lang.Integer)
   */
  @Override
  public void writeInteger(PreparedStatement pPs, int pIndex, @Nullable Integer pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.INTEGER);
    else
      pPs.setInt(pIndex, pValue);
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getTextType(int)
   */
  @Override
  public String getTextType(int pMaxLength) {
    return "varchar(" + pMaxLength + ")";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readText(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable String readText(ResultSet pRs, int pIndex) throws SQLException {
    String value = pRs.getString(pIndex);
    if (pRs.wasNull() == true)
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeText(java.sql.PreparedStatement, int, java.lang.String)
   */
  @Override
  public void writeText(PreparedStatement pPs, int pIndex, @Nullable String pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.VARCHAR);
    else
      pPs.setString(pIndex, pValue);
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getTimestampType()
   */
  @Override
  public String getTimestampType() {
    return "timestamp(3) with time zone";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readTimestamp(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable Long readTimestamp(ResultSet pRs, int pIndex) throws SQLException {
    Calendar c = sCALENDAR.get();
    Timestamp value = pRs.getTimestamp(pIndex, c);
    if ((pRs.wasNull() == true) || (value == null))
      return null;
    return value.getTime();
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeTimestamp(java.sql.PreparedStatement, int, java.lang.Long)
   */
  @Override
  public void writeTimestamp(PreparedStatement pPs, int pIndex, @Nullable Long pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.TIMESTAMP_WITH_TIMEZONE);
    else {
      Calendar c = sCALENDAR.get();
      pPs.setTimestamp(pIndex, new Timestamp(pValue), c);
    }
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getUUIDType()
   */
  @Override
  public String getUUIDType() {
    return "uuid";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readUUID(java.sql.ResultSet, int)
   */
  @Override
  public @Nullable UUID readUUID(ResultSet pRs, int pIndex) throws SQLException {
    UUID value = pRs.getObject(pIndex, UUID.class);
    if ((pRs.wasNull() == true) || (value == null))
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeUUID(java.sql.PreparedStatement, int, java.util.UUID)
   */
  @Override
  public void writeUUID(PreparedStatement pPs, int pIndex, @Nullable UUID pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.JAVA_OBJECT);
    else {
      pPs.setObject(pIndex, pValue);
    }
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getBinaryType(int)
   */
  @Override
  public String getBinaryType(int pMaxLength) {
    return "binary(" + String.valueOf(pMaxLength) + ")";
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#readBinary(java.sql.ResultSet, int)
   */
  @Override
  public byte @Nullable [] readBinary(ResultSet pRs, int pIndex) throws SQLException {
    byte[] value = pRs.getBytes(pIndex);
    if (pRs.wasNull() == true)
      return null;
    return value;
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#writeBinary(java.sql.PreparedStatement, int, byte[])
   */
  @Override
  public void writeBinary(PreparedStatement pPs, int pIndex, byte @Nullable [] pValue) throws SQLException {
    if (pValue == null)
      pPs.setNull(pIndex, Types.BINARY);
    else {
      pPs.setBytes(pIndex, pValue);
    }
  }

  /**
   * @see com.diamondq.common.storage.jdbc.IJDBCDialect#getReservedWords()
   */
  @Override
  public Set<@NonNull String> getReservedWords() {
    return sRESERVED_WORDS;
  }

}

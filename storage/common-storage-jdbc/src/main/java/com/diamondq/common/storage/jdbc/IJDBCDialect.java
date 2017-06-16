package com.diamondq.common.storage.jdbc;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

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

	public String readUnlimitedTextType(ResultSet pRs, int pIndex) throws SQLException;

	public void writeUnlimitedText(PreparedStatement pPs, int pIndex, String pValue) throws SQLException;

	public String getBooleanType();

	public Boolean readBoolean(ResultSet pResultSet, int pIndex) throws SQLException;

	public void writeBoolean(PreparedStatement pPs, int pIndex, Boolean pValue) throws SQLException;

	public String getLongType();

	public Long readLong(ResultSet pRs, int pIndex) throws SQLException;

	public void writeLong(PreparedStatement pPs, int pIndex, Long pValue) throws SQLException;

	public String getUnlimitedDecimalType();

	public BigDecimal readDecimal(ResultSet pRs, int pIndex) throws SQLException;

	public void writeDecimal(PreparedStatement pPs, int pIndex, BigDecimal pValue) throws SQLException;

	public String getIntegerType();

	public Integer readInteger(ResultSet pRs, int pIndex) throws SQLException;

	public void writeInteger(PreparedStatement pPs, int pIndex, Integer pValue) throws SQLException;

	public String getTextType(int pMaxLength);

	public String readText(ResultSet pRs, int pIndex) throws SQLException;

	public void writeText(PreparedStatement pPs, int pIndex, String pValue) throws SQLException;

	public String getTimestampType();

	public Long readTimestamp(ResultSet pRs, int pIndex) throws SQLException;

	public void writeTimestamp(PreparedStatement pPs, int pIndex, Long pValue) throws SQLException;

	public Set<String> getReservedWords();
}

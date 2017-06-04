package com.diamondq.common.storage.common.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class DelegatingConnection implements Connection {

	private final Connection							mDelegate;

	private int											mReferenceCount	= 1;

	private final BiConsumer<Connection, Connection>	mCallback;

	public DelegatingConnection(Connection pDelegate, BiConsumer<Connection, Connection> pCallback) {
		super();
		mDelegate = pDelegate;
		mCallback = pCallback;
	}

	@Override
	public <T> T unwrap(Class<T> pIface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> pIface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Statement createStatement() throws SQLException {
		return mDelegate.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String pSql) throws SQLException {
		return mDelegate.prepareStatement(pSql);
	}

	@Override
	public CallableStatement prepareCall(String pSql) throws SQLException {
		return mDelegate.prepareCall(pSql);
	}

	@Override
	public String nativeSQL(String pSql) throws SQLException {
		return mDelegate.nativeSQL(pSql);
	}

	@Override
	public void setAutoCommit(boolean pAutoCommit) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return mDelegate.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		if (mReferenceCount > 1)
			return;
		mDelegate.commit();
	}

	@Override
	public void rollback() throws SQLException {
		if (mReferenceCount > 1)
			return;
		mDelegate.rollback();
	}

	@Override
	public void close() throws SQLException {
		mReferenceCount--;
		if (mReferenceCount > 0)
			return;
		mDelegate.close();
		mCallback.accept(this, mDelegate);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return mDelegate.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return mDelegate.getMetaData();
	}

	@Override
	public void setReadOnly(boolean pReadOnly) throws SQLException {
		mDelegate.setReadOnly(pReadOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return mDelegate.isReadOnly();
	}

	@Override
	public void setCatalog(String pCatalog) throws SQLException {
		mDelegate.setCatalog(pCatalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return mDelegate.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int pLevel) throws SQLException {
		mDelegate.setTransactionIsolation(pLevel);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return mDelegate.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return mDelegate.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		mDelegate.clearWarnings();
	}

	@Override
	public Statement createStatement(int pResultSetType, int pResultSetConcurrency) throws SQLException {
		return mDelegate.createStatement(pResultSetType, pResultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String pSql, int pResultSetType, int pResultSetConcurrency)
		throws SQLException {
		return mDelegate.prepareStatement(pSql, pResultSetType, pResultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String pSql, int pResultSetType, int pResultSetConcurrency)
		throws SQLException {
		return mDelegate.prepareCall(pSql, pResultSetType, pResultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return mDelegate.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> pMap) throws SQLException {
		mDelegate.setTypeMap(pMap);
	}

	@Override
	public void setHoldability(int pHoldability) throws SQLException {
		mDelegate.setHoldability(pHoldability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return mDelegate.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return mDelegate.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String pName) throws SQLException {
		return mDelegate.setSavepoint(pName);
	}

	@Override
	public void rollback(Savepoint pSavepoint) throws SQLException {
		mDelegate.rollback(pSavepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint pSavepoint) throws SQLException {
		mDelegate.releaseSavepoint(pSavepoint);
	}

	@Override
	public Statement createStatement(int pResultSetType, int pResultSetConcurrency, int pResultSetHoldability)
		throws SQLException {
		return mDelegate.createStatement(pResultSetType, pResultSetConcurrency, pResultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String pSql, int pResultSetType, int pResultSetConcurrency,
		int pResultSetHoldability) throws SQLException {
		return mDelegate.prepareStatement(pSql, pResultSetType, pResultSetConcurrency, pResultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String pSql, int pResultSetType, int pResultSetConcurrency,
		int pResultSetHoldability) throws SQLException {
		return mDelegate.prepareCall(pSql, pResultSetType, pResultSetConcurrency, pResultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String pSql, int pAutoGeneratedKeys) throws SQLException {
		return mDelegate.prepareStatement(pSql, pAutoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String pSql, int[] pColumnIndexes) throws SQLException {
		return mDelegate.prepareStatement(pSql, pColumnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String pSql, String[] pColumnNames) throws SQLException {
		return mDelegate.prepareStatement(pSql, pColumnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return mDelegate.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return mDelegate.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return mDelegate.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return mDelegate.createSQLXML();
	}

	@Override
	public boolean isValid(int pTimeout) throws SQLException {
		return mDelegate.isValid(pTimeout);
	}

	@Override
	public void setClientInfo(String pName, String pValue) throws SQLClientInfoException {
		mDelegate.setClientInfo(pName, pValue);
	}

	@Override
	public void setClientInfo(Properties pProperties) throws SQLClientInfoException {
		mDelegate.setClientInfo(pProperties);
	}

	@Override
	public String getClientInfo(String pName) throws SQLException {
		return mDelegate.getClientInfo(pName);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return mDelegate.getClientInfo();
	}

	@Override
	public Array createArrayOf(String pTypeName, Object[] pElements) throws SQLException {
		return mDelegate.createArrayOf(pTypeName, pElements);
	}

	@Override
	public Struct createStruct(String pTypeName, Object[] pAttributes) throws SQLException {
		return mDelegate.createStruct(pTypeName, pAttributes);
	}

	@Override
	public void setSchema(String pSchema) throws SQLException {
		mDelegate.setSchema(pSchema);
	}

	@Override
	public String getSchema() throws SQLException {
		return mDelegate.getSchema();
	}

	@Override
	public void abort(Executor pExecutor) throws SQLException {
		mDelegate.abort(pExecutor);
	}

	@Override
	public void setNetworkTimeout(Executor pExecutor, int pMilliseconds) throws SQLException {
		mDelegate.setNetworkTimeout(pExecutor, pMilliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return mDelegate.getNetworkTimeout();
	}

	public void increaseRefCount() {
		mReferenceCount++;
	}
}

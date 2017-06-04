package com.diamondq.common.storage.common.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class TransactionAwareDataSource implements DataSource {

	private final DataSource										mDelegate;

	private final ThreadLocal<DelegatingConnection>					mThreadLocal		= new ThreadLocal<>();

	private final ThreadLocal<Map<String, DelegatingConnection>>	mThreadUserPWLocal	= new ThreadLocal<>();

	public TransactionAwareDataSource(DataSource pDataSource) {
		mDelegate = pDataSource;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return mDelegate.getLogWriter();
	}

	@Override
	public <T> T unwrap(Class<T> pIface) throws SQLException {
		return mDelegate.unwrap(pIface);
	}

	@Override
	public void setLogWriter(PrintWriter pOut) throws SQLException {
		mDelegate.setLogWriter(pOut);
	}

	@Override
	public boolean isWrapperFor(Class<?> pIface) throws SQLException {
		return mDelegate.isWrapperFor(pIface);
	}

	@Override
	public void setLoginTimeout(int pSeconds) throws SQLException {
		mDelegate.setLoginTimeout(pSeconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return mDelegate.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return mDelegate.getParentLogger();
	}

	@Override
	public Connection getConnection() throws SQLException {
		DelegatingConnection connection = mThreadLocal.get();
		if (connection == null) {
			Connection childConnection = mDelegate.getConnection();
			childConnection.setAutoCommit(false);
			connection = new DelegatingConnection(childConnection, (w, d) -> closeConnection(w, d));
			mThreadLocal.set(connection);
		}
		else
			connection.increaseRefCount();
		return connection;
	}

	@Override
	public Connection getConnection(String pUsername, String pPassword) throws SQLException {
		Map<String, DelegatingConnection> map = mThreadUserPWLocal.get();
		if (map == null) {
			map = new HashMap<>();
			mThreadUserPWLocal.set(map);
		}
		DelegatingConnection connection = map.get(pUsername);
		if (connection == null) {
			Connection childConnection = mDelegate.getConnection(pUsername, pPassword);
			childConnection.setAutoCommit(false);
			connection = new DelegatingConnection(childConnection, (w, d) -> closeUserPWConnection(w, d, pUsername));
			map.put(pUsername, connection);
		}
		else
			connection.increaseRefCount();
		return connection;
	}

	private void closeConnection(Connection pWrapper, Connection pDelegate) {
		mThreadLocal.remove();
	}

	private void closeUserPWConnection(Connection pWrapper, Connection pDelegate, String pUserName) {
		Map<String, DelegatingConnection> map = mThreadUserPWLocal.get();
		if (map != null) {
			map.remove(pUserName);
			if (map.isEmpty() == true)
				mThreadUserPWLocal.remove();
		}
	}

}

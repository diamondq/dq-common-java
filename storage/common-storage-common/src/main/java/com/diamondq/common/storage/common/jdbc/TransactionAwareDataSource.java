package com.diamondq.common.storage.common.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A DataSource where the actual connections are wrapped up in ThreadLocal storage.
 */
public class TransactionAwareDataSource implements DataSource {

	private final DataSource																	mDelegate;

	private final ThreadLocal<@Nullable DelegatingConnection>									mThreadLocal		=
		new ThreadLocal<>();

	private final ThreadLocal<@Nullable Map<@NonNull String, @Nullable DelegatingConnection>>	mThreadUserPWLocal	=
		new ThreadLocal<>();

	/**
	 * Constructor
	 * 
	 * @param pDataSource the DataSource to actually use to retrieve Connections
	 */
	public TransactionAwareDataSource(DataSource pDataSource) {
		mDelegate = pDataSource;
	}

	@Override
	public @Nullable PrintWriter getLogWriter() throws SQLException {
		return mDelegate.getLogWriter();
	}

	@Override
	public <T> T unwrap(Class<T> pIface) throws SQLException {
		return mDelegate.unwrap(pIface);
	}

	@Override
	public void setLogWriter(@Nullable PrintWriter pOut) throws SQLException {
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
	public @Nullable Logger getParentLogger() throws SQLFeatureNotSupportedException {
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
	public Connection getConnection(@Nullable String pUsername, @Nullable String pPassword) throws SQLException {
		if (pUsername == null)
			throw new IllegalArgumentException();
		Map<@NonNull String, @Nullable DelegatingConnection> map = mThreadUserPWLocal.get();
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

	private void closeUserPWConnection(Connection pWrapper, Connection pDelegate, @Nullable String pUserName) {
		Map<@NonNull String, @Nullable DelegatingConnection> map = mThreadUserPWLocal.get();
		if (map != null) {
			map.remove(pUserName);
			if (map.isEmpty() == true)
				mThreadUserPWLocal.remove();
		}
	}

}

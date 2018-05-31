package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVTransaction;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.DataSource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The Cloudant database is effectively flat. Thus, mapping the concept of a 'table' is done by concatenating the
 * 'table' to the front of any keys.
 */
public class JDBCKVTransaction implements IKVTransaction {

	private final JDBCKVStore	mStore;

	private final DataSource	mDataSource;

	@Nullable
	private Connection			mConnection;

	public JDBCKVTransaction(JDBCKVStore pStore, DataSource pDataSource) {
		mStore = pStore;
		mDataSource = pDataSource;
	}

	private void validateConnection() throws SQLException {
		if (mConnection == null) {
			mConnection = mDataSource.getConnection();
			mConnection.setAutoCommit(false);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#getByKey(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	public <@Nullable O> O getByKey(String pTable, String pKey1, @Nullable String pKey2, Class<O> pClass) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, pClass);
			try (PreparedStatement ps = c.prepareStatement(info.getBySQL)) {
				ps.setString(1, pKey1);
				ps.setString(2, pKey2);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next() == false)
						return null;
					return info.deserializer.deserializeFromResultSet(rs, pClass);
				}
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#putByKey(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public <@Nullable O> void putByKey(String pTable, String pKey1, @Nullable String pKey2, O pObj) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, (pObj == null ? null : pObj.getClass()));
			if (info.supportsUpsert == true) {
				try (PreparedStatement ps = c.prepareStatement(info.putBySQL)) {
					ps.setString(1, pKey1);
					ps.setString(2, pKey2);
					info.serializer.serializeToPreparedStatement(pObj, ps, 2);
					ps.execute();
				}
			}
			else {
				boolean exists;
				try (PreparedStatement qps = c.prepareStatement(info.putQueryBySQL)) {
					qps.setString(1, pKey1);
					qps.setString(2, pKey2);
					try (ResultSet rs = qps.executeQuery()) {
						exists = rs.next();
					}
				}
				if (exists == false) {
					try (PreparedStatement ps = c.prepareStatement(info.putInsertBySQL)) {
						ps.setString(1, pKey1);
						ps.setString(2, pKey2);
						info.serializer.serializeToPreparedStatement(pObj, ps, 3);
						ps.execute();
					}
				}
				else {
					try (PreparedStatement ps = c.prepareStatement(info.putUpdateBySQL)) {
						int offset = info.serializer.serializeToPreparedStatement(pObj, ps, 1);
						ps.setString(offset, pKey1);
						ps.setString(offset + 1, pKey2);
						ps.execute();
					}
				}
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#removeByKey(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean removeByKey(String pTable, String pKey1, @Nullable String pKey2) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, null);
			try (PreparedStatement ps = c.prepareStatement(info.removeBySQL)) {
				ps.setString(1, pKey1);
				ps.setString(2, pKey2);
				if (ps.executeUpdate() > 0)
					return true;
				return false;
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator(java.lang.String)
	 */
	@Override
	public Iterator<@NonNull String> keyIterator(String pTable) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, null);
			PreparedStatement ps = c.prepareStatement(info.keyIteratorSQL);
			try {
				ResultSet rs = ps.executeQuery();
				try {
					JDBCResultSetIterator result = new JDBCResultSetIterator(ps, rs);
					rs = null;
					ps = null;
					@SuppressWarnings("null")
					Iterator<@NonNull String> filtered =
						(Iterator<@NonNull String>) Iterators.filter(result, Predicates.notNull());
					return filtered;
				}
				finally {
					if (rs != null)
						rs.close();
				}
			}
			finally {
				if (ps != null)
					ps.close();
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator2(java.lang.String, java.lang.String)
	 */
	@Override
	public Iterator<@NonNull String> keyIterator2(String pTable, String pKey1) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, null);
			PreparedStatement ps = c.prepareStatement(info.keyIterator2SQL);
			try {
				ps.setString(1, pKey1);
				ResultSet rs = ps.executeQuery();
				try {
					JDBCResultSetIterator result = new JDBCResultSetIterator(ps, rs);
					rs = null;
					ps = null;
					@SuppressWarnings("null")
					Iterator<@NonNull String> filtered =
						(Iterator<@NonNull String>) Iterators.filter(result, Predicates.notNull());
					return filtered;
				}
				finally {
					if (rs != null)
						rs.close();
				}
			}
			finally {
				if (ps != null)
					ps.close();
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#clear(java.lang.String)
	 */
	@Override
	public void clear(String pTable) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, null);
			try (PreparedStatement ps = c.prepareStatement(info.clearSQL)) {
				ps.executeUpdate();
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#getCount(java.lang.String)
	 */
	@Override
	public long getCount(String pTable) {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			JDBCTableInfo info = mStore.validateTable(c, pTable, null);
			try (PreparedStatement ps = c.prepareStatement(info.getCountSQL)) {
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next() == false)
						return 0L;
					return rs.getLong(1);
				}
			}
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#getTableList()
	 */
	@Override
	public Iterator<@NonNull String> getTableList() {
		try {
			validateConnection();
			Connection c = mConnection;
			if (c == null)
				throw new IllegalStateException();
			ImmutableList.Builder<@NonNull String> results = ImmutableList.builder();
			try (ResultSet rs = c.getMetaData().getTables(null, mStore.getTableSchema(), null, null)) {
				while (rs.next() == true) {
					String str = rs.getString(3);
					if (str == null)
						continue;
					results.add(str);
				}
			}
			return results.build().iterator();
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#commit()
	 */
	@Override
	public void commit() {
		try {
			Connection c = mConnection;
			if (c != null) {
				mConnection = null;
				c.commit();
				c.close();
			}
		}
		catch (SQLException ex) {
			try {
				Connection c = mConnection;
				if (c != null)
					c.close();
			}
			catch (SQLException ex2) {
				throw new RuntimeException(ex2);
			}
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTransaction#rollback()
	 */
	@Override
	public void rollback() {
		try {
			Connection c = mConnection;
			if (c != null) {
				mConnection = null;
				c.rollback();
				c.close();
			}
		}
		catch (SQLException ex) {
			try {
				Connection c = mConnection;
				if (c != null)
					c.close();
			}
			catch (SQLException ex2) {
				throw new RuntimeException(ex2);
			}
			throw new RuntimeException(ex);
		}
	}

}

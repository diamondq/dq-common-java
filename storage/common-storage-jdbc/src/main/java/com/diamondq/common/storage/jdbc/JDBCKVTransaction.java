package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVTransaction;
import com.google.common.collect.ImmutableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.DataSource;

/**
 * The Cloudant database is effectively flat. Thus, mapping the concept of a 'table' is done by concatenating the
 * 'table' to the front of any keys.
 */
public class JDBCKVTransaction implements IKVTransaction {

	private final JDBCKVStore	mStore;

	private final DataSource	mDataSource;

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
	public <O> O getByKey(String pTable, String pKey1, String pKey2, Class<O> pClass) {
		try {
			validateConnection();
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, pClass);
			try (PreparedStatement ps = mConnection.prepareStatement(info.getBySQL)) {
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
	public <O> void putByKey(String pTable, String pKey1, String pKey2, O pObj) {
		try {
			validateConnection();
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, (pObj == null ? null : pObj.getClass()));
			if (info.supportsUpsert == true) {
				try (PreparedStatement ps = mConnection.prepareStatement(info.putBySQL)) {
					ps.setString(1, pKey1);
					ps.setString(2, pKey2);
					info.serializer.serializeToPreparedStatement(pObj, ps, 2);
					ps.execute();
				}
			}
			else {
				boolean exists;
				try (PreparedStatement qps = mConnection.prepareStatement(info.putQueryBySQL)) {
					qps.setString(1, pKey1);
					qps.setString(2, pKey2);
					try (ResultSet rs = qps.executeQuery()) {
						exists = rs.next();
					}
				}
				if (exists == false) {
					try (PreparedStatement ps = mConnection.prepareStatement(info.putInsertBySQL)) {
						ps.setString(1, pKey1);
						ps.setString(2, pKey2);
						info.serializer.serializeToPreparedStatement(pObj, ps, 3);
						ps.execute();
					}
				}
				else {
					try (PreparedStatement ps = mConnection.prepareStatement(info.putUpdateBySQL)) {
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
	public boolean removeByKey(String pTable, String pKey1, String pKey2) {
		try {
			validateConnection();
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, null);
			try (PreparedStatement ps = mConnection.prepareStatement(info.removeBySQL)) {
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
	public Iterator<String> keyIterator(String pTable) {
		try {
			validateConnection();
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, null);
			PreparedStatement ps = mConnection.prepareStatement(info.keyIteratorSQL);
			try {
				ResultSet rs = ps.executeQuery();
				try {
					JDBCResultSetIterator result = new JDBCResultSetIterator(ps, rs);
					rs = null;
					ps = null;
					return result;
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
	public Iterator<String> keyIterator2(String pTable, String pKey1) {
		try {
			validateConnection();
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, null);
			PreparedStatement ps = mConnection.prepareStatement(info.keyIterator2SQL);
			try {
				ps.setString(1, pKey1);
				ResultSet rs = ps.executeQuery();
				try {
					JDBCResultSetIterator result = new JDBCResultSetIterator(ps, rs);
					rs = null;
					ps = null;
					return result;
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
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, null);
			try (PreparedStatement ps = mConnection.prepareStatement(info.clearSQL)) {
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
			JDBCTableInfo info = mStore.validateTable(mConnection, pTable, null);
			try (PreparedStatement ps = mConnection.prepareStatement(info.getCountSQL)) {
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
	public Iterator<String> getTableList() {
		try {
			validateConnection();
			ImmutableList.Builder<String> results = ImmutableList.builder();
			try (ResultSet rs = mConnection.getMetaData().getTables(null, mStore.getTableSchema(), null, null)) {
				while (rs.next() == true)
					results.add(rs.getString(3));
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
			if (mConnection != null) {
				mConnection.commit();
				Connection c = mConnection;
				mConnection = null;
				c.close();
			}
		}
		catch (SQLException ex) {
			try {
				if (mConnection != null)
					mConnection.close();
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
			if (mConnection != null) {
				mConnection.rollback();
				Connection c = mConnection;
				mConnection = null;
				c.close();
			}
		}
		catch (SQLException ex) {
			try {
				if (mConnection != null)
					mConnection.close();
			}
			catch (SQLException ex2) {
				throw new RuntimeException(ex2);
			}
			throw new RuntimeException(ex);
		}
	}

}

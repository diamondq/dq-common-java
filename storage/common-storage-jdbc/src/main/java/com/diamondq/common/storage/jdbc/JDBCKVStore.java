package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVAsyncTransaction;
import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.IKVIndexDefinition;
import com.diamondq.common.storage.kv.IKVIndexSupport;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTableDefinition;
import com.diamondq.common.storage.kv.IKVTableDefinitionSupport;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.KVColumnDefinitionBuilder;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;
import com.diamondq.common.storage.kv.impl.SyncWrapperAsyncKVTransaction;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCKVStore implements IKVStore, IKVIndexSupport<JDBCIndexColumnBuilder, JDBCIndexDefinitionBuilder>,
	IKVTableDefinitionSupport {

	private static final Logger	sLogger			= LoggerFactory.getLogger(JDBCKVStore.class);

	private static final String	sDATA_KEY		= "DATA";

	private static final String	sPRIMARY_KEY_2	= "PRIMARY_KEY_2";

	private static final String	sPRIMARY_KEY_1	= "PRIMARY_KEY_1";

	static final BigDecimal		sLONG_MIN_VALUE	= BigDecimal.valueOf(Long.MIN_VALUE);

	static final BigDecimal		sLONG_MAX_VALUE	= BigDecimal.valueOf(Long.MAX_VALUE);

	public static class JDBCKVStoreBuilder {
		private DataSource		mDatabase;

		private IJDBCDialect	mDialect;

		private String			mTableSchema;

		public JDBCKVStoreBuilder database(DataSource pDatabase) {
			mDatabase = pDatabase;
			return this;
		}

		public JDBCKVStoreBuilder dialect(IJDBCDialect pValue) {
			mDialect = pValue;
			return this;
		}

		public JDBCKVStoreBuilder tableSchema(String pValue) {
			mTableSchema = pValue;
			return this;
		}

		public JDBCKVStore build() {
			return new JDBCKVStore(mDatabase, mDialect, mTableSchema);
		}
	}

	private final DataSource					mDatabase;

	private final IJDBCDialect					mDialect;

	private final String						mTableSchema;

	private final Cache<String, JDBCTableInfo>	mTableCache;

	public JDBCKVStore(DataSource pDatabase, IJDBCDialect pDialect, String pTableSchema) {
		mDatabase = pDatabase;
		mDialect = pDialect;
		mTableSchema = (pTableSchema == null ? null : pTableSchema.toLowerCase());
		mTableCache = CacheBuilder.newBuilder().build();
		//
		// try {
		// DatabaseInfoHelper.getReservedWords(mDatabase.getConnection());
		// }
		// catch (SQLException ex) {
		// throw new RuntimeException(ex);
		// }
	}

	public static JDBCKVStoreBuilder builder() {
		return new JDBCKVStoreBuilder();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#startTransaction()
	 */
	@Override
	public IKVTransaction startTransaction() {
		return new JDBCKVTransaction(this, mDatabase);
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#startAsyncTransaction()
	 */
	@Override
	public IKVAsyncTransaction startAsyncTransaction() {
		return new SyncWrapperAsyncKVTransaction(startTransaction());
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#getIndexSupport()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <ICB extends KVIndexColumnBuilder<ICB>, IDB extends KVIndexDefinitionBuilder<IDB>> IKVIndexSupport<ICB, IDB> getIndexSupport() {
		return (IKVIndexSupport<ICB, IDB>) this;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexSupport#addRequiredIndexes(java.util.Collection)
	 */
	@Override
	public void addRequiredIndexes(Collection<IKVIndexDefinition> pIndexes) {
		Map<String, IKVIndexDefinition> indexByName = Maps.newHashMap();
		for (IKVIndexDefinition index : pIndexes)
			indexByName.put(index.getName(), index);

		// throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexSupport#createIndexColumnBuilder()
	 */
	@Override
	public JDBCIndexColumnBuilder createIndexColumnBuilder() {
		return new JDBCIndexColumnBuilder();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVIndexSupport#createIndexDefinitionBuilder()
	 */
	@Override
	public JDBCIndexDefinitionBuilder createIndexDefinitionBuilder() {
		return new JDBCIndexDefinitionBuilder();
	}

	/**
	 * Validates that the specified table is present and configured to the right structure
	 * 
	 * @param pConnection the connection
	 * @param pTable the table name
	 * @param pClass the class that will be stored in the table
	 * @return the table info
	 */
	public <O> JDBCTableInfo validateTable(Connection pConnection, String pTable, Class<O> pClass) {
		JDBCTableInfo tableInfo = mTableCache.getIfPresent(pTable);
		String mungedTableName = escapeTableName(pTable);
		if (tableInfo == null) {

			/* Query the database to see if the table exists */

			try {

				String matchingSchema = null;
				if (getTableSchema() != null) {

					/* Check the schema */

					boolean missingSchema = true;
					try (ResultSet rs = pConnection.getMetaData().getSchemas(null, null)) {
						while (rs.next() == true) {
							String testName = rs.getString(1).toLowerCase();
							if (getTableSchema().equals(testName) == true) {
								missingSchema = false;
								matchingSchema = testName;
								break;
							}
						}
					}

					if (missingSchema == true) {
						try (PreparedStatement ps =
							pConnection.prepareStatement(mDialect.generateCreateSchemaSQL(getTableSchema()))) {
							ps.execute();
						}

						try (ResultSet rs = pConnection.getMetaData().getSchemas(null, null)) {
							while (rs.next() == true) {
								String testName = rs.getString(1);
								if (getTableSchema().equals(testName) == true) {
									matchingSchema = testName;
									break;
								}
							}
						}
					}
				}

				/* Check the table itself */

				boolean missingTable = true;
				try (ResultSet rs = pConnection.getMetaData().getTables(null, matchingSchema, null, null)) {
					while (rs.next() == true) {
						String testName = rs.getString(3).toLowerCase();
						if (mungedTableName.equals(testName) == true) {

							/* Validate the fields */

							missingTable = false;
							break;
						}
					}
				}

				if (missingTable == true) {
					StringBuilder sb = new StringBuilder();
					sb.append("CREATE TABLE ");
					if (getTableSchema() != null)
						sb.append(getTableSchema()).append('.');
					sb.append(mungedTableName);
					sb.append(" (");
					sb.append(sPRIMARY_KEY_1);
					sb.append(" varchar(1024),");
					sb.append(sPRIMARY_KEY_2);
					sb.append(" varchar(1024),");
					sb.append(sDATA_KEY);
					sb.append(" ").append(mDialect.getUnlimitedTextType()).append(",");
					sb.append(" PRIMARY KEY(");
					sb.append(sPRIMARY_KEY_1);
					sb.append(',');
					sb.append(sPRIMARY_KEY_2);
					sb.append(')');
					sb.append(")");

					sLogger.info("Constructing table via {}", sb.toString());
					try (PreparedStatement ps = pConnection.prepareStatement(sb.toString())) {
						ps.execute();
					}
				}
			}
			catch (SQLException ex) {
				throw new RuntimeException(ex);
			}

			/* Generate all the SQL */

			/* Get By */

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT ").append(sDATA_KEY).append(" FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ").append(sPRIMARY_KEY_1).append("=?");
			sb.append(" AND ").append(sPRIMARY_KEY_2).append("=?");
			String getBySQL = sb.toString();

			/* Supports Upsert */

			boolean supportsUpsert = false;

			/* putBySQL */

			String putBySQL = "";

			/* put query */

			sb = new StringBuilder();
			sb.append("SELECT 1 FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ").append(sPRIMARY_KEY_1).append("=?");
			sb.append(" AND ").append(sPRIMARY_KEY_2).append("=?");
			String putQueryBySQL = sb.toString();

			/* put insert */

			sb = new StringBuilder();
			sb.append("INSERT INTO ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append('(');
			sb.append(sPRIMARY_KEY_1).append(',');
			sb.append(sPRIMARY_KEY_2).append(',');
			sb.append(sDATA_KEY);
			sb.append(") VALUES (?, ?, ?)");
			String putInsertBySQL = sb.toString();

			/* put update */

			sb = new StringBuilder();
			sb.append("UPDATE ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" SET ");
			sb.append(sDATA_KEY).append("=?");
			sb.append(" WHERE ");
			sb.append(sPRIMARY_KEY_1).append("=? AND ");
			sb.append(sPRIMARY_KEY_2).append("=?");
			String putUpdateBySQL = sb.toString();

			/* remove */

			sb = new StringBuilder();
			sb.append("DELETE FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ");
			sb.append(sPRIMARY_KEY_1).append("=? AND ");
			sb.append(sPRIMARY_KEY_2).append("=?");
			String removeBySQL = sb.toString();

			/* get count */

			sb = new StringBuilder();
			sb.append("SELECT count(1) FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			String getCountSQL = sb.toString();

			/* clear sql */

			sb = new StringBuilder();
			sb.append("DELETE FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			String clearSQL = sb.toString();

			/* key iterator */

			sb = new StringBuilder();
			sb.append("SELECT distinct ");
			sb.append(sPRIMARY_KEY_1);
			sb.append(" FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			String keyIteratorSQL = sb.toString();

			/* key iterator 2 */

			sb = new StringBuilder();
			sb.append("SELECT ");
			sb.append(sPRIMARY_KEY_2);
			sb.append(" FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ");
			sb.append(sPRIMARY_KEY_1).append("=?");
			String keyIterator2SQL = sb.toString();

			IResultSetDeserializer deserializer = new JDBCJsonDeserializer();
			IPreparedStatementSerializer serializer = new JDBCJsonSerializer();

			tableInfo =
				new JDBCTableInfo(getBySQL, supportsUpsert, putBySQL, putQueryBySQL, putInsertBySQL, putUpdateBySQL,
					deserializer, serializer, removeBySQL, getCountSQL, clearSQL, keyIteratorSQL, keyIterator2SQL);
			mTableCache.put(pTable, tableInfo);
		}
		return tableInfo;
	}

	/**
	 * Makes sure that the table name is a valid table name by escaping any illegal characters
	 * 
	 * @param pTable the unescaped name
	 * @return the escaped name
	 */
	private String escapeTableName(String pTable) {
		return pTable.toLowerCase();
	}

	/**
	 * Returns the table schema to be used
	 * 
	 * @return the table schema
	 */
	public String getTableSchema() {
		return mTableSchema;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVStore#getTableDefinitionSupport()
	 */
	@Override
	public IKVTableDefinitionSupport getTableDefinitionSupport() {
		return this;
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTableDefinitionSupport#createTableDefinitionBuilder()
	 */
	@Override
	public KVTableDefinitionBuilder createTableDefinitionBuilder() {
		return new JDBCTableDefinitionBuilder();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTableDefinitionSupport#createColumnDefinitionBuilder()
	 */
	@Override
	public KVColumnDefinitionBuilder createColumnDefinitionBuilder() {
		return new JDBCColumnDefinitionBuilder();
	}

	/**
	 * @see com.diamondq.common.storage.kv.IKVTableDefinitionSupport#addTableDefinition(com.diamondq.common.storage.kv.IKVTableDefinition)
	 */
	@Override
	public void addTableDefinition(IKVTableDefinition pDefinition) {
		JDBCTableInfo tableInfo = mTableCache.getIfPresent(pDefinition.getTableName());
		if (tableInfo == null) {
			String mungedTableName = escapeTableName(pDefinition.getTableName());

			/* Query the database to see if the table exists */

			try {
				try (Connection connection = mDatabase.getConnection()) {
					connection.setAutoCommit(true);

					String matchingSchema = null;
					if (getTableSchema() != null) {

						/* Check the schema */

						boolean missingSchema = true;
						try (ResultSet rs = connection.getMetaData().getSchemas(null, null)) {
							while (rs.next() == true) {
								String testName = rs.getString(1).toLowerCase();
								if (getTableSchema().equals(testName) == true) {
									missingSchema = false;
									matchingSchema = testName;
									break;
								}
							}
						}

						if (missingSchema == true) {
							try (PreparedStatement ps =
								connection.prepareStatement(mDialect.generateCreateSchemaSQL(getTableSchema()))) {
								ps.execute();
							}

							try (ResultSet rs = connection.getMetaData().getSchemas(null, null)) {
								while (rs.next() == true) {
									String testName = rs.getString(1);
									if (getTableSchema().equals(testName) == true) {
										matchingSchema = testName;
										break;
									}
								}
							}
						}
					}

					/* Check the table itself */

					boolean missingTable = true;
					try (ResultSet rs = connection.getMetaData().getTables(null, matchingSchema, null, null)) {
						while (rs.next() == true) {
							String testName = rs.getString(3).toLowerCase();
							if (mungedTableName.equals(testName) == true) {

								/* Validate the fields */

								missingTable = false;
								break;
							}
						}
					}

					if (missingTable == true) {
						StringBuilder sb = new StringBuilder();
						sb.append("CREATE TABLE ");
						if (getTableSchema() != null)
							sb.append(getTableSchema()).append('.');
						sb.append(mungedTableName);
						sb.append(" (");
						sb.append(sPRIMARY_KEY_1);
						sb.append(" varchar(1024),");
						sb.append(sPRIMARY_KEY_2);
						sb.append(" varchar(1024),");
						for (IKVColumnDefinition cd : pDefinition.getColumnDefinitions()) {
							sb.append(' ');
							sb.append(escapeColumnName(cd.getName()));
							sb.append(' ');
							switch (cd.getType()) {
							case Boolean: {
								sb.append(mDialect.getBooleanType());
								break;
							}
							case Decimal: {

								/* If the decimal is actually the long range, then let's use long support */

								BigDecimal minValue = cd.getMinValue();
								BigDecimal maxValue = cd.getMaxValue();
								if ((minValue != null) && (minValue.equals(sLONG_MIN_VALUE)) && (maxValue != null)
									&& (maxValue.equals(sLONG_MAX_VALUE))) {
									sb.append(mDialect.getLongType());
								}
								else
									sb.append(mDialect.getUnlimitedDecimalType());
								break;
							}
							case Integer: {
								sb.append(mDialect.getIntegerType());
								break;
							}
							case String: {
								Integer maxLength = cd.getMaxLength();
								if (maxLength != null)
									sb.append(mDialect.getTextType(maxLength));
								else
									sb.append(mDialect.getUnlimitedTextType());
								break;
							}
							case Timestamp: {
								sb.append(mDialect.getTimestampType());
								break;
							}
							}
							sb.append(',');
						}
						sb.append(" PRIMARY KEY(");
						sb.append(sPRIMARY_KEY_1);
						sb.append(',');
						sb.append(sPRIMARY_KEY_2);
						sb.append(')');
						sb.append(")");

						sLogger.info("Constructing table via {}", sb.toString());

						try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
							ps.execute();
						}
					}
				}
			}
			catch (SQLException ex) {
				throw new RuntimeException(ex);
			}

			/* Generate all the SQL */

			/* Get By */

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT ");
			sb.append(String.join(",",
				Iterables.transform(pDefinition.getColumnDefinitions(), (cd) -> escapeColumnName(cd.getName()))));
			sb.append(" FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ").append(sPRIMARY_KEY_1).append("=?");
			sb.append(" AND ").append(sPRIMARY_KEY_2).append("=?");
			String getBySQL = sb.toString();

			/* Supports Upsert */

			boolean supportsUpsert = false;

			/* putBySQL */

			String putBySQL = "";

			/* put query */

			sb = new StringBuilder();
			sb.append("SELECT 1 FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ").append(sPRIMARY_KEY_1).append("=?");
			sb.append(" AND ").append(sPRIMARY_KEY_2).append("=?");
			String putQueryBySQL = sb.toString();

			/* put insert */

			sb = new StringBuilder();
			sb.append("INSERT INTO ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append('(');
			sb.append(sPRIMARY_KEY_1).append(',');
			sb.append(sPRIMARY_KEY_2).append(',');
			sb.append(String.join(",",
				Iterables.transform(pDefinition.getColumnDefinitions(), (cd) -> escapeColumnName(cd.getName()))));
			sb.append(") VALUES (?, ?, ");
			sb.append(String.join(",", Iterables.transform(pDefinition.getColumnDefinitions(), (cd) -> "?")));
			sb.append(")");
			String putInsertBySQL = sb.toString();

			/* put update */

			sb = new StringBuilder();
			sb.append("UPDATE ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" SET ");
			sb.append(String.join(",", Iterables.transform(pDefinition.getColumnDefinitions(),
				(cd) -> escapeColumnName(cd.getName()) + "=?")));
			sb.append(" WHERE ");
			sb.append(sPRIMARY_KEY_1).append("=? AND ");
			sb.append(sPRIMARY_KEY_2).append("=?");
			String putUpdateBySQL = sb.toString();

			/* remove */

			sb = new StringBuilder();
			sb.append("DELETE FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ");
			sb.append(sPRIMARY_KEY_1).append("=? AND ");
			sb.append(sPRIMARY_KEY_2).append("=?");
			String removeBySQL = sb.toString();

			/* get count */

			sb = new StringBuilder();
			sb.append("SELECT count(1) FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			String getCountSQL = sb.toString();

			/* clear sql */

			sb = new StringBuilder();
			sb.append("DELETE FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			String clearSQL = sb.toString();

			/* key iterator */

			sb = new StringBuilder();
			sb.append("SELECT distinct ");
			sb.append(sPRIMARY_KEY_1);
			sb.append(" FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			String keyIteratorSQL = sb.toString();

			/* key iterator 2 */

			sb = new StringBuilder();
			sb.append("SELECT ");
			sb.append(sPRIMARY_KEY_2);
			sb.append(" FROM ");
			if (getTableSchema() != null)
				sb.append(getTableSchema()).append('.');
			sb.append(mungedTableName);
			sb.append(" WHERE ");
			sb.append(sPRIMARY_KEY_1).append("=?");
			String keyIterator2SQL = sb.toString();

			IResultSetDeserializer deserializer = new JDBCColumnDeserializer(mDialect, pDefinition);
			IPreparedStatementSerializer serializer = new JDBCColumnSerializer(mDialect, pDefinition);

			tableInfo =
				new JDBCTableInfo(getBySQL, supportsUpsert, putBySQL, putQueryBySQL, putInsertBySQL, putUpdateBySQL,
					deserializer, serializer, removeBySQL, getCountSQL, clearSQL, keyIteratorSQL, keyIterator2SQL);
			mTableCache.put(pDefinition.getTableName(), tableInfo);
		}
	}

	private String escapeColumnName(String pName) {
		StringBuilder sb = new StringBuilder();
		char[] chars = pName.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isUpperCase(chars[i])) {
				if (i > 0)
					sb.append('_');
				sb.append(Character.toLowerCase(chars[i]));
			}
			else
				sb.append(chars[i]);
		}
		String partial = sb.toString();
		if (mDialect.getReservedWords().contains(partial) == true) {
			partial = partial + "_";
		}
		return partial;
	}
}

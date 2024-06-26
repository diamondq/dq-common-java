package com.diamondq.common.storage.jdbc;

import com.diamondq.common.builders.IBuilder;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.storage.kv.IKVAsyncTransaction;
import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.IKVIndexColumn;
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
import com.diamondq.common.storage.kv.Query;
import com.diamondq.common.storage.kv.SyncWrapperAsyncKVTransaction;
import com.diamondq.common.storage.kv.WhereInfo;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JDBCKVStore implements IKVStore, IKVIndexSupport<JDBCIndexColumnBuilder, JDBCIndexDefinitionBuilder>,
  IKVTableDefinitionSupport<JDBCTableDefinitionBuilder, JDBCColumnDefinitionBuilder> {

  private static final Logger sLogger = LoggerFactory.getLogger(JDBCKVStore.class);

  public static final String sPRIMARY_KEY_2 = "PRIMARY_KEY_2";

  public static final String sPRIMARY_KEY_1 = "PRIMARY_KEY_1";

  static final BigDecimal sLONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);

  static final BigDecimal sLONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);

  public static class JDBCKVStoreBuilder implements IBuilder<IKVStore> {

    @Nullable protected ContextFactory contextFactory;

    @Nullable protected DataSource datasource;

    @Nullable protected IJDBCDialect dialect;

    @Nullable protected String mTableSchema;

    @Deprecated
    public JDBCKVStoreBuilder database(DataSource pDatabase) {
      datasource = pDatabase;
      return this;
    }

    public JDBCKVStoreBuilder contextFactory(ContextFactory pContextFactory) {
      contextFactory = pContextFactory;
      return this;
    }

    public JDBCKVStoreBuilder datasource(DataSource pDatabase) {
      datasource = pDatabase;
      return this;
    }

    public JDBCKVStoreBuilder dialect(IJDBCDialect pValue) {
      dialect = pValue;
      return this;
    }

    public JDBCKVStoreBuilder tableSchema(String pValue) {
      mTableSchema = pValue;
      return this;
    }

    public void onActivate(Map<String, Object> pProps) {
      mTableSchema = PropertiesParsing.getNullableString(pProps, ".tableSchema");
    }

    @Override
    public JDBCKVStore build() {
      DataSource localDatabase = datasource;
      if (localDatabase == null) throw new IllegalArgumentException("datasource not set in JDBCKVStoreBuilder");
      IJDBCDialect localDialect = dialect;
      if (localDialect == null) throw new IllegalArgumentException("dialect not set in JDBCKVStoreBuilder");
      ContextFactory localContextFactory = contextFactory;
      if (localContextFactory == null)
        throw new IllegalArgumentException("contextFactory not set in JDBCKVStoreBuilder");
      return new JDBCKVStore(localContextFactory, localDatabase, localDialect, mTableSchema);
    }
  }

  private final ContextFactory mContextFactory;

  private final DataSource mDatabase;

  private final IJDBCDialect mDialect;

  @Nullable private final String mTableSchema;

  private final Cache<String, JDBCTableInfo> mTableCache;

  public JDBCKVStore(ContextFactory pContextFactory, DataSource pDatabase, IJDBCDialect pDialect,
    @Nullable String pTableSchema) {
    mContextFactory = pContextFactory;
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
    return new JDBCKVTransaction(mContextFactory, this, mDatabase);
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
  public <ICB extends @NotNull KVIndexColumnBuilder<@NotNull ICB>, IDB extends @NotNull KVIndexDefinitionBuilder<@NotNull IDB>> @Nullable IKVIndexSupport<@NotNull ICB, @NotNull IDB> getIndexSupport() {
    return (IKVIndexSupport<ICB, IDB>) this;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexSupport#addRequiredIndexes(java.util.Collection)
   */
  @Override
  public void addRequiredIndexes(Collection<@NotNull IKVIndexDefinition> pIndexes) {
    try (Context context = mContextFactory.newContext(JDBCKVStore.class, this, pIndexes)) {
      Map<@NotNull String, @NotNull IKVIndexDefinition> indexByName = Maps.newHashMap();
      for (IKVIndexDefinition index : pIndexes) {
        indexByName.put(index.getName(), index);
        String indexName = index.getName().toLowerCase();
        String tableName = index.getTableName();

        String mungedTableName = escapeTableName(tableName);

        /* Query the database to see if the table exists */

        String tableSchema = getTableSchema();

        UserTransaction userTransaction = context.getData(UserTransaction.class.getName(), true, UserTransaction.class);

        try {
          try (Connection connection = mDatabase.getConnection()) {
            if ((userTransaction == null) || (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION))
              connection.setAutoCommit(true);

            /* Find the actual schema */

            String matchingSchema = null;
            if (tableSchema != null) {

              /* Check the schema */

              boolean missingSchema = true;
              try (ResultSet rs = connection.getMetaData().getSchemas(null, null)) {
                while (rs.next() == true) {
                  String str = rs.getString(1);
                  if (str == null) continue;
                  String testName = str.toLowerCase();
                  if (tableSchema.equals(testName) == true) {
                    missingSchema = false;
                    matchingSchema = str;
                    break;
                  }
                }
              }
              if (missingSchema == true) throw new IllegalStateException();
            }

            /* Check the table itself */

            boolean missingIndex = true;
            try (ResultSet rs = connection.getMetaData().getIndexInfo(null, matchingSchema, tableName, false, false)) {
              while (rs.next() == true) {
                String str = rs.getString(6);
                if (str == null) continue;
                String testName = str.toLowerCase();
                if (indexName.equals(testName) == true) {

                  /* TODO: Validate the fields */

                  missingIndex = false;
                  break;
                }
              }
            }

            if (missingIndex == true) {
              StringBuilder sb = new StringBuilder();
              sb.append("CREATE INDEX ");
              if (tableSchema != null) sb.append(tableSchema).append('.');
              sb.append(indexName);
              sb.append(" on ");
              if (tableSchema != null) sb.append(tableSchema).append('.');
              sb.append(mungedTableName);
              sb.append(" (");
              boolean firstCol = true;
              for (IKVIndexColumn cd : index.getColumns()) {
                if (firstCol == true) firstCol = false;
                else sb.append(", ");
                sb.append(escapeColumnName(cd.getName()));
              }
              sb.append(')');

              sLogger.info("Constructing index via {}", sb.toString());

              try (PreparedStatement ps = connection.prepareStatement(sb.toString())) {
                ps.execute();
              }
            }
          }
        }
        catch (SQLException | SystemException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
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
   * @return the table info
   */
  public JDBCTableInfo validateTable(Connection pConnection, String pTable) {
    JDBCTableInfo tableInfo = mTableCache.getIfPresent(pTable);
    if (tableInfo == null) throw new IllegalStateException("The table has not yet been defined.");
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
  public @Nullable String getTableSchema() {
    return mTableSchema;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVStore
   */
  @SuppressWarnings("unchecked")
  @Override
  public <TDB extends @NotNull KVTableDefinitionBuilder<@NotNull TDB>, CDB extends @NotNull KVColumnDefinitionBuilder<@NotNull CDB>> @Nullable IKVTableDefinitionSupport<@NotNull TDB, @NotNull CDB> getTableDefinitionSupport() {
    return (@Nullable IKVTableDefinitionSupport<@NotNull TDB, @NotNull CDB>) this;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinitionSupport#createTableDefinitionBuilder()
   */
  @Override
  public JDBCTableDefinitionBuilder createTableDefinitionBuilder() {
    return new JDBCTableDefinitionBuilder();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinitionSupport#createColumnDefinitionBuilder()
   */
  @Override
  public JDBCColumnDefinitionBuilder createColumnDefinitionBuilder() {
    return new JDBCColumnDefinitionBuilder();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinitionSupport#addTableDefinition(com.diamondq.common.storage.kv.IKVTableDefinition)
   */
  @Override
  public void addTableDefinition(IKVTableDefinition pDefinition) {
    try (Context context = mContextFactory.newContext(JDBCKVStore.class, this, pDefinition)) {
      JDBCTableInfo tableInfo = mTableCache.getIfPresent(pDefinition.getTableName());
      if (tableInfo == null) {
        String mungedTableName = escapeTableName(pDefinition.getTableName());

        /* Query the database to see if the table exists */

        String tableSchema = getTableSchema();

        UserTransaction userTransaction = context.getData(UserTransaction.class.getName(), true, UserTransaction.class);

        try {
          try (Connection connection = mDatabase.getConnection()) {
            if ((userTransaction == null) || (userTransaction.getStatus() == Status.STATUS_NO_TRANSACTION))
              connection.setAutoCommit(true);

            String matchingSchema = null;
            if (tableSchema != null) {

              /* Check the schema */

              boolean missingSchema = true;
              try (ResultSet rs = connection.getMetaData().getSchemas(null, null)) {
                while (rs.next() == true) {
                  String str = rs.getString(1);
                  if (str == null) continue;
                  String testName = str.toLowerCase();
                  if (tableSchema.equals(testName) == true) {
                    missingSchema = false;
                    matchingSchema = str;
                    break;
                  }
                }
              }

              if (missingSchema == true) {
                try (
                  PreparedStatement ps = connection.prepareStatement(mDialect.generateCreateSchemaSQL(tableSchema))) {
                  ps.execute();
                }

                try (ResultSet rs = connection.getMetaData().getSchemas(null, null)) {
                  while (rs.next() == true) {
                    String testName = rs.getString(1);
                    if (tableSchema.equals(testName) == true) {
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
                String str = rs.getString(3);
                if (str == null) continue;
                String testName = str.toLowerCase();
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
              if (tableSchema != null) sb.append(tableSchema).append('.');
              sb.append(mungedTableName);
              sb.append(" (");
              sb.append(sPRIMARY_KEY_1);
              sb.append(" ").append(mDialect.getTextType(1024)).append(',');
              sb.append(sPRIMARY_KEY_2);
              sb.append(" ").append(mDialect.getTextType(1024));
              for (IKVColumnDefinition cd : pDefinition.getColumnDefinitions()) {
                sb.append(", ");
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
                    } else sb.append(mDialect.getUnlimitedDecimalType());
                    BigDecimal autoIncrementStart = cd.getAutoIncrementStart();
                    BigDecimal autoIncrementBy = cd.getAutoIncrementBy();
                    if (autoIncrementStart != null) sb.append(' ')
                      .append(mDialect.getAutoIncrement(cd.getType(), autoIncrementStart, autoIncrementBy));
                    break;
                  }
                  case Integer: {
                    sb.append(mDialect.getIntegerType());
                    BigDecimal autoIncrementStart = cd.getAutoIncrementStart();
                    BigDecimal autoIncrementBy = cd.getAutoIncrementBy();
                    if (autoIncrementStart != null) sb.append(' ')
                      .append(mDialect.getAutoIncrement(cd.getType(), autoIncrementStart, autoIncrementBy));
                    break;
                  }
                  case Long: {
                    sb.append(mDialect.getLongType());
                    BigDecimal autoIncrementStart = cd.getAutoIncrementStart();
                    BigDecimal autoIncrementBy = cd.getAutoIncrementBy();
                    if (autoIncrementStart != null) sb.append(' ')
                      .append(mDialect.getAutoIncrement(cd.getType(), autoIncrementStart, autoIncrementBy));
                    break;
                  }
                  case String: {
                    Integer maxLength = cd.getMaxLength();
                    if (maxLength != null) sb.append(mDialect.getTextType(maxLength));
                    else sb.append(mDialect.getUnlimitedTextType());
                    break;
                  }
                  case Timestamp: {
                    sb.append(mDialect.getTimestampType());
                    break;
                  }
                  case UUID: {
                    sb.append(mDialect.getUUIDType());
                    break;
                  }
                  case Binary: {
                    Integer maxLength = cd.getMaxLength();
                    if (maxLength != null) sb.append(mDialect.getBinaryType(maxLength));
                    else throw new UnsupportedOperationException();
                    break;
                  }
                }
              }
              sb.append(", PRIMARY KEY(");
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
        catch (SQLException | SystemException ex) {
          throw new RuntimeException(ex);
        }

        /* Generate all the SQL */

        /* Get By */

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (pDefinition.getColumnDefinitions().isEmpty()) sb.append("1");
        else sb.append(String.join(",",
          Iterables.transform(pDefinition.getColumnDefinitions(),
            (cd) -> cd == null ? null : escapeColumnName(cd.getName())
          )
        ));
        sb.append(" FROM ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
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
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        sb.append(" WHERE ").append(sPRIMARY_KEY_1).append("=?");
        sb.append(" AND ").append(sPRIMARY_KEY_2).append("=?");
        String putQueryBySQL = sb.toString();

        /* put insert */

        sb = new StringBuilder();
        sb.append("INSERT INTO ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        sb.append('(');
        sb.append(sPRIMARY_KEY_1).append(',');
        sb.append(sPRIMARY_KEY_2);
        if (pDefinition.getColumnDefinitions().isEmpty() == false) {
          sb.append(',');
          sb.append(String.join(",",
            Iterables.transform(pDefinition.getColumnDefinitions(),
              (cd) -> cd == null ? null : escapeColumnName(cd.getName())
            )
          ));
        }
        sb.append(") VALUES (?, ?");
        if (pDefinition.getColumnDefinitions().isEmpty() == false) {
          sb.append(", ");
          sb.append(String.join(", ", Iterables.transform(pDefinition.getColumnDefinitions(), (cd) -> "?")));
        }
        sb.append(")");
        String putInsertBySQL = sb.toString();

        /* put update */

        sb = new StringBuilder();
        sb.append("UPDATE ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        sb.append(" SET ");
        sb.append(String.join(",",
          Iterables.transform(pDefinition.getColumnDefinitions(),
            (cd) -> cd == null ? null : escapeColumnName(cd.getName()) + "=?"
          )
        ));
        sb.append(" WHERE ");
        sb.append(sPRIMARY_KEY_1).append("=? AND ");
        sb.append(sPRIMARY_KEY_2).append("=?");
        String putUpdateBySQL = sb.toString();

        /* remove */

        sb = new StringBuilder();
        sb.append("DELETE FROM ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        sb.append(" WHERE ");
        sb.append(sPRIMARY_KEY_1).append("=? AND ");
        sb.append(sPRIMARY_KEY_2).append("=?");
        String removeBySQL = sb.toString();

        /* get count */

        sb = new StringBuilder();
        sb.append("SELECT count(1) FROM ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        String getCountSQL = sb.toString();

        /* clear sql */

        sb = new StringBuilder();
        sb.append("DELETE FROM ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        String clearSQL = sb.toString();

        /* key iterator */

        sb = new StringBuilder();
        sb.append("SELECT distinct ");
        sb.append(sPRIMARY_KEY_1);
        sb.append(" FROM ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        String keyIteratorSQL = sb.toString();

        /* key iterator 2 */

        sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(sPRIMARY_KEY_2);
        sb.append(" FROM ");
        if (tableSchema != null) sb.append(tableSchema).append('.');
        sb.append(mungedTableName);
        sb.append(" WHERE ");
        sb.append(sPRIMARY_KEY_1).append("=?");
        String keyIterator2SQL = sb.toString();

        IResultSetDeserializer deserializer = new JDBCColumnDeserializer(mDialect, pDefinition);
        IPreparedStatementSerializer serializer = new JDBCColumnSerializer(mDialect, pDefinition);

        tableInfo = new JDBCTableInfo(getBySQL,
          supportsUpsert,
          putBySQL,
          putQueryBySQL,
          putInsertBySQL,
          putUpdateBySQL,
          deserializer,
          serializer,
          removeBySQL,
          getCountSQL,
          clearSQL,
          keyIteratorSQL,
          keyIterator2SQL,
          mungedTableName,
          tableSchema,
          pDefinition
        );
        mTableCache.put(pDefinition.getTableName(), tableInfo);
      }
    }
  }

  private String escapeColumnName(String pName) {
    StringBuilder sb = new StringBuilder();
    char[] chars = pName.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (Character.isUpperCase(chars[i])) {
        if (i > 0) sb.append('_');
        sb.append(Character.toLowerCase(chars[i]));
      } else sb.append(chars[i]);
    }
    String partial = sb.toString();
    if (mDialect.getReservedWords().contains(partial) == true) {
      partial = partial + "_";
    }
    return partial;
  }

  /**
   * Lookup or build the SQL query necessary to perform the query
   *
   * @param pInfo the table info
   * @param pQuery the query
   * @return the SQL
   */
  public String getQuerySQL(JDBCTableInfo pInfo, Query pQuery) {
    String querySQL = pInfo.querySQL.get(pQuery);
    if (querySQL != null) return querySQL;
    try (Context context = mContextFactory.newContext(JDBCKVStore.class, this, pInfo, pQuery)) {

      /* Build the query sql */

      StringBuilder sb = new StringBuilder();

      sb.append("SELECT ");

      sb.append(String.join(",",
        Iterables.concat(Iterables.transform(pInfo.definition.getColumnDefinitions(),
          (cd) -> cd == null ? null : escapeColumnName(cd.getName())
        ), Lists.newArrayList(sPRIMARY_KEY_1, sPRIMARY_KEY_2))
      ));
      sb.append(" FROM ");
      if (pInfo.tableSchema != null) sb.append(pInfo.tableSchema).append('.');
      sb.append(pInfo.mungedTableName);

      List<WhereInfo> whereList = pQuery.getWhereList();
      if (whereList.isEmpty() == false) {
        sb.append(" WHERE ");
        boolean isFirstWhere = true;
        for (WhereInfo where : whereList) {
          if (isFirstWhere == true) isFirstWhere = false;
          else sb.append(" AND ");
          IKVColumnDefinition colDef = pInfo.definition.getColumnDefinitionsByName(where.key);
          if (colDef == null) {
            String singlePrimaryKeyName = pInfo.definition.getSinglePrimaryKeyName();
            if ((singlePrimaryKeyName == null) || (where.key.equals(singlePrimaryKeyName) == false))
              throw new IllegalArgumentException();
            sb.append(sPRIMARY_KEY_1);
            sb.append("=");
            sb.append("'__ROOT__'");
            sb.append(" AND ");
            sb.append(sPRIMARY_KEY_2);
          } else sb.append(escapeColumnName(colDef.getName()));
          switch (where.operator) {
            case eq:
              sb.append("=");
              break;
            case gt:
              sb.append(">");
              break;
            case gte:
              sb.append(">=");
              break;
            case lt:
              sb.append("<");
              break;
            case lte:
              sb.append("<=");
              break;
            case ne:
              sb.append("!=");
              break;
          }
          sb.append("?");
        }
      }

      List<Pair<String, Boolean>> sortList = pQuery.getSortList();
      if (sortList.isEmpty() == false) {
        sb.append(" ORDER BY ");
        boolean firstSort = true;
        for (Pair<String, Boolean> sort : sortList) {
          if (firstSort == true) firstSort = false;
          else sb.append(", ");
          IKVColumnDefinition colDef = pInfo.definition.getColumnDefinitionsByName(sort.getValue0());
          if (colDef == null) {
            String singlePrimaryKeyName = pInfo.definition.getSinglePrimaryKeyName();
            if ((singlePrimaryKeyName == null) || (sort.getValue0().equals(singlePrimaryKeyName) == false))
              throw new IllegalArgumentException();
            sb.append(sPRIMARY_KEY_2);
          } else sb.append(escapeColumnName(colDef.getName()));
          if (sort.getValue1() == true) sb.append(" ASC");
          else sb.append(" DESC");
        }
      }

      String limitKey = pQuery.getLimitKey();
      if (limitKey != null) {
        sb.append(" ").append(mDialect.getLimitKeyword()).append("?");
      }
      String newQuerySQL = sb.toString();
      if ((querySQL = pInfo.querySQL.putIfAbsent(pQuery, newQuerySQL)) == null) querySQL = newQuerySQL;
    }

    return querySQL;
  }

  public String getCountSQL(JDBCTableInfo pInfo, Query pQuery) {
    String querySQL = pInfo.countSQL.get(pQuery);
    if (querySQL != null) return querySQL;
    try (Context context = mContextFactory.newContext(JDBCKVStore.class, this, pInfo, pQuery)) {

      /* Build the query sql */

      StringBuilder sb = new StringBuilder();

      sb.append("SELECT ");

      sb.append("COUNT(1)");

      sb.append(" FROM ");
      if (pInfo.tableSchema != null) sb.append(pInfo.tableSchema).append('.');
      sb.append(pInfo.mungedTableName);

      List<WhereInfo> whereList = pQuery.getWhereList();
      if (whereList.isEmpty() == false) {
        sb.append(" WHERE ");
        boolean isFirstWhere = true;
        for (WhereInfo where : whereList) {
          if (isFirstWhere == true) isFirstWhere = false;
          else sb.append(" AND ");
          IKVColumnDefinition colDef = pInfo.definition.getColumnDefinitionsByName(where.key);
          if (colDef == null) {
            String singlePrimaryKeyName = pInfo.definition.getSinglePrimaryKeyName();
            if ((singlePrimaryKeyName == null) || (where.key.equals(singlePrimaryKeyName) == false))
              throw new IllegalArgumentException();
            sb.append(sPRIMARY_KEY_1);
            sb.append("=");
            sb.append("'__ROOT__'");
            sb.append(" AND ");
            sb.append(sPRIMARY_KEY_2);
          } else sb.append(escapeColumnName(colDef.getName()));
          switch (where.operator) {
            case eq:
              sb.append("=");
              break;
            case gt:
              sb.append(">");
              break;
            case gte:
              sb.append(">=");
              break;
            case lt:
              sb.append("<");
              break;
            case lte:
              sb.append("<=");
              break;
            case ne:
              sb.append("!=");
              break;
          }
          sb.append("?");
        }
      }

      List<Pair<String, Boolean>> sortList = pQuery.getSortList();
      if (sortList.isEmpty() == false) {
        sb.append(" ORDER BY ");
        boolean firstSort = true;
        for (Pair<String, Boolean> sort : sortList) {
          if (firstSort == true) firstSort = false;
          else sb.append(", ");
          IKVColumnDefinition colDef = pInfo.definition.getColumnDefinitionsByName(sort.getValue0());
          if (colDef == null) {
            String singlePrimaryKeyName = pInfo.definition.getSinglePrimaryKeyName();
            if ((singlePrimaryKeyName == null) || (sort.getValue0().equals(singlePrimaryKeyName) == false))
              throw new IllegalArgumentException();
            sb.append(sPRIMARY_KEY_2);
          } else sb.append(escapeColumnName(colDef.getName()));
          if (sort.getValue1() == true) sb.append(" ASC");
          else sb.append(" DESC");
        }
      }

      String newQuerySQL = sb.toString();
      if ((querySQL = pInfo.countSQL.putIfAbsent(pQuery, newQuerySQL)) == null) querySQL = newQuerySQL;
    }

    return querySQL;
  }

  public IJDBCDialect getDialect() {
    return mDialect;
  }
}

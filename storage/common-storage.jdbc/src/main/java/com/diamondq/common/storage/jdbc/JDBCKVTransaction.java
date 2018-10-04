package com.diamondq.common.storage.jdbc;

import com.diamondq.common.storage.kv.IKVColumnDefinition;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.Query;
import com.diamondq.common.storage.kv.WhereInfo;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextFactory;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Cloudant database is effectively flat. Thus, mapping the concept of a 'table' is done by concatenating the
 * 'table' to the front of any keys.
 */
public class JDBCKVTransaction implements IKVTransaction {

  private static final Logger  sLogger = LoggerFactory.getLogger(JDBCKVTransaction.class);

  private final ContextFactory mContextFactory;

  private final JDBCKVStore    mStore;

  private final DataSource     mDataSource;

  @Nullable
  private Connection           mConnection;

  public JDBCKVTransaction(ContextFactory pContextFactory, JDBCKVStore pStore, DataSource pDataSource) {
    mContextFactory = pContextFactory;
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
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable, pKey1, pKey2, pClass)) {
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        JDBCTableInfo info = mStore.validateTable(c, pTable, pClass);
        try (PreparedStatement ps = c.prepareStatement(info.getBySQL)) {
          ps.setString(1, pKey1);
          ps.setString(2, pKey2);
          sLogger.trace("{} -> {}, {}", info.getBySQL, pKey1, pKey2);
          try (ResultSet rs = ps.executeQuery()) {
            if (rs.next() == false)
              return context.exit(null);
            return context.exit(info.deserializer.deserializeFromResultSet(rs, pClass));
          }
        }
      }
      catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#putByKey(java.lang.String, java.lang.String, java.lang.String,
   *      java.lang.Object)
   */
  @Override
  public <@Nullable O> void putByKey(String pTable, String pKey1, @Nullable String pKey2, O pObj) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable, pKey1, pKey2, pObj)) {
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
            sLogger.trace("{} -> {}, {}", info.putBySQL, pKey1, pKey2);
            ps.execute();
          }
        }
        else {
          boolean exists;
          try (PreparedStatement qps = c.prepareStatement(info.putQueryBySQL)) {
            qps.setString(1, pKey1);
            qps.setString(2, pKey2);
            sLogger.trace("{} -> {}, {}", info.putQueryBySQL, pKey1, pKey2);
            try (ResultSet rs = qps.executeQuery()) {
              exists = rs.next();
            }
          }
          if (exists == false) {
            try (PreparedStatement ps = c.prepareStatement(info.putInsertBySQL)) {
              ps.setString(1, pKey1);
              ps.setString(2, pKey2);
              info.serializer.serializeToPreparedStatement(pObj, ps, 3);
              sLogger.trace("{} -> {}, {}", info.putInsertBySQL, pKey1, pKey2);
              ps.execute();
            }
          }
          else {
            try (PreparedStatement ps = c.prepareStatement(info.putUpdateBySQL)) {
              int offset = info.serializer.serializeToPreparedStatement(pObj, ps, 1);
              ps.setString(offset, pKey1);
              ps.setString(offset + 1, pKey2);
              sLogger.trace("{} -> {}, {}", info.putUpdateBySQL, pKey1, pKey2);
              ps.execute();
            }
          }
        }
      }
      catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#removeByKey(java.lang.String, java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean removeByKey(String pTable, String pKey1, @Nullable String pKey2) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable, pKey1, pKey2)) {
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        JDBCTableInfo info = mStore.validateTable(c, pTable, null);
        try (PreparedStatement ps = c.prepareStatement(info.removeBySQL)) {
          ps.setString(1, pKey1);
          ps.setString(2, pKey2);
          sLogger.trace("{} -> {}, {}", info.removeBySQL, pKey1, pKey2);
          if (ps.executeUpdate() > 0)
            return context.exit(true);
          return context.exit(false);
        }
      }
      catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator(java.lang.String)
   */
  @Override
  public Iterator<@NonNull String> keyIterator(String pTable) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable)) {
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        JDBCTableInfo info = mStore.validateTable(c, pTable, null);
        PreparedStatement ps = c.prepareStatement(info.keyIteratorSQL);
        try {
          sLogger.trace("{}", info.keyIteratorSQL);
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
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator2(java.lang.String, java.lang.String)
   */
  @Override
  public Iterator<@NonNull String> keyIterator2(String pTable, String pKey1) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable, pKey1)) {
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        JDBCTableInfo info = mStore.validateTable(c, pTable, null);
        PreparedStatement ps = c.prepareStatement(info.keyIterator2SQL);
        try {
          ps.setString(1, pKey1);
          sLogger.trace("{} -> {}", info.keyIterator2SQL, pKey1);
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
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#clear(java.lang.String)
   */
  @Override
  public void clear(String pTable) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable)) {
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        JDBCTableInfo info = mStore.validateTable(c, pTable, null);
        try (PreparedStatement ps = c.prepareStatement(info.clearSQL)) {
          sLogger.trace("{}", info.clearSQL);
          ps.executeUpdate();
        }
      }
      catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#getCount(java.lang.String)
   */
  @Override
  public long getCount(String pTable) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pTable)) {
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        JDBCTableInfo info = mStore.validateTable(c, pTable, null);
        try (PreparedStatement ps = c.prepareStatement(info.getCountSQL)) {
          sLogger.trace("{}", info.getCountSQL);
          try (ResultSet rs = ps.executeQuery()) {
            if (rs.next() == false)
              return context.exit(0L);
            return context.exit(rs.getLong(1));
          }
        }
      }
      catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#getTableList()
   */
  @Override
  public Iterator<@NonNull String> getTableList() {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this)) {
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
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#commit()
   */
  @Override
  public void commit() {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this)) {
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
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#rollback()
   */
  @Override
  public void rollback() {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this)) {
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

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#executeQuery(com.diamondq.common.storage.kv.Query,
   *      java.lang.Class, java.util.Map)
   */
  @Override
  public <O> List<O> executeQuery(Query pQuery, Class<O> pClass, Map<String, Object> pParamValues) {
    try (Context context = mContextFactory.newContext(JDBCKVTransaction.class, this, pQuery, pClass)) {
      ImmutableList.Builder<O> builder = ImmutableList.builder();
      try {
        validateConnection();
        Connection c = mConnection;
        if (c == null)
          throw new IllegalStateException();
        String table = pQuery.getDefinitionName();
        JDBCTableInfo info = mStore.validateTable(c, table, pClass);
        String querySQL = mStore.getQuerySQL(info, pQuery);
        try (PreparedStatement ps = c.prepareStatement(querySQL)) {
          sLogger.trace("{}", querySQL);
          List<WhereInfo> whereList = pQuery.getWhereList();
          int paramCount = 0;
          for (WhereInfo where : whereList) {
            paramCount++;
            Object value;
            if (where.constant != null)
              value = where.constant;
            else
              value = pParamValues.get(where.paramKey);
            IKVColumnDefinition colDef = info.definition.getColumnDefinitionsByName(where.key);
            if (colDef == null)
              throw new UnsupportedOperationException();
            info.serializer.serializeColumnToPreparedStatement(value, colDef, ps, paramCount);
            ps.setObject(paramCount, value);
          }
          try (ResultSet rs = ps.executeQuery()) {
            while (rs.next() == true)
              builder.add(info.deserializer.deserializeFromResultSet(rs, pClass));
          }
        }
      }
      catch (SQLException ex) {
        throw new RuntimeException(ex);
      }

      return builder.build();
    }
  }

}

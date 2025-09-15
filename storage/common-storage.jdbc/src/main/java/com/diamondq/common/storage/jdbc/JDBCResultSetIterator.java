package com.diamondq.common.storage.jdbc;

import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JDBCResultSetIterator implements Iterator<@Nullable String> {

  private PreparedStatement mPs;

  @Nullable private ResultSet mRs;

  private boolean moved;

  public JDBCResultSetIterator(PreparedStatement pPs, ResultSet pRs) {
    mPs = pPs;
    mRs = pRs;
    moved = false;
  }

  /**
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    ResultSet rs = mRs;
    if (rs == null) return false;
    if (moved == true) return true;
    try {
      boolean next = rs.next();
      if (next == false) {
        rs.close();
        mRs = null;
        mPs.close();
      }
      moved = true;
      return next;
    }
    catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see java.util.Iterator#next()
   */
  @Override
  public @Nullable String next() {
    ResultSet rs = mRs;
    if (rs == null) throw new NoSuchElementException();
    try {
      if (moved == false) {
        boolean next = rs.next();
        if (next == false) {
          rs.close();
          mRs = null;
          mPs.close();
          throw new NoSuchElementException();
        }
      } else moved = false;
      return rs.getString(1);
    }
    catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

}

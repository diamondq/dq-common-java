package com.diamondq.common.storage.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInfoHelper {

  public static void getReservedWords(Connection pConnection) throws SQLException {
    System.out.println(pConnection.getMetaData().getSQLKeywords());
  }

  public static void dumpTypes(Connection pConnection) throws SQLException {
    try (ResultSet rs = pConnection.getMetaData().getTypeInfo()) {
      System.out.println(
        "TYPE_NAME,DATA_TYPE,PRECISION,LITERAL_PREFIX,LITERAL_SUFFIX,CREATE_PARAMS,NULLABLE,CASE_SENSITIVE,SEARCHABLE,UNSIGNED_ATTRIBUTE,FIXED_PREC_SCALE,AUTO_INCREMENT,LOCAL_TYPE_NAME,MINIMUM_SCALE,MAXIMUM_SCALE,SQL_DATA_TYPE,SQL_DATETIME_SUB,NUM_PREC_RADIX");
      while (rs.next() == true) {
        System.out.print(rs.getString(1));
        System.out.print(",");
        System.out.print(rs.getInt(2));
        System.out.print(",");
        System.out.print(rs.getInt(3));
        System.out.print(",");
        System.out.print(rs.getString(4));
        System.out.print(",");
        System.out.print(rs.getString(5));
        System.out.print(",");
        System.out.print(rs.getString(6));
        System.out.print(",");
        System.out.print(rs.getShort(7));
        System.out.print(",");
        System.out.print(rs.getBoolean(8));
        System.out.print(",");
        System.out.print(rs.getShort(9));
        System.out.print(",");
        System.out.print(rs.getBoolean(10));
        System.out.print(",");
        System.out.print(rs.getBoolean(11));
        System.out.print(",");
        System.out.print(rs.getBoolean(12));
        System.out.print(",");
        System.out.print(rs.getString(13));
        System.out.print(",");
        System.out.print(rs.getShort(14));
        System.out.print(",");
        System.out.print(rs.getShort(15));
        System.out.print(",");
        System.out.print(rs.getInt(16));
        System.out.print(",");
        System.out.print(rs.getInt(17));
        System.out.print(",");
        System.out.print(rs.getInt(18));
        System.out.println();
      }
    }
  }
}


package com.diamondq.common.storage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IResultSetDeserializer {

	public <O> O deserializeFromResultSet(ResultSet pRs, Class<O> pClass) throws SQLException;

}

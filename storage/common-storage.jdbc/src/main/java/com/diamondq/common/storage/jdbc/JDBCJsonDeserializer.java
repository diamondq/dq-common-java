package com.diamondq.common.storage.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCJsonDeserializer implements IResultSetDeserializer {

	private final ObjectMapper mMapper;

	public JDBCJsonDeserializer() {
		mMapper = new ObjectMapper();
	}

	/**
	 * @see com.diamondq.common.storage.jdbc.IResultSetDeserializer#deserializeFromResultSet(java.sql.ResultSet,
	 *      java.lang.Class)
	 */
	@SuppressWarnings("null")
	@Override
	public <O> O deserializeFromResultSet(ResultSet pRs, Class<O> pClass) throws SQLException {
		String data = pRs.getString(1);
		try {
			return mMapper.readValue(data, pClass);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}

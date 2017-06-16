package com.diamondq.common.storage.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCJsonSerializer implements IPreparedStatementSerializer {

	private final ObjectMapper mMapper;

	public JDBCJsonSerializer() {
		mMapper = new ObjectMapper();
	}

	/**
	 * @see com.diamondq.common.storage.jdbc.IPreparedStatementSerializer#serializeToPreparedStatement(java.lang.Object,
	 *      java.sql.PreparedStatement, int)
	 */
	@Override
	public <O> int serializeToPreparedStatement(O pObj, PreparedStatement pPs, int pStartAtIndex) throws SQLException {
		try {
			String str = mMapper.writeValueAsString(pObj);
			pPs.setString(pStartAtIndex, str);
			return pStartAtIndex + 1;
		}
		catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}

}

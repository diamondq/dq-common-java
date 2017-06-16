package com.diamondq.common.storage.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JDBCResultSetIterator implements Iterator<String> {

	private PreparedStatement	mPs;

	private ResultSet			mRs;

	private boolean				moved;

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
		if (mRs == null)
			return false;
		if (moved == true)
			return true;
		try {
			boolean next = mRs.next();
			if (next == false) {
				mRs.close();
				mRs = null;
				mPs.close();
				mPs = null;
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
	public String next() {
		if (mRs == null)
			throw new NoSuchElementException();
		try {
			if (moved == false) {
				boolean next = mRs.next();
				if (next == false) {
					mRs.close();
					mRs = null;
					mPs.close();
					mPs = null;
					throw new NoSuchElementException();
				}
			}
			else
				moved = false;
			return mRs.getString(1);
		}
		catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}

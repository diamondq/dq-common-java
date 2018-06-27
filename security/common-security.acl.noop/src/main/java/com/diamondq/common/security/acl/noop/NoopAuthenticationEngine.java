package com.diamondq.common.security.acl.noop;

import com.diamondq.common.security.acl.api.AuthenticationEngine;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public class NoopAuthenticationEngine implements AuthenticationEngine {

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationEngine#decide(java.lang.Object[])
	 */
	@Override
	public boolean decide(Object @Nullable... pObjects) {
		return true;
	}

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationEngine#bulkDecide(java.util.List, java.lang.Object[])
	 */
	@Override
	public boolean[] bulkDecide(List<?> pAssociations, Object @Nullable... pCommonObjects) {
		int size = pAssociations.size();
		boolean[] results = new boolean[size];
		if (size > 0)
			for (int i = 0; i < size; i++)
				results[i] = true;
		return results;
	}

}

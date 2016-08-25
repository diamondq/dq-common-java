package com.diamondq.common.security.openaz;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepResponse;

import com.diamondq.common.security.acl.api.AuthenticationEngine;

@Singleton
public class AuthEngineImpl implements AuthenticationEngine {

	private final PepAgent mPepAgent;

	@Inject
	public AuthEngineImpl(PepAgent pAgent) {
		mPepAgent = pAgent;
	}

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationEngine#decide(java.lang.Object[])
	 */
	@Override
	public boolean decide(Object... pObjects) {
		PepResponse response = mPepAgent.decide(pObjects);
		return response.allowed() == true;
	}

	@Override
	public boolean[] bulkDecide(List<?> pAssociations, Object... pCommonObjects) {
		List<PepResponse> responses = mPepAgent.bulkDecide(pAssociations, pCommonObjects);
		boolean[] results = new boolean[responses.size()];
		for (int i = 0; i < responses.size(); i++)
			results[i] = responses.get(i).allowed() == true;
		return results;
	}
}

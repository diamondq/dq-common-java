package com.diamondq.common.security.openaz;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acl.api.AuthenticationEngine;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepResponse;

@Singleton
public class AuthEngineImpl implements AuthenticationEngine {

	private final PepAgent	mPepAgent;

	private final Code		mCode;

	@Inject
	public AuthEngineImpl(PepAgent pAgent, Config pConfig) {
		mPepAgent = pAgent;
		String fqdn = pConfig.bind("application.fqdn", String.class);
		if (fqdn == null)
			throw new IllegalArgumentException();
		mCode = new Code(fqdn);
	}

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationEngine#decide(java.lang.Object[])
	 */
	@Override
	public boolean decide(Object... pObjects) {
		Object[] expand = new Object[(pObjects != null ? pObjects.length + 1 : 1)];
		if (pObjects != null)
			System.arraycopy(pObjects, 0, expand, 1, pObjects.length);
		expand[0] = mCode;
		PepResponse response = mPepAgent.decide(expand);
		return response.allowed() == true;
	}

	@Override
	public boolean[] bulkDecide(List<?> pAssociations, Object... pCommonObjects) {
		Object[] expand = new Object[(pCommonObjects != null ? pCommonObjects.length + 1 : 1)];
		if (pCommonObjects != null)
			System.arraycopy(pCommonObjects, 0, expand, 1, pCommonObjects.length);
		expand[0] = mCode;
		List<PepResponse> responses = mPepAgent.bulkDecide(pAssociations, expand);
		boolean[] results = new boolean[responses.size()];
		for (int i = 0; i < responses.size(); i++)
			results[i] = responses.get(i).allowed() == true;
		return results;
	}
}

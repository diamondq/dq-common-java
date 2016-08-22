package com.diamondq.common.security.openaz;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acl.api.AuthenticationEngine;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepAgentFactory;
import org.apache.openaz.pepapi.PepResponse;
import org.apache.openaz.pepapi.std.StdPepAgentFactory;

@Singleton
public class AuthEngineImpl implements AuthenticationEngine {

	private final PepAgent mPepAgent;

	@Inject
	public AuthEngineImpl(Config pConfig) {
		Properties props = pConfig.bind("openaz", Properties.class);
		PepAgentFactory agentFactory = new StdPepAgentFactory(props);
		mPepAgent = agentFactory.getPepAgent();
	}

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationEngine#decide(java.lang.Object[])
	 */
	@Override
	public boolean decide(Object... pObjects) {
		PepResponse response = mPepAgent.decide(pObjects);
		return response.allowed() == true;
	}
}

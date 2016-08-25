package com.diamondq.common.security.openaz;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepAgentFactory;
import org.apache.openaz.pepapi.std.StdPepAgentFactory;

import com.diamondq.common.config.Config;

public class CDIFactory {

	@Produces
	@Singleton
	public PepAgent getPepAgent(Config pConfig) {
		Properties props = pConfig.bind("openaz", Properties.class);
		PepAgentFactory agentFactory = new StdPepAgentFactory(props);
		return agentFactory.getPepAgent();
	}

}

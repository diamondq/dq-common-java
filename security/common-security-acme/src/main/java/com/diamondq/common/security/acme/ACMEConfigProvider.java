package com.diamondq.common.security.acme;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acme.model.ACMEConfig;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

/**
 * Factory provider that produces the ACMEConfig object on demand
 */
public class ACMEConfigProvider {

	@Produces
	@Singleton
	public ACMEConfig getConfig(Config pConfig) {
		return pConfig.bind("acme-ssl", ACMEConfig.class);
	}

}

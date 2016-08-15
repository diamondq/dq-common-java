package com.diamondq.common.security.acme;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acme.model.ACMEConfig;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory provider that produces the ACMEConfig object on demand
 */
public class ACMEConfigProvider {

	@SuppressWarnings("unused")
	private static Logger	sLogger	= LoggerFactory.getLogger(ACMEConfigProvider.class);

	private final Config	configProvider;

	@Inject
	public ACMEConfigProvider(Config pConfigProvider) {
		configProvider = pConfigProvider;
	}

	@Produces
	public ACMEConfig getConfig() {
		return configProvider.bind("dq.acme-ssl", ACMEConfig.class);
	}

}

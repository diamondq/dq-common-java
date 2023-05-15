package com.diamondq.common.security.acme;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acme.model.ACMEConfig;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

/**
 * Factory provider that produces the ACMEConfig object on demand
 */
public class ACMEConfigProvider {

  @Produces
  @Singleton
  public ACMEConfig getConfig(Config pConfig) {
    @Nullable ACMEConfig result = pConfig.bind("acme-ssl", ACMEConfig.class);
    if (result == null) throw new IllegalArgumentException();
    return result;
  }

}

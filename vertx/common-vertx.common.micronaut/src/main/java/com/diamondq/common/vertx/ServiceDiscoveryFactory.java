package com.diamondq.common.vertx;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

@Factory
public class ServiceDiscoveryFactory {

  @Bean(preDestroy = "close")
  public @Singleton ServiceDiscovery getVertx(Vertx pVertx) {
    ServiceDiscoveryOptions options = new ServiceDiscoveryOptions();
    // options.setAnnounceAddress(announceAddress);
    // options.setAutoRegistrationOfImporters(autoRegistrationOfImporters);
    // options.setBackendConfiguration(backendConfiguration);
    // options.setName(name);
    // options.setUsageAddress(usageAddress);
    return ServiceDiscovery.create(pVertx, options);
  }

}

package com.diamondq.common.vertx;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.utils.context.ContextFactory;

import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

public class ServiceDiscoveryProvider extends AbstractOSGiConstructor {

  private Vertx mVertx;

  @SuppressWarnings("null")
  public ServiceDiscoveryProvider() {
    super(ConstructorInfoBuilder.builder().constructorClass(ServiceDiscoveryProvider.class).factoryMethod("onCreate")
      .factoryDelete("onDelete").register(ServiceDiscovery.class) //
    );
  }

  public void setVertx(Vertx pVertx) {
    ContextFactory.staticReportTrace(ServiceDiscoveryProvider.class, this, pVertx);
    mVertx = pVertx;
  }

  public ServiceDiscovery onCreate() {
    ServiceDiscoveryOptions options = new ServiceDiscoveryOptions();
    // options.setAnnounceAddress(announceAddress);
    // options.setAutoRegistrationOfImporters(autoRegistrationOfImporters);
    // options.setBackendConfiguration(backendConfiguration);
    // options.setName(name);
    // options.setUsageAddress(usageAddress);
    return ServiceDiscovery.create(mVertx, options);
  }

  public void onDelete(Vertx pValue) {
    pValue.close();
  }
}

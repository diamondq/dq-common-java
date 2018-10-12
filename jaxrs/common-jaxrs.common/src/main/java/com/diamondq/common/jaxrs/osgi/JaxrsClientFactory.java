package com.diamondq.common.jaxrs.osgi;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.utils.context.Context;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class JaxrsClientFactory extends AbstractOSGiConstructor {

  public JaxrsClientFactory() {
    super(ConstructorInfoBuilder.builder().register(Client.class) //
      .constructorClass(JaxrsClientFactory.class).factoryMethod("onCreate").factoryDelete("onDelete") //
    //
    );
  }

  public Client onCreate() {
    try (Context context = mContextFactory.newContext(JaxrsClientFactory.class, this)) {
      ClientBuilder builder = ClientBuilder.newBuilder();
      Client client = builder.build();
      return context.exit(client);
    }
  }

  public void onDelete(Client pClient) {
    try (Context context = mContextFactory.newContext(JaxrsClientFactory.class, this, pClient)) {
      pClient.close();
    }
  }

}

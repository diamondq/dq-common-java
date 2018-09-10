package com.diamondq.common.jaxrs.osgi;

import com.diamondq.common.utils.context.logging.LoggingUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFactory implements ServiceFactory<Client> {

  private static final Logger sLogger       = LoggerFactory.getLogger(ClientFactory.class);

  protected @Nullable Client  mCachedClient = null;

  protected int               mCount        = 0;

  /**
   * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle,
   *      org.osgi.framework.ServiceRegistration)
   */
  @Override
  public Client getService(Bundle pBundle, ServiceRegistration<Client> pRegistration) {
    synchronized (this) {
      LoggingUtils.entry(sLogger, this);
      try {
        Client client = mCachedClient;
        if (client == null) {
          ClientBuilder builder = ClientBuilder.newBuilder();

          client = builder.build();
          mCachedClient = client;
        }
        mCount++;
        return LoggingUtils.exit(sLogger, this, client);
      }
      catch (RuntimeException ex) {
        LoggingUtils.exitWithException(sLogger, this, ex);
        throw ex;
      }
    }
  }

  /**
   * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle,
   *      org.osgi.framework.ServiceRegistration, java.lang.Object)
   */
  @Override
  public void ungetService(Bundle pBundle, ServiceRegistration<Client> pRegistration, Client pService) {
    synchronized (this) {
      mCount--;
      if (mCount == 0) {
        Client client = mCachedClient;
        mCachedClient = null;
        if (client != null)
          client.close();
      }
    }
  }

}

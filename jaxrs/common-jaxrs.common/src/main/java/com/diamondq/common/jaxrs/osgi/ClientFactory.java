package com.diamondq.common.jaxrs.osgi;

import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class ClientFactory implements ServiceFactory<Client> {

  protected ContextFactory   mContextFactory;

  protected @Nullable Client mCachedClient = null;

  protected int              mCount        = 0;

  @SuppressWarnings("null")
  public ClientFactory() {
    ContextFactory.staticReportTrace(ClientFactory.class, this);
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(ClientFactory.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  /**
   * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle,
   *      org.osgi.framework.ServiceRegistration)
   */
  @Override
  public Client getService(Bundle pBundle, ServiceRegistration<Client> pRegistration) {
    try (Context context = mContextFactory.newContext(ClientFactory.class, this, pBundle, pRegistration)) {
      synchronized (this) {
        Client client = mCachedClient;
        if (client == null) {
          ClientBuilder builder = ClientBuilder.newBuilder();

          client = builder.build();
          mCachedClient = client;
        }
        mCount++;
        return context.exit(client);
      }
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(ClientFactory.class, this, ex);
    }
  }

  /**
   * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle,
   *      org.osgi.framework.ServiceRegistration, java.lang.Object)
   */
  @Override
  public void ungetService(Bundle pBundle, ServiceRegistration<Client> pRegistration, Client pService) {
    try (Context context = mContextFactory.newContext(ClientFactory.class, this, pBundle, pRegistration, pService)) {
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
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(ClientFactory.class, this, ex);
    }
  }

}

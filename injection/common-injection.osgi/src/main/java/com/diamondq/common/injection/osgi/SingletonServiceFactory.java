package com.diamondq.common.injection.osgi;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import org.jspecify.annotations.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Objects;

public abstract class SingletonServiceFactory<S> implements ServiceFactory<S> {

  protected @Nullable ContextFactory mContextFactory;

  protected @Nullable S mCachedService;

  protected int mCount = 0;

  protected abstract S createSingleton(ServiceReference<S> pServiceReference);

  protected abstract void destroySingleton(S pService);

  @SuppressWarnings("null")
  public SingletonServiceFactory() {
    ContextFactory.staticReportTrace(SingletonServiceFactory.class, this);
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(SingletonServiceFactory.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  @SuppressWarnings("null")
  @Override
  public S getService(Bundle pBundle, ServiceRegistration<S> pRegistration) {
    try (Context context = Objects.requireNonNull(mContextFactory)
      .newContext(SingletonServiceFactory.class, this, pBundle, pRegistration)) {
      synchronized (this) {
        S service = mCachedService;
        if (service == null) {
          service = createSingleton(pRegistration.getReference());
          mCachedService = service;
        }
        mCount++;
        return context.exit(service);
      }
    }
    catch (RuntimeException ex) {
      throw Objects.requireNonNull(mContextFactory).reportThrowable(SingletonServiceFactory.class, this, ex);
    }
  }

  @Override
  public void ungetService(Bundle pBundle, ServiceRegistration<S> pRegistration, S pService) {
    try (Context context = Objects.requireNonNull(mContextFactory)
      .newContext(SingletonServiceFactory.class, this, pBundle, pRegistration, pService)) {
      synchronized (this) {
        mCount--;
        if (mCount == 0) {
          S service = mCachedService;
          mCachedService = null;
          if (service != null) destroySingleton(service);
        }
      }
    }
    catch (RuntimeException ex) {
      throw Objects.requireNonNull(mContextFactory).reportThrowable(SingletonServiceFactory.class, this, ex);
    }
  }

}

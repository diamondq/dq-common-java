package com.diamondq.common.injection.osgi;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public abstract class SingletonServiceFactory<S> implements ServiceFactory<S> {

  protected ContextFactory mContextFactory;

  protected @Nullable S    mCachedService;

  protected int            mCount = 0;

  protected abstract @NonNull S createSingleton(ServiceReference<S> pServiceReference);

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
    try (Context context = mContextFactory.newContext(SingletonServiceFactory.class, this, pBundle, pRegistration)) {
      synchronized (this) {
        @Nullable
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
      throw mContextFactory.reportThrowable(SingletonServiceFactory.class, this, ex);
    }
  }

  @Override
  public void ungetService(Bundle pBundle, ServiceRegistration<@NonNull S> pRegistration, @NonNull S pService) {
    try (Context context =
      mContextFactory.newContext(SingletonServiceFactory.class, this, pBundle, pRegistration, pService)) {
      synchronized (this) {
        mCount--;
        if (mCount == 0) {
          @Nullable
          S service = mCachedService;
          mCachedService = null;
          if (service != null)
            destroySingleton(service);
        }
      }
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(SingletonServiceFactory.class, this, ex);
    }
  }

}

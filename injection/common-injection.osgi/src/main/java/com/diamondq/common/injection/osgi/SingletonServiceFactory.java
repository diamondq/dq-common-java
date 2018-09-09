package com.diamondq.common.injection.osgi;

import com.diamondq.common.utils.misc.logging.LoggingUtils;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SingletonServiceFactory<S> implements ServiceFactory<S> {

  private static final Logger sLogger = LoggerFactory.getLogger(SingletonServiceFactory.class);

  protected @Nullable S       mCachedService;

  protected int               mCount  = 0;

  protected abstract @NonNull S createSingleton(ServiceReference<S> pServiceReference);

  protected abstract void destroySingleton(S pService);

  @SuppressWarnings("null")
  @Override
  public S getService(Bundle pBundle, ServiceRegistration<S> pRegistration) {
    synchronized (this) {
      LoggingUtils.entry(sLogger, this);
      try {
        @Nullable
        S service = mCachedService;
        if (service == null) {
          service = createSingleton(pRegistration.getReference());
          mCachedService = service;
        }
        mCount++;
        return LoggingUtils.exit(sLogger, this, service);
      }
      catch (RuntimeException ex) {
        LoggingUtils.exitWithException(sLogger, this, ex);
        throw ex;
      }
    }
  }

  @Override
  public void ungetService(Bundle pBundle, ServiceRegistration<@NonNull S> pRegistration, @NonNull S pService) {
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

}

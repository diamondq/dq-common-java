package com.diamondq.common.vertx;

import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.MiscMessages;
import com.diamondq.common.utils.misc.converters.ConverterManager;
import com.diamondq.common.utils.misc.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.utils.misc.errors.Verify;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;

public abstract class AbstractVerticleOsgi {

  protected ContextFactory                          mContextFactory;

  protected ConverterManager                        mConverterManager;

  protected ServiceDiscovery                        mServiceDiscovery;

  protected Vertx                                   mVertx;

  private int                                       mInstanceCount;

  private String                                    mName;

  private @Nullable String                          mAddress;

  private @Nullable Map<String, String>             mMetaData;

  private final Method                              mSetupMethod;

  private final Method                              mShutdownMethod;

  protected volatile @Nullable Pair<String, String> mRegistration;

  @SuppressWarnings("null")
  public AbstractVerticleOsgi(Class<?> pSetupClass, int pInstanceCount, String pName, @Nullable String pAddress,
    @Nullable Map<String, String> pMetaData) {

    try {
      mSetupMethod = pSetupClass.getDeclaredMethod("setup", ContextFactory.class, ServiceDiscovery.class, Vertx.class,
        Integer.TYPE, Supplier.class, String.class, String.class, Map.class);
      mShutdownMethod = pSetupClass.getDeclaredMethod("shutdown", ContextFactory.class, ServiceDiscovery.class,
        Vertx.class, Pair.class);
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new ExtendedIllegalArgumentException(ex, VertxMessages.ABSTRACTVERTICLEOSGI_NO_MATCHING_METHOD,
        pSetupClass.getName());
    }
    mInstanceCount = pInstanceCount;
    mName = pName;
    mAddress = pAddress;
    mMetaData = pMetaData;
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(AbstractVerticleOsgi.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  public void setConverterManager(ConverterManager pConverterManager) {
    ContextFactory.staticReportTrace(AbstractVerticleOsgi.class, this, pConverterManager);
    mConverterManager = pConverterManager;
  }

  public void setServiceDiscovery(ServiceDiscovery pServiceDiscovery) {
    ContextFactory.staticReportTrace(AbstractVerticleOsgi.class, this, pServiceDiscovery);
    mServiceDiscovery = pServiceDiscovery;
  }

  public void setVertx(Vertx pVertx) {
    ContextFactory.staticReportTrace(AbstractVerticleOsgi.class, this, pVertx);
    mVertx = pVertx;
  }

  protected abstract Supplier<Verticle> getSupplier();

  public void onActivate(ComponentContext pContext) {
    String pid = (String) pContext.getProperties().get(Constants.SERVICE_PID);
    Verify.notNullArg(mContextFactory, MiscMessages.VERIFY_DEPENDENCY_MISSING, "contextFactory", pid);
    try (Context ctx = mContextFactory.newContext(AbstractVerticleOsgi.class, this)) {
      Verify.notNullArg(mConverterManager, MiscMessages.VERIFY_DEPENDENCY_MISSING, "converterManager", pid);
      Verify.notNullArg(mServiceDiscovery, MiscMessages.VERIFY_DEPENDENCY_MISSING, "serviceDiscovery", pid);
      Verify.notNullArg(mVertx, MiscMessages.VERIFY_DEPENDENCY_MISSING, "vertx", pid);
      try {
        Supplier<Verticle> supplier = getSupplier();
        @SuppressWarnings("unchecked")
        ContextExtendedCompletionStage<Pair<String, String>> regFuture =
          (ContextExtendedCompletionStage<Pair<String, String>>) mSetupMethod.invoke(null, mContextFactory,
            mServiceDiscovery, mVertx, mInstanceCount, supplier, mName, mAddress, mMetaData);
        regFuture.thenAccept((reg, ctx2) -> {
          mRegistration = reg;
        }).exceptionally(VertxUtils::reportThrowable);
      }
      catch (RuntimeException | IllegalAccessException | InvocationTargetException ex) {
        throw ctx.reportThrowable(ex);
      }
    }
  }

  public void onDeactivate() {
    try (Context ctx = mContextFactory.newContext(AbstractVerticleOsgi.class, this)) {
      try {
        Pair<String, String> registration = mRegistration;
        if (registration != null) {
          mShutdownMethod.invoke(null, mContextFactory, mServiceDiscovery, mVertx, registration);
        }
      }
      catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        throw ctx.reportThrowable(ex);
      }
    }
  }

}

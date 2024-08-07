package com.diamondq.common.vertx;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.security.acl.api.SecurityContextManager;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractVerticleOsgi {

  protected ContextFactory mContextFactory;

  protected ConverterManager mConverterManager;

  protected SecurityContextManager mSecurityContextManager;

  protected ServiceDiscovery mServiceDiscovery;

  protected Vertx mVertx;

  private int mInstanceCount;

  private String mName;

  private @Nullable String mAddress;

  private @Nullable Map<String, String> mMetaData;

  private final Method mSetupMethod;

  private final Method mShutdownMethod;

  protected volatile @Nullable Pair<String, String> mRegistration;

  @SuppressWarnings("null")
  public AbstractVerticleOsgi(Class<?> pSetupClass, int pInstanceCount, String pName, @Nullable String pAddress,
    @Nullable Map<String, String> pMetaData) {

    try {
      mSetupMethod = pSetupClass.getDeclaredMethod("setup",
        ContextFactory.class,
        ServiceDiscovery.class,
        Vertx.class,
        Integer.TYPE,
        Supplier.class,
        String.class,
        String.class,
        Map.class
      );
      mShutdownMethod = pSetupClass.getDeclaredMethod("shutdown",
        ContextFactory.class,
        ServiceDiscovery.class,
        Vertx.class,
        Pair.class
      );
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new ExtendedIllegalArgumentException(ex,
        VertxMessages.ABSTRACTVERTICLEOSGI_NO_MATCHING_METHOD,
        pSetupClass.getName()
      );
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

  public void setSecurityContextManager(SecurityContextManager pSecurityContextManager) {
    ContextFactory.staticReportTrace(AbstractVerticleOsgi.class, this, pSecurityContextManager);
    mSecurityContextManager = pSecurityContextManager;
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
    Verify.notNullArg(mContextFactory, UtilMessages.VERIFY_DEPENDENCY_MISSING, "contextFactory", pid);
    try (Context ctx = mContextFactory.newContext(AbstractVerticleOsgi.class, this)) {
      Verify.notNullArg(mConverterManager, UtilMessages.VERIFY_DEPENDENCY_MISSING, "converterManager", pid);
      Verify.notNullArg(mSecurityContextManager, UtilMessages.VERIFY_DEPENDENCY_MISSING, "securityContextManager", pid);
      Verify.notNullArg(mServiceDiscovery, UtilMessages.VERIFY_DEPENDENCY_MISSING, "serviceDiscovery", pid);
      Verify.notNullArg(mVertx, UtilMessages.VERIFY_DEPENDENCY_MISSING, "vertx", pid);
      try {
        Supplier<Verticle> supplier = getSupplier();
        @SuppressWarnings(
          "unchecked") ContextExtendedCompletionStage<Pair<String, String>> regFuture = (ContextExtendedCompletionStage<Pair<String, String>>) mSetupMethod.invoke(
          null,
          mContextFactory,
          mServiceDiscovery,
          mVertx,
          mInstanceCount,
          supplier,
          mName,
          mAddress,
          mMetaData
        );
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
          @SuppressWarnings(
            "unchecked") ContextExtendedCompletionStage<@Nullable Void> result = (ContextExtendedCompletionStage<@Nullable Void>) mShutdownMethod.invoke(
            null,
            mContextFactory,
            mServiceDiscovery,
            mVertx,
            registration
          );
          result.join();
        }
      }
      catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        throw ctx.reportThrowable(ex);
      }
    }
  }

}

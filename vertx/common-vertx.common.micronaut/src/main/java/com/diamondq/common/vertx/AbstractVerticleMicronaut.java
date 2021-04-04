package com.diamondq.common.vertx;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.injection.Constants;
import com.diamondq.common.security.acl.api.SecurityContextManager;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;

public abstract class AbstractVerticleMicronaut<INTERFACE> extends AbstractVerticle {

  protected ContextFactory                mContextFactory;

  protected ConverterManager              mConverterManager;

  protected SecurityContextManager        mSecurityContextManager;

  protected ServiceDiscovery              mServiceDiscovery;

  protected Vertx                         mVertx;

  private final int                       mInstanceCount;

  private final String                    mName;

  private final @Nullable String          mAddress;

  private final Class<@NonNull INTERFACE> mInterfaceClass;

  private @Nullable Record                mPublishedRecord;

  private @Nullable ServiceBinder         mServiceBinder;

  private MessageConsumer<JsonObject>     mConsumer;

  @SuppressWarnings("null")
  public AbstractVerticleMicronaut(int pInstanceCount, String pName, @Nullable
  String pAddress, Class<INTERFACE> pInterfaceClass) {
    mInstanceCount = pInstanceCount;
    mName = pName;
    mAddress = pAddress;
    mInterfaceClass = pInterfaceClass;
  }

  @Inject
  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(AbstractVerticleMicronaut.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  @Inject
  public void setConverterManager(ConverterManager pConverterManager) {
    ContextFactory.staticReportTrace(AbstractVerticleMicronaut.class, this, pConverterManager);
    mConverterManager = pConverterManager;
  }

  @Inject
  public void setSecurityContextManager(SecurityContextManager pSecurityContextManager) {
    ContextFactory.staticReportTrace(AbstractVerticleMicronaut.class, this, pSecurityContextManager);
    mSecurityContextManager = pSecurityContextManager;
  }

  @Inject
  public void setServiceDiscovery(ServiceDiscovery pServiceDiscovery) {
    ContextFactory.staticReportTrace(AbstractVerticleMicronaut.class, this, pServiceDiscovery);
    mServiceDiscovery = pServiceDiscovery;
  }

  @Inject
  public void setVertx(Vertx pVertx) {
    ContextFactory.staticReportTrace(AbstractVerticleMicronaut.class, this, pVertx);
    mVertx = pVertx;
  }

  protected void onActivate(Map<String, Object> pProperties, String pPid) {
    String pid = (String) pProperties.getOrDefault(Constants.SERVICE_PID, pPid);
    Verify.notNullArg(mContextFactory, UtilMessages.VERIFY_DEPENDENCY_MISSING, "contextFactory", pid);
    try (Context ctx = mContextFactory.newContext(AbstractVerticleMicronaut.class, this)) {
      Verify.notNullArg(mConverterManager, UtilMessages.VERIFY_DEPENDENCY_MISSING, "converterManager", pid);
      Verify.notNullArg(mSecurityContextManager, UtilMessages.VERIFY_DEPENDENCY_MISSING, "securityContextManager", pid);
      Verify.notNullArg(mServiceDiscovery, UtilMessages.VERIFY_DEPENDENCY_MISSING, "serviceDiscovery", pid);
      Verify.notNullArg(mVertx, UtilMessages.VERIFY_DEPENDENCY_MISSING, "vertx", pid);
      try {

        @SuppressWarnings("null")
        @NonNull
        String address = (mAddress == null ? mName : mAddress);

        /* Start by deploying the instance to Vertx */

        mPublishedRecord = VertxUtils.deployMultiInstance(mVertx, this, mInstanceCount)

          /* The instance is now deployed. Next, register the instance onto the event bus */

          .thenCompose((unreg, ctx2) -> {

            ctx2.trace("After vertx deployment. Binding the service");

            ServiceBinder binder = new ServiceBinder(mVertx);
            mServiceBinder = binder;
            binder.setAddress(address);
            @SuppressWarnings("unchecked")
            INTERFACE itf = (INTERFACE) this;
            mConsumer = binder.register(mInterfaceClass, itf);
            // mVertx.eventBus().<String> consumer(pPid).completionHandler(wrap.getValue1());
            ; // .handler(ScanController.this::scan);

            /* When it's complete registering on the bus, then we need to register with the ServiceDiscovery */

            ctx2.trace("Publishing the record");

            Record record = EventBusService.createRecord(mName, address, mInterfaceClass);
            ContextExtendedCompletionStage<Record> publishedRecord =
              VertxUtils.<Record, Record> call(mServiceDiscovery::publish, record);
            return publishedRecord;
          })

          /* Wait until it's running before continuing */

          .get();
      }
      catch (RuntimeException | InterruptedException | ExecutionException ex) {
        throw ctx.reportThrowable(ex);
      }
    }

  }

  @PreDestroy
  public void onDeactivate() {
    try (Context ctx = mContextFactory.newContext(AbstractVerticleMicronaut.class, this)) {

      try {
        /* Initially, we should unpublish the record */

        if (mPublishedRecord != null) {
          String registrationId = mPublishedRecord.getRegistration();
          if (registrationId != null) {
            VertxUtils.<String, Void> callReturnsNullable(mServiceDiscovery::unpublish, registrationId)

              /* Wait for it to complete */

              .thenApply((v) -> {
                ServiceBinder b = mServiceBinder;
                if (b != null)
                  b.unregister(mConsumer);
                return null;
              })

              .get();
          }
        }
      }
      catch (RuntimeException | InterruptedException | ExecutionException ex) {
        throw ctx.reportThrowable(ex);
      }
    }
  }

}

package com.diamondq.common.vertx;

import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.Verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.spi.ServiceExporter;
import io.vertx.servicediscovery.spi.ServicePublisher;

public class EventBusRecordRegistrator implements ServiceExporter {

  private ContextFactory                                      mContextFactory;

  private Vertx                                               mVertx;

  private ServiceDiscovery                                    mServiceDiscovery;

  private BundleContext                                       mBundleContext;

  private final ConcurrentMap<String, ServiceRegistration<?>> mRegistrations = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, String>                 mById          = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, String>                 mIdByReg       = new ConcurrentHashMap<>();

  @SuppressWarnings("null")
  public EventBusRecordRegistrator() {
  }

  public void setVertx(Vertx pVertx) {
    ContextFactory.staticReportTrace(EventBusRecordRegistrator.class, this, pVertx);
    mVertx = pVertx;
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(EventBusRecordRegistrator.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  public void setServiceDiscovery(ServiceDiscovery pServiceDiscovery) {
    ContextFactory.staticReportTrace(EventBusRecordRegistrator.class, this, pServiceDiscovery);
    mServiceDiscovery = pServiceDiscovery;
  }

  public void onActivate(BundleContext pContext) {
    Verify.notNullArg(mContextFactory, VertxMessages.EVENTBUSRECORDREGISTRATOR_MISSING_DEPENDENCY, "contextFactory");
    try (Context context = mContextFactory.newContext(EventBusRecordRegistrator.class, this)) {
      Verify.notNullArg(mVertx, VertxMessages.EVENTBUSRECORDREGISTRATOR_MISSING_DEPENDENCY, "vertx");
      Verify.notNullArg(mServiceDiscovery, VertxMessages.EVENTBUSRECORDREGISTRATOR_MISSING_DEPENDENCY,
        "serviceDiscovery");
      mBundleContext = pContext;
      mServiceDiscovery.registerServiceExporter(this, new JsonObject());
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(EventBusRecordRegistrator.class, this, ex);
    }
  }

  private void processRecord(Record pRecord) {
    try (Context context = mContextFactory.newContext(EventBusRecordRegistrator.class, this, pRecord)) {
      context.info("{}", pRecord);
      JsonObject location = pRecord.getLocation();
      if (location == null)
        return;
      String address = location.getString(Record.ENDPOINT);
      if (address == null)
        return;

      Status status = pRecord.getStatus();
      String recordName = pRecord.getName();
      String recordType = pRecord.getType();
      String regId = pRecord.getRegistration();
      if (regId == null)
        return;
      StringBuilder sb = new StringBuilder();
      sb.append(address);
      sb.append('/');
      sb.append(recordName);
      sb.append('/');
      sb.append(recordType);
      sb.append('/');
      JsonObject metadata = pRecord.getMetadata();
      List<String> keys = new ArrayList<>(metadata.fieldNames());
      Collections.sort(keys);
      for (String key : keys) {
        Object value = metadata.getValue(key);
        if (value != null) {
          sb.append(key);
          sb.append('=');
          sb.append(value.toString());
          sb.append('/');
        }
      }
      String regKey = sb.toString();
      ServiceRegistration<?> reg = mRegistrations.get(regKey);
      if (reg == null) {
        if (status == Status.UP) {
          EventService service = new EventServiceImpl(address);

          Dictionary<String, Object> props = new Hashtable<>();
          if (recordName != null)
            props.put("name", recordName);
          if (recordType != null)
            props.put("type", recordType);
          for (String key : metadata.fieldNames()) {
            Object value = metadata.getValue(key);
            if (value != null)
              props.put(key, value);
          }
          ServiceRegistration<EventService> newReg = mBundleContext.registerService(EventService.class, service, props);
          if (mRegistrations.putIfAbsent(regKey, newReg) != null)
            newReg.unregister();
          else {
            mById.put(regId, regKey);
            mIdByReg.put(regKey, regId);
          }
        }
      }
      else {
        if (status != Status.UP) {
          mRegistrations.remove(regKey, reg);
          String id = mIdByReg.remove(regKey);
          if (id != null)
            mById.remove(id);
          reg.unregister();
        }
      }
    }

  }

  /**
   * @see io.vertx.servicediscovery.spi.ServiceExporter#init(io.vertx.core.Vertx,
   *      io.vertx.servicediscovery.spi.ServicePublisher, io.vertx.core.json.JsonObject, io.vertx.core.Future)
   */
  @Override
  public void init(Vertx pVertx, ServicePublisher pPublisher, JsonObject pConfiguration, Future<Void> pFuture) {
    try (Context ctx =
      mContextFactory.newContext(EventBusRecordRegistrator.class, this, pVertx, pPublisher, pConfiguration, pFuture)) {
      /* Get all the existing records and process them before marking us as active */

      VertxUtils.<Function<Record, Boolean>, List<Record>> call(mServiceDiscovery::getRecords, (r) -> true)
        .thenAccept((records) -> {

          for (Record record : records)
            processRecord(record);

          pFuture.complete();

        });
    }
  }

  /**
   * @see io.vertx.servicediscovery.spi.ServiceExporter#onPublish(io.vertx.servicediscovery.Record)
   */
  @Override
  public void onPublish(Record pRecord) {
    try (Context context = mContextFactory.newContext(EventBusRecordRegistrator.class, this, pRecord)) {
      processRecord(pRecord);
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(EventBusRecordRegistrator.class, this, ex);
    }
  }

  /**
   * @see io.vertx.servicediscovery.spi.ServiceExporter#onUpdate(io.vertx.servicediscovery.Record)
   */
  @Override
  public void onUpdate(Record pRecord) {
    try (Context context = mContextFactory.newContext(EventBusRecordRegistrator.class, this, pRecord)) {
      processRecord(pRecord);
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(EventBusRecordRegistrator.class, this, ex);
    }
  }

  /**
   * @see io.vertx.servicediscovery.spi.ServiceExporter#onUnpublish(java.lang.String)
   */
  @Override
  public void onUnpublish(String pId) {
    try (Context context = mContextFactory.newContext(EventBusRecordRegistrator.class, this, pId)) {
      String regKey = mById.remove(pId);
      mIdByReg.remove(regKey);
      ServiceRegistration<?> serviceRegistration = mRegistrations.remove(regKey);
      if (serviceRegistration != null)
        serviceRegistration.unregister();
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(EventBusRecordRegistrator.class, this, ex);
    }
  }

  /**
   * @see io.vertx.servicediscovery.spi.ServiceExporter#close(io.vertx.core.Handler)
   */
  @Override
  public void close(@Nullable Handler<@Nullable Void> pCloseHandler) {

    /* Unregister all the registrations */

    Collection<ServiceRegistration<?>> values = new ArrayList<>(mRegistrations.values());
    mRegistrations.clear();
    mIdByReg.clear();
    mById.clear();
    for (ServiceRegistration<?> reg : values)
      reg.unregister();

    if (pCloseHandler != null)
      pCloseHandler.handle(null);
  }

}

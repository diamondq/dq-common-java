package com.diamondq.common.vertx;

import com.diamondq.common.utils.context.ContextFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;

public class ServiceDiscoveryUtils {

  public static void listenFor(Vertx pVertx, ServiceDiscovery pServiceDiscovery, String pName, String pType,
    Consumer<Record> pNewReference, Consumer<Record> pLostReference) {

    JsonObject match = new JsonObject().put("name", pName).put("type", pType);
    CopyOnWriteArraySet<Record> seenRecord = new CopyOnWriteArraySet<>();

    /* Register a consumer for any new record events */

    pVertx.eventBus().localConsumer("vertx.discovery.announce", (message) -> {
      JsonObject body = (JsonObject) message.body();
      Record record = new Record(body);

      if (record.match(match) == true) {
        Status status = record.getStatus();
        if (status == Status.UP) {

          /* Inform about a new instance */

          if (seenRecord.add(record) == true)
            pNewReference.accept(record);
        }
        else {

          /* Inform about loss of instance */

          if (seenRecord.remove(record) == true)
            pLostReference.accept(record);

        }
      }
    });

    /* Issue a query for all the existing records */

    VertxUtils.<JsonObject, List<Record>> call(pServiceDiscovery::getRecords, match).thenAccept((records) -> {
      for (Record record : records) {
        Status status = record.getStatus();
        if (status == Status.UP) {

          /* Inform about a new instance */

          if (seenRecord.add(record) == true)
            pNewReference.accept(record);
        }
        else {

          /* Inform about loss of instance */

          if (seenRecord.remove(record) == true)
            pLostReference.accept(record);
        }
      }
    }).exceptionally((ex) -> {
      ContextFactory.staticReportThrowable(ServiceDiscoveryUtils.class, ServiceDiscoveryUtils.class, ex);
      return null;
    });
  }
}

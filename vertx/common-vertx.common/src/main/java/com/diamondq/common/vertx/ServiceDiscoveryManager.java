package com.diamondq.common.vertx;

import com.diamondq.common.context.ContextExtendedCompletionStage;

import io.vertx.core.Vertx;

public interface ServiceDiscoveryManager {

  public <T> ContextExtendedCompletionStage<T> lookupService(Vertx pVertx, Class<T> pClass, String pName);

}

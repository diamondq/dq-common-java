package com.diamondq.common.vertx.impl;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.utils.context.ContextExtendedCompletableFuture;
import com.diamondq.common.vertx.MessageCodecWrapper;
import com.diamondq.common.vertx.VertxContextExtendedCompletableFuture;
import com.diamondq.common.vertx.VertxUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

@Factory
public class VertxFactory {

  public @Singleton Vertx getVertx(@Value("${vertx.blockedthreadcheckinternal:}") String pBlockedThreadCheckInterval,
    @Value("${vertx.blockedthreadcheckinternalunit:}") String pBlockedThreadCheckIntervalUnit,
    List<MessageCodecWrapper<Object, Object>> pCodecs) {
    System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());

    /* Assign the Vertx future as the primary future */

    try {
      Method newCompletableFuture =
        VertxContextExtendedCompletableFuture.class.getDeclaredMethod("newCompletableFuture");
      Method completedFuture =
        VertxContextExtendedCompletableFuture.class.getDeclaredMethod("completedFuture", Object.class);
      Method completedFailure =
        VertxContextExtendedCompletableFuture.class.getDeclaredMethod("completedFailure", Throwable.class);
      Method listOf = VertxContextExtendedCompletableFuture.class.getDeclaredMethod("listOf", List.class);
      Set<Class<?>> replacements = new HashSet<>();
      replacements.add(ContextExtendedCompletableFuture.class);
      replacements.add(ExtendedCompletableFuture.class);

      FutureUtils.setMethods(newCompletableFuture, completedFuture, completedFailure, listOf,
        VertxContextExtendedCompletableFuture.class, replacements);
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
    VertxOptions options = new VertxOptions();
    // options.setAddressResolverOptions(addressResolverOptions);
    if (pBlockedThreadCheckInterval.isEmpty() == false) {
      long blockedThreadCheckIntervalTime = Long.parseLong(pBlockedThreadCheckInterval);
      TimeUnit blockedThreadCheckIntervalUnit = pBlockedThreadCheckIntervalUnit.isEmpty() == true
        ? TimeUnit.MILLISECONDS : TimeUnit.valueOf(pBlockedThreadCheckIntervalUnit);
      options.setBlockedThreadCheckIntervalUnit(blockedThreadCheckIntervalUnit);
      options.setBlockedThreadCheckInterval(blockedThreadCheckIntervalTime);
    }
    // options.setClustered(clustered);
    // options.setClusterHost(clusterHost);
    // options.setClusterManager(clusterManager);
    // options.setClusterPingInterval(clusterPingInterval);
    // options.setClusterPingReplyInterval(clusterPingReplyInterval);
    // options.setClusterPort(clusterPort);
    // options.setClusterPublicHost(clusterPublicHost);
    // options.setClusterPublicPort(clusterPublicPort);

    EventBusOptions eventBusOptions = new EventBusOptions();
    // eventBusOptions.setAcceptBacklog(acceptBacklog);
    // eventBusOptions.setClientAuth(clientAuth);
    // eventBusOptions.setClustered(clustered);
    // eventBusOptions.setClusterPingInterval(clusterPingInterval);
    // eventBusOptions.setClusterPingReplyInterval(clusterPingReplyInterval);
    // eventBusOptions.setClusterPublicHost(clusterPublicHost);
    // eventBusOptions.setClusterPublicPort(clusterPublicPort);
    // eventBusOptions.setConnectTimeout(connectTimeout);
    // eventBusOptions.setEnabledSecureTransportProtocols(enabledSecureTransportProtocols);
    // eventBusOptions.setHost(host);
    // eventBusOptions.setIdleTimeout(idleTimeout);
    // eventBusOptions.setJdkSslEngineOptions(sslEngineOptions);
    // eventBusOptions.setKeyCertOptions(options);
    // eventBusOptions.setKeyStoreOptions(options);
    // eventBusOptions.setLogActivity(logEnabled);
    // eventBusOptions.setOpenSslEngineOptions(sslEngineOptions);
    // eventBusOptions.setPemKeyCertOptions(options);
    // eventBusOptions.setPemTrustOptions(options);
    // eventBusOptions.setPfxKeyCertOptions(options);
    // eventBusOptions.setPfxTrustOptions(options);
    // eventBusOptions.setPort(port);
    // eventBusOptions.setReceiveBufferSize(receiveBufferSize);
    // eventBusOptions.setReconnectAttempts(attempts);
    // eventBusOptions.setReconnectInterval(interval);
    // eventBusOptions.setReuseAddress(reuseAddress);
    // eventBusOptions.setReusePort(reusePort);
    // eventBusOptions.setSendBufferSize(sendBufferSize);
    // eventBusOptions.setSoLinger(soLinger);
    // eventBusOptions.setSsl(ssl);
    // eventBusOptions.setSslEngineOptions(sslEngineOptions);
    // eventBusOptions.setTcpCork(tcpCork);
    // eventBusOptions.setTcpFastOpen(tcpFastOpen);
    // eventBusOptions.setTcpKeepAlive(tcpKeepAlive);
    // eventBusOptions.setTcpNoDelay(tcpNoDelay);
    // eventBusOptions.setTcpQuickAck(tcpQuickAck);
    // eventBusOptions.setTrafficClass(trafficClass);
    // eventBusOptions.setTrustAll(trustAll);
    // eventBusOptions.setTrustOptions(options);
    // eventBusOptions.setTrustStoreOptions(options);
    // eventBusOptions.setUseAlpn(useAlpn);
    // eventBusOptions.setUsePooledBuffers(usePooledBuffers);
    // eventBusOptions.addCrlPath(crlPath);
    // eventBusOptions.addCrlValue(crlValue);
    // eventBusOptions.addEnabledCipherSuite(suite);
    // eventBusOptions.addEnabledSecureTransportProtocol(protocol);
    options.setEventBusOptions(eventBusOptions);
    // options.setEventLoopPoolSize(eventLoopPoolSize);
    // options.setFileResolverCachingEnabled(fileResolverCachingEnabled);
    // options.setHAEnabled(haEnabled);
    // options.setInternalBlockingPoolSize(internalBlockingPoolSize);
    // options.setMaxEventLoopExecuteTime(maxEventLoopExecuteTime);
    // options.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
    // options.setMetricsOptions(metrics);
    // options.setPreferNativeTransport(preferNativeTransport);
    // options.setQuorumSize(quorumSize);
    // options.setWarningExceptionTime(warningExceptionTime);
    // options.setWorkerPoolSize(workerPoolSize);

    Vertx result = Vertx.vertx(options);
    if (pCodecs.isEmpty() == false) {
      EventBus eventBus = result.eventBus();
      for (MessageCodecWrapper<Object, Object> w : pCodecs) {
        eventBus.registerDefaultCodec(w.getCodecClass(), w.getMessageCodec());
      }
    }
    VertxUtils.setDefaultVertx(result);
    return result;
  }
}

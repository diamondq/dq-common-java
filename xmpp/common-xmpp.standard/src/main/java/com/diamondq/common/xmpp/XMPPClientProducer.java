package com.diamondq.common.xmpp;

import com.diamondq.common.config.Config;
import com.diamondq.common.xmpp.cdi.XMPPClientRef;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.client.ClientConnectionConfiguration;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.session.ReconnectionStrategy;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.XmppDebugger;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

@ApplicationScoped
public class XMPPClientProducer {

  private static final Logger sLogger = LoggerFactory.getLogger(XMPPClientProducer.class);

  private final Cache<String, XmppClient> mCache;

  @Inject
  public XMPPClientProducer() {
    mCache = CacheBuilder.newBuilder().weakValues().build();
  }

  @Produces
  @Dependent
  public @Nullable
  @Default
  @XMPPClientRef("") XmppClient getClient(InjectionPoint pInjectionPoint, Config pConfig,
    XMPPServerLookup pServerInfoLookup) {

    String scope = pInjectionPoint.getBean().getScope().getName();

    /* Get information about the injection point */

    Annotated annotatedLocation = pInjectionPoint.getAnnotated();

    /* See if it has an XMPPClientRef annotation */

    XMPPClientRef clientAnnotation = annotatedLocation.getAnnotation(XMPPClientRef.class);

    String clientKey;

    /* If it does not, then use the 'default' client */
    if (clientAnnotation == null) clientKey = scope + "/__DEFAULT__";
    else clientKey = scope + "/" + clientAnnotation.value();

    XmppClient client = mCache.getIfPresent(clientKey);
    if (client != null) return client;

    /* So, create a new client */

    String domain = null;
    if (clientAnnotation != null) {
      domain = clientAnnotation.domainConfigKey();
    }
    if ((domain == null) || (domain.isEmpty() == true)) {

      /* Read the domain from the config */

      domain = pConfig.bind("xmpp.domain", String.class);
      if (domain == null)
        throw new IllegalStateException("The domain is required, possibly by the config key xmpp.domain");
    }

    SortedSet<XMPPServerInfo> pServerInfos = pServerInfoLookup.getXMPPServerList(domain);

    /* At this point, we should iterate through the list until we find a server that we can connect to */

    for (XMPPServerInfo serverInfo : pServerInfos) {

      List<ClientConnectionConfiguration> configurationList = Lists.newArrayList();

      /* Handle the TCP Configuration */

      SocketConnectionConfiguration.Builder socketBuilder = SocketConnectionConfiguration.builder();

      socketBuilder = socketBuilder.hostname(serverInfo.hostname);

      socketBuilder = socketBuilder.port(serverInfo.port);

      Integer connectTimeout = pConfig.bind("xmpp.tcp.connect-timeout", Integer.class);
      if (connectTimeout != null) socketBuilder = socketBuilder.connectTimeout(connectTimeout);

      Integer keepAliveInterval = pConfig.bind("xmpp.tcp.keep-alive-interval", Integer.class);
      if (keepAliveInterval != null) socketBuilder.keepAliveInterval(keepAliveInterval);

      Boolean secure = pConfig.bind("xmpp.tcp.secure", Boolean.class);
      if (secure != null) socketBuilder = socketBuilder.channelEncryption(ChannelEncryption.REQUIRED);

      String proxyHost = pConfig.bind("xmpp.tcp.proxy.hostname", String.class);
      if (proxyHost != null) {
        Integer proxyPort = pConfig.bind("xmpp.tcp.proxy.port", Integer.class);
        if (proxyPort == null) throw new IllegalArgumentException(
          "The xmpp.tcp.proxy.port is required if xmpp.tcp.proxy.hostname is provided.");

        String proxyTypeStr = pConfig.bind("xmpp.tcp.proxy.type", String.class);
        if (proxyTypeStr == null) proxyTypeStr = Proxy.Type.SOCKS.toString();
        Proxy.Type proxyType = Proxy.Type.valueOf(proxyTypeStr);

        InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(proxyType, address);
        socketBuilder = socketBuilder.proxy(proxy);
      }

      SocketConnectionConfiguration socketConfiguration = socketBuilder.build();
      configurationList.add(socketConfiguration);

      /* Now set up the Session */

      XmppSessionConfiguration.Builder builder = XmppSessionConfiguration.builder();

      String debuggerName = pConfig.bind("xmpp.session.debugger", String.class);
      if (debuggerName != null) {
        try {
          @SuppressWarnings(
            "unchecked") Class<? extends XmppDebugger> debuggerClass = (Class<? extends XmppDebugger>) Class.forName(
            debuggerName);
          builder = builder.debugger(debuggerClass);
        }
        catch (ClassNotFoundException ex) {
        }
      }

      Integer defaultResponseTimeout = pConfig.bind("xmpp.session.default-response-timeout", Integer.class);
      if (defaultResponseTimeout != null)
        builder = builder.defaultResponseTimeout(Duration.ofMillis(defaultResponseTimeout));

      String localeStr = pConfig.bind("xmpp.session.locale", String.class);
      if (localeStr != null) {
        Locale locale = Locale.forLanguageTag(localeStr);
        builder = builder.language(locale);
      }

      String reconnectType = pConfig.bind("xmpp.session.reconnection-strategy.type", String.class);
      if (reconnectType != null) {
        ReconnectionStrategy reconnectionStrategy;
        if ("backoff".equals(reconnectType)) {
          Integer slotTime = pConfig.bind("xmpp.session.reconnection-strategy.slot-time", Integer.class);
          Integer ceilingTime = pConfig.bind("xmpp.session.reconnection-strategy.ceiling", Integer.class);
          if (slotTime == null) throw new IllegalArgumentException(
            "The xmpp.session.reconnection-strategy.slot-time is required for a backoff type.");
          if (ceilingTime == null) throw new IllegalArgumentException(
            "The xmpp.session.reconnection-strategy.ceiling is required for a backoff type.");
          reconnectionStrategy = ReconnectionStrategy.truncatedBinaryExponentialBackoffStrategy(slotTime, ceilingTime);
        } else if ("after".equals(reconnectType)) {
          Integer afterTime = pConfig.bind("xmpp.session.reconnection-strategy.after-time", Integer.class);
          if (afterTime == null) throw new IllegalArgumentException(
            "The xmpp.session.reconnection-strategy.after-time is required for a after type.");
          reconnectionStrategy = ReconnectionStrategy.alwaysAfter(Duration.ofMillis(afterTime));
        } else if ("random".equals(reconnectType)) {
          Integer minTime = pConfig.bind("xmpp.session.reconnection-strategy.min-time", Integer.class);
          if (minTime == null) throw new IllegalArgumentException(
            "The xmpp.session.reconnection-strategy.min-time is required for a random type.");
          Integer maxTime = pConfig.bind("xmpp.session.reconnection-strategy.max-time", Integer.class);
          if (maxTime == null) throw new IllegalArgumentException(
            "The xmpp.session.reconnection-strategy.max-time is required for a random type.");
          reconnectionStrategy = ReconnectionStrategy.alwaysRandomlyAfter(Duration.ofMillis(minTime),
            Duration.ofMillis(maxTime)
          );
        } else throw new IllegalArgumentException("Unrecognized reconnection-strategy type: " + reconnectType);

        builder = builder.reconnectionStrategy(reconnectionStrategy);
      }

      XmppSessionConfiguration sessionConfiguration = builder.build();

      ClientConnectionConfiguration[] configurations = configurationList.toArray(new ClientConnectionConfiguration[0]);

      /* Connect to the domain */

      XmppClient xmppClient = XmppClient.create(serverInfo.domain, sessionConfiguration, configurations);
      try {
        xmppClient.connect();

        /* Validate the connection */

        /* Associate the XMPPServerInfo for future lookup */

        XMPPServerInfoManager manager = xmppClient.getManager(XMPPServerInfoManager.class);
        manager.setServerInfo(serverInfo);

        mCache.put(clientKey, xmppClient);
        return xmppClient;
      }
      catch (XmppException ex) {
        sLogger.warn("Unable to connect to XMPP Server at {}:{}", serverInfo.hostname, serverInfo.port);
      }

    }

    return null;
  }
}

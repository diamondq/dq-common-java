package com.diamondq.common.xmpp.osgi;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import org.jspecify.annotations.Nullable;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.net.ChannelEncryption;
import rocks.xmpp.core.net.ConnectionConfiguration;
import rocks.xmpp.core.net.client.ClientConnectionConfiguration;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration;
import rocks.xmpp.core.net.client.SocketConnectionConfiguration.Builder;
import rocks.xmpp.core.session.Module;
import rocks.xmpp.core.session.ReconnectionStrategy;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.XmppDebugger;
import rocks.xmpp.core.stanza.PresenceEvent;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This represents a connection to an XMPP server for a given user
 */
public class XMPPConnectionImpl extends AbstractOSGiConstructor {

  private static final Logger sLogger = LoggerFactory.getLogger(XMPPConnectionImpl.class);

  private ScheduledExecutorService mScheduledExecutorService;

  @SuppressWarnings("null")
  public XMPPConnectionImpl() {
    super(ConstructorInfoBuilder.builder()
      .constructorClass(XMPPConnectionImpl.class)
      .factoryMethod("create")
      .factoryDelete("onDelete")
      .register(XmppClient.class) //
      .cArg()
      .type(String.class)
      .prop("domain")
      .required()
      .build() //
      .cArg()
      .type(String.class)
      .prop("host")
      .required()
      .build() //
      .cArg()
      .type(Integer.TYPE)
      .prop("port")
      .value(5222)
      .required()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".connectTimeout")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".keepAliveInterval")
      .optional()
      .build() //
      .cArg()
      .type(Boolean.class)
      .prop("secure")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".proxyHost")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".proxyPort")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".proxyType")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".debugger")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".defaultResponseTimeout")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".locale")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".reconnectType")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".slotTime")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".ceilingTime")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".afterTime")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".minTime")
      .optional()
      .build() //
      .cArg()
      .type(Integer.class)
      .prop(".maxTime")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop("username")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".password")
      .optional()
      .build() //
      .cArg()
      .type(String.class)
      .prop(".resource")
      .optional()
      .build() //
      .cArg()
      .type(Consumer.class)
      .propFilter(".inboundPresenceListeners")
      .collection()
      .optional()
      .build() //
    );
  }

  public void setScheduledExecutorService(ScheduledExecutorService pScheduledExecutorService) {
    mScheduledExecutorService = pScheduledExecutorService;
  }

  public void onDelete(XmppClient pClient) {
    sLogger.trace("onDelete({}) from {}", pClient, this);
    try {
      pClient.close();
    }
    catch (XmppException ex) {
      throw new RuntimeException(ex);
    }
  }

  public @Nullable XmppClient create(String pDomain, String pHostname, int pPort, @Nullable Integer pConnectTimeout,
    @Nullable Integer pKeepAliveInterval, @Nullable Boolean pSecure, @Nullable String pProxyHost,
    @Nullable Integer pProxyPort, @Nullable String pProxyType, @Nullable String pDebuggerName,
    @Nullable Integer pDefaultResponseTimeout, @Nullable String pLocaleStr, @Nullable String pReconnectType,
    @Nullable Integer pSlotTime, @Nullable Integer pCeilingTime, @Nullable Integer pAfterTime,
    @Nullable Integer pMinTime, @Nullable Integer pMaxTime, @Nullable String pUserName, @Nullable String pPassword,
    @Nullable String pResource, @Nullable Collection<Consumer<PresenceEvent>> pInboundPresenceListeners) {
    sLogger.trace("create({}, {}, ...) from {}", pDomain, pHostname, this);

    /* Check to see if all the modules are available */

    try {
      int count = 0;
      String[] requiredModules = new String[] { "com.diamondq.common.xmpp.Module", "rocks.xmpp.core.session.CoreModule", "rocks.xmpp.core.session.context.extensions.ExtensionModule" };
      for (ServiceReference<Module> sr : mComponentContext.getBundleContext()
        .getServiceReferences(Module.class, null)) {
        Module module = mComponentContext.getBundleContext().getService(sr);
        if (module == null) throw new UnsupportedOperationException();
        String name = module.getClass().getName();
        for (String test : requiredModules) {
          if (test.equals(name)) count++;
        }
      }
      if (count < requiredModules.length) {
        mScheduledExecutorService.schedule(() -> {
            XmppClient client = create(pDomain,
              pHostname,
              pPort,
              pConnectTimeout,
              pKeepAliveInterval,
              pSecure,
              pProxyHost,
              pProxyPort,
              pProxyType,
              pDebuggerName,
              pDefaultResponseTimeout,
              pLocaleStr,
              pReconnectType,
              pSlotTime,
              pCeilingTime,
              pAfterTime,
              pMinTime,
              pMaxTime,
              pUserName,
              pPassword,
              pResource,
              pInboundPresenceListeners
            );
            if (client != null) {
              registerService(client);
            }
          }, 5, TimeUnit.SECONDS
        );
        return null;
      }
    }
    catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }

    List<ConnectionConfiguration> configurationList = new ArrayList<>();

    /* Handle the TCP Configuration */

    Builder socketBuilder = SocketConnectionConfiguration.builder();

    socketBuilder = socketBuilder.hostname(pHostname);

    socketBuilder = socketBuilder.port(pPort);

    if (pConnectTimeout != null) socketBuilder = socketBuilder.connectTimeout(pConnectTimeout);

    if (pKeepAliveInterval != null) socketBuilder.keepAliveInterval(pKeepAliveInterval);

    if (pSecure != null) socketBuilder = socketBuilder.channelEncryption(ChannelEncryption.REQUIRED);

    if (pProxyHost != null) {
      if (pProxyPort == null) throw new IllegalArgumentException("The proxyPort is required if proxyHost is provided.");

      String proxyTypeStr = pProxyType;
      if (proxyTypeStr == null) proxyTypeStr = Proxy.Type.SOCKS.toString();
      Proxy.Type proxyType = Proxy.Type.valueOf(proxyTypeStr);

      InetSocketAddress address = new InetSocketAddress(pProxyHost, pProxyPort);
      Proxy proxy = new Proxy(proxyType, address);
      socketBuilder = socketBuilder.proxy(proxy);
    }

    SocketConnectionConfiguration socketConfiguration = socketBuilder.build();
    configurationList.add(socketConfiguration);

    /* Now set up the Session */

    XmppSessionConfiguration.Builder builder = XmppSessionConfiguration.builder();

    if (pDebuggerName != null) {
      try {
        @SuppressWarnings(
          "unchecked") Class<? extends XmppDebugger> debuggerClass = (Class<? extends XmppDebugger>) Class.forName(
          pDebuggerName);
        builder = builder.debugger(debuggerClass);
      }
      catch (ClassNotFoundException ex) {
        sLogger.warn("Unable to find debugger class {}", pDebuggerName);
      }
    }

    if (pDefaultResponseTimeout != null)
      builder = builder.defaultResponseTimeout(Duration.ofMillis(pDefaultResponseTimeout));

    if (pLocaleStr != null) {
      Locale locale = Locale.forLanguageTag(pLocaleStr);
      builder = builder.language(locale);
    }

    if (pReconnectType != null) {
      ReconnectionStrategy reconnectionStrategy;
      if ("backoff".equals(pReconnectType)) {
        if (pSlotTime == null) throw new IllegalArgumentException(
          "The xmpp.session.reconnection-strategy.slot-time is required for a backoff type.");
        if (pCeilingTime == null) throw new IllegalArgumentException(
          "The xmpp.session.reconnection-strategy.ceiling is required for a backoff type.");
        reconnectionStrategy = ReconnectionStrategy.truncatedBinaryExponentialBackoffStrategy(pSlotTime, pCeilingTime);
      } else if ("after".equals(pReconnectType)) {
        if (pAfterTime == null) throw new IllegalArgumentException(
          "The xmpp.session.reconnection-strategy.after-time is required for a after type.");
        reconnectionStrategy = ReconnectionStrategy.alwaysAfter(Duration.ofMillis(pAfterTime));
      } else if ("random".equals(pReconnectType)) {
        if (pMinTime == null) throw new IllegalArgumentException(
          "The xmpp.session.reconnection-strategy.min-time is required for a random type.");
        if (pMaxTime == null) throw new IllegalArgumentException(
          "The xmpp.session.reconnection-strategy.max-time is required for a random type.");
        reconnectionStrategy = ReconnectionStrategy.alwaysRandomlyAfter(Duration.ofMillis(pMinTime),
          Duration.ofMillis(pMaxTime)
        );
      } else throw new IllegalArgumentException("Unrecognized reconnection-strategy type: " + pReconnectType);

      builder = builder.reconnectionStrategy(reconnectionStrategy);
    }

    XmppSessionConfiguration sessionConfiguration = builder.build();

    ClientConnectionConfiguration[] configurations = configurationList.toArray(new ClientConnectionConfiguration[0]);

    /* Connect to the domain */

    XmppClient client = XmppClient.create(pDomain, sessionConfiguration, configurations);
    try {
      sLogger.debug("connecting...");

      /* Register the existing listeners before we connect */

      if (pInboundPresenceListeners != null) for (Consumer<PresenceEvent> listener : pInboundPresenceListeners)
        client.addInboundPresenceListener(listener);

      client.connect();

      sLogger.debug("connected");

      if (pUserName != null) {
        if (pResource != null) client.login(pUserName, pPassword, pResource);
        else client.login(pUserName, pPassword);
      }
    }
    catch (XmppException ex) {
      throw new RuntimeException(ex);
    }

    return client;
  }

}

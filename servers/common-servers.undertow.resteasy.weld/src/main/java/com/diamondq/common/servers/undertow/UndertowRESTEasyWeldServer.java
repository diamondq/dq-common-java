package com.diamondq.common.servers.undertow;

import com.diamondq.common.config.Config;

import javax.ws.rs.core.Application;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;

public class UndertowRESTEasyWeldServer extends UndertowServer {

  @Nullable
  private UndertowJaxrsServer mServer;

  public UndertowRESTEasyWeldServer(Config pConfig, Class<? extends Application> pAppClass) {
    super(pConfig);

    /* Now start the mServer */

    String appName = pConfig.bind("application.name", String.class);
    String deployContext = pConfig.bind("application.context", String.class);
    if ((deployContext == null) || (deployContext.trim().isEmpty() == true))
      deployContext = "/";

    ClassLoader classLoader = UndertowRESTEasyWeldServer.class.getClassLoader();
    DeploymentInfo di = deployApplication("/", pAppClass).setClassLoader(classLoader).setContextPath(deployContext)
      .setDeploymentName(appName).setResourceManager(new ClassPathResourceManager(classLoader, "META-INF/resources/"))
      .addListeners(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

    deploy(di);
  }

  /**
   * @see com.diamondq.common.servers.undertow.UndertowServer#startServer(io.undertow.Undertow.Builder)
   */
  @Override
  protected void startServer(Undertow.Builder pBuilder) {
    mServer = new UndertowJaxrsServer();
    mServer.start(pBuilder);
  }

  protected DeploymentInfo deployApplication(String appPath, Class<? extends Application> applicationClass) {
    ResteasyDeployment deployment = new ResteasyDeploymentImpl();
    deployment.setInjectorFactoryClass("org.jboss.resteasy.cdi.CdiInjectorFactory");
    deployment.setApplicationClass(applicationClass.getName());
    UndertowJaxrsServer server = mServer;
    if (server == null)
      throw new IllegalArgumentException();
    return server.undertowDeployment(deployment, appPath);
  }

  protected void deploy(DeploymentInfo deploymentInfo) {
    UndertowJaxrsServer server = mServer;
    if (server == null)
      throw new IllegalArgumentException();
    server.deploy(deploymentInfo);
  }

}

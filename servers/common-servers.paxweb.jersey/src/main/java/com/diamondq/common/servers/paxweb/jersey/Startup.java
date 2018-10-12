package com.diamondq.common.servers.paxweb.jersey;

import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.Verify;

import java.util.Properties;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class Startup {

  private ContextFactory                   mContextFactory;

  private HttpService                      mHttpService;

  private CopyOnWriteArraySet<Application> mApplications;

  @SuppressWarnings("null")
  public Startup() {
    mApplications = new CopyOnWriteArraySet<>();
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(Startup.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  public void setHttpService(HttpService pHttpService) {
    ContextFactory.staticReportTrace(Startup.class, this, pHttpService);
    mHttpService = pHttpService;
  }

  public void addApplication(Application pApplication) {
    ContextFactory.staticReportTrace(Startup.class, this, pApplication);
    mApplications.add(pApplication);
  }

  public void onActivate() {
    Verify.notNullArg(mContextFactory, Messages.STARTUP_MISSING_DEPENDENCY, "contextFactory");
    try (Context ctx = mContextFactory.newContext(Startup.class, this)) {
      Verify.notNullArg(mHttpService, Messages.STARTUP_MISSING_DEPENDENCY, "httpService");

      try {
        JerseyServlet servlet = new JerseyServlet();
        Properties initParams = new Properties();
        HttpContext context = mHttpService.createDefaultHttpContext();
        mHttpService.registerServlet("/jaxrs", servlet, initParams, context);
        // ServletContext servletContext = servlet.getServletConfig().getServletContext();
        // JerseyServletContainerInitializer jsci = new JerseyServletContainerInitializer();
        // jsci.onStartup(classes, servletContext);
      }
      catch (ServletException | NamespaceException ex) {
        throw new RuntimeException(ex);
      }
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(Startup.class, this, ex);
    }
  }
}

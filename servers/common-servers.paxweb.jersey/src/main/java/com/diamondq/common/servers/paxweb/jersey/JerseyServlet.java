package com.diamondq.common.servers.paxweb.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.glassfish.jersey.servlet.internal.ServletContainerProviderFactory;
import org.glassfish.jersey.servlet.internal.Utils;
import org.glassfish.jersey.servlet.internal.spi.ServletContainerProvider;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Registration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JerseyServlet implements Servlet {

  private static final Logger sLogger = LoggerFactory.getLogger(JerseyServlet.class);

  private ServletConfig mServletConfig;

  @SuppressWarnings("null")
  public JerseyServlet() {
  }

  @Override
  public void init(ServletConfig pConfig) throws ServletException {
    mServletConfig = pConfig;
    onStartup(new HashSet<>(), pConfig.getServletContext());
  }

  @Override
  public ServletConfig getServletConfig() {
    return mServletConfig;
  }

  @Override
  public void service(ServletRequest pReq, ServletResponse pRes) throws ServletException, IOException {

  }

  @Override
  public String getServletInfo() {
    return "";
  }

  @Override
  public void destroy() {

  }

  public void onStartup(Set<Class<?>> classes, final ServletContext servletContext) throws ServletException {
    final ServletContainerProvider[] allServletContainerProviders = ServletContainerProviderFactory.getAllServletContainerProviders();

    // PRE INIT
    for (final ServletContainerProvider servletContainerProvider : allServletContainerProviders) {
      servletContainerProvider.preInit(servletContext, classes);
    }
    // INIT IMPL
    onStartupImpl(classes, servletContext);
    // POST INIT
    for (final ServletContainerProvider servletContainerProvider : allServletContainerProviders) {
      servletContainerProvider.postInit(servletContext, classes, findJerseyServletNames(servletContext));
    }
    // ON REGISTER
    for (final ServletContainerProvider servletContainerProvider : allServletContainerProviders) {
      servletContainerProvider.onRegister(servletContext, findJerseyServletNames(servletContext));
    }
  }

  private void onStartupImpl(final Set<Class<?>> classes, final ServletContext servletContext) throws ServletException {
    // first see if there are any application classes in the web app
    for (final Class<? extends Application> applicationClass : getApplicationClasses(classes)) {
      final ServletRegistration servletRegistration = servletContext.getServletRegistration(applicationClass.getName());

      if (servletRegistration != null) {
        addServletWithExistingRegistration(servletContext, servletRegistration, applicationClass, classes);
      } else {
        // Servlet is not registered with app name or the app name is used to register a different servlet
        // check if some servlet defines the app in init params
        final List<Registration> srs = getInitParamDeclaredRegistrations(servletContext, applicationClass);
        if (!srs.isEmpty()) {
          // app handled by at least one servlet or filter
          // fix the registrations if needed (i.e. add servlet class)
          for (final Registration sr : srs) {
            if (sr instanceof ServletRegistration) {
              addServletWithExistingRegistration(servletContext, (ServletRegistration) sr, applicationClass, classes);
            }
          }
        } else {
          // app not handled by any servlet/filter -> add it
          addServletWithApplication(servletContext, applicationClass, classes);
        }
      }
    }

    // check for javax.ws.rs.core.Application registration
    addServletWithDefaultConfiguration(servletContext, classes);
  }

  /**
   * Returns names of all registered Jersey servlets. Servlets are configured in {@code web.xml} or managed via Servlet
   * API.
   *
   * @param servletContext the {@link ServletContext} of the web application that is being started
   * @return list of Jersey servlet names or empty array, never returns {@code null}
   */
  private static Set<String> findJerseyServletNames(final ServletContext servletContext) {
    final Set<String> jerseyServletNames = new HashSet<>();

    for (final ServletRegistration servletRegistration : servletContext.getServletRegistrations().values()) {
      if (isJerseyServlet(servletRegistration.getClassName())) {
        jerseyServletNames.add(servletRegistration.getName());
      }
    }
    return Collections.unmodifiableSet(jerseyServletNames);
  }

  /**
   * Check if the {@code className} is an implementation of a Jersey Servlet container.
   *
   * @return {@code true} if the class is a Jersey servlet container class, {@code false} otherwise.
   */
  private static boolean isJerseyServlet(@Nullable final String className) {
    return ServletContainer.class.getName().equals(className)
      || "org.glassfish.jersey.servlet.portability.PortableServletContainer".equals(className);
  }

  private static List<Registration> getInitParamDeclaredRegistrations(final ServletContext context,
    final Class<? extends Application> clazz) {
    final List<Registration> registrations = new ArrayList<>();
    collectJaxRsRegistrations(context.getServletRegistrations(), registrations, clazz);
    collectJaxRsRegistrations(context.getFilterRegistrations(), registrations, clazz);
    return registrations;
  }

  private static void collectJaxRsRegistrations(final Map<String, ? extends Registration> registrations,
    final List<Registration> collected, final Class<? extends Application> a) {
    for (final Registration sr : registrations.values()) {
      final Map<String, String> ips = sr.getInitParameters();
      String appClass = ips.get(ServletProperties.JAXRS_APPLICATION_CLASS);
      if (appClass != null) {
        if (appClass.equals(a.getName())) {
          collected.add(sr);
        }
      }
    }
  }

  /**
   * Enhance default servlet (named {@link Application}) configuration.
   */
  private static void addServletWithDefaultConfiguration(final ServletContext context, final Set<Class<?>> classes)
    throws ServletException {

    ServletRegistration registration = context.getServletRegistration(Application.class.getName());

    if (registration != null) {
      final Set<Class<?>> appClasses = getRootResourceAndProviderClasses(classes);
      final ResourceConfig resourceConfig = ResourceConfig.forApplicationClass(ResourceConfig.class, appClasses)
        .addProperties(getInitParams(registration))
        .addProperties(Utils.getContextParams(context));

      if (registration.getClassName() != null) {
        // class name present - complete servlet registration from container point of view
        Utils.store(resourceConfig, context, registration.getName());
      } else {
        // no class name - no complete servlet registration from container point of view
        final ServletContainer servlet = new ServletContainer(resourceConfig);
        registration = context.addServlet(registration.getName(), servlet);
        ((ServletRegistration.Dynamic) registration).setLoadOnStartup(1);

        if (registration.getMappings().isEmpty()) {
          // Error
          sLogger.warn("JERSEY_APP_NO_MAPPING {}", registration.getName());
        } else {
          sLogger.info("JERSEY_APP_REGISTERED_CLASSES {} {}", registration.getName(), appClasses);
        }
      }
    }
  }

  /**
   * Add new servlet according to {@link Application} subclass with {@link ApplicationPath} annotation or existing
   * {@code servlet-mapping}.
   */
  private static void addServletWithApplication(final ServletContext context, final Class<? extends Application> clazz,
    final Set<Class<?>> defaultClasses) throws ServletException {
    final ApplicationPath ap = clazz.getAnnotation(ApplicationPath.class);
    if (ap != null) {
      // App is annotated with ApplicationPath
      final ResourceConfig resourceConfig = ResourceConfig.forApplicationClass(clazz, defaultClasses)
        .addProperties(Utils.getContextParams(context));
      final ServletContainer s = new ServletContainer(resourceConfig);
      final ServletRegistration.Dynamic dsr = context.addServlet(clazz.getName(), s);
      dsr.setAsyncSupported(true);
      dsr.setLoadOnStartup(1);

      final String mapping = createMappingPath(ap);
      if (!mappingExists(context, mapping)) {
        dsr.addMapping(mapping);

        sLogger.info("JERSEY_APP_REGISTERED_MAPPING {} {}", clazz.getName(), mapping);
      } else {
        sLogger.warn("JERSEY_APP_MAPPING_CONFLICT {} {}", clazz.getName(), mapping);
      }
    }
  }

  /**
   * Enhance existing servlet configuration.
   */
  private static void addServletWithExistingRegistration(final ServletContext context, ServletRegistration registration,
    final Class<? extends Application> clazz, final Set<Class<?>> classes) throws ServletException {
    // create a new servlet container for a given app.
    final ResourceConfig resourceConfig = ResourceConfig.forApplicationClass(clazz, classes)
      .addProperties(getInitParams(registration))
      .addProperties(Utils.getContextParams(context));

    if (registration.getClassName() != null) {
      // class name present - complete servlet registration from container point of view
      Utils.store(resourceConfig, context, registration.getName());
    } else {
      // no class name - no complete servlet registration from container point of view
      final ServletContainer servlet = new ServletContainer(resourceConfig);
      final ServletRegistration.Dynamic dynamicRegistration = context.addServlet(clazz.getName(), servlet);
      dynamicRegistration.setAsyncSupported(true);
      dynamicRegistration.setLoadOnStartup(1);
      registration = dynamicRegistration;
    }
    if (registration.getMappings().isEmpty()) {
      final ApplicationPath ap = clazz.getAnnotation(ApplicationPath.class);
      if (ap != null) {
        final String mapping = createMappingPath(ap);
        if (!mappingExists(context, mapping)) {
          registration.addMapping(mapping);

          sLogger.info("JERSEY_APP_REGISTERED_MAPPING {} {}", clazz.getName(), mapping);
        } else {
          sLogger.warn("JERSEY_APP_MAPPING_CONFLICT {} {} ", clazz.getName(), mapping);
        }
      } else {
        // Error
        sLogger.warn("JERSEY_APP_NO_MAPPING_OR_ANNOTATION {} {}",
          clazz.getName(),
          ApplicationPath.class.getSimpleName()
        );
      }
    } else {
      sLogger.info("JERSEY_APP_REGISTERED_APPLICATION {}", clazz.getName());
    }
  }

  private static Map<String, Object> getInitParams(final ServletRegistration sr) {
    final Map<String, Object> initParams = new HashMap<>();
    for (final Map.Entry<String, String> entry : sr.getInitParameters().entrySet()) {
      initParams.put(entry.getKey(), entry.getValue());
    }
    return initParams;
  }

  private static boolean mappingExists(final ServletContext sc, final String mapping) {
    for (final ServletRegistration sr : sc.getServletRegistrations().values()) {
      for (final String declaredMapping : sr.getMappings()) {
        if (mapping.equals(declaredMapping)) {
          return true;
        }
      }
    }

    return false;
  }

  private static String createMappingPath(final ApplicationPath ap) {
    String path = ap.value();
    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    if (!path.endsWith("/*")) {
      if (path.endsWith("/")) {
        path += "*";
      } else {
        path += "/*";
      }
    }

    return path;
  }

  private static Set<Class<? extends Application>> getApplicationClasses(final Set<Class<?>> classes) {
    final Set<Class<? extends Application>> s = new LinkedHashSet<>();
    for (final Class<?> c : classes) {
      if (Application.class != c && Application.class.isAssignableFrom(c)) {
        s.add(c.asSubclass(Application.class));
      }
    }

    return s;
  }

  private static Set<Class<?>> getRootResourceAndProviderClasses(final Set<Class<?>> classes) {
    // TODO filter out any classes from the Jersey jars
    final Set<Class<?>> s = new LinkedHashSet<>();
    for (final Class<?> c : classes) {
      if (c.isAnnotationPresent(Path.class) || c.isAnnotationPresent(Provider.class)) {
        s.add(c);
      }
    }

    return s;
  }

}

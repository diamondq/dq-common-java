package com.diamondq.common.jaxrs.model;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;

/**
 * This represents a service that needs to inject JAXRS application features. This is the equivalent of the
 * javax.ws.rs.core.Application but for multiple services that need appear under a single JAXRS Application. NOTE:
 * Documentation on the methods is directly copied from the Application class.
 */
public interface JAXRSApplicationInfo {

  /**
   * Get a set of root resource, provider and {@link Feature feature} classes. The default life-cycle for resource class
   * instances is per-request. The default life-cycle for providers (registered directly or via a feature) is singleton.
   * <p>
   * Implementations should warn about and ignore classes that do not conform to the requirements of root resource or
   * provider/feature classes. Implementations should warn about and ignore classes for which {@link #getSingletons()}
   * returns an instance. Implementations MUST NOT modify the returned set.
   * </p>
   * <p>
   * The default implementation returns an empty set.
   * </p>
   *
   * @return a set of root resource and provider classes. Returning {@code null} is equivalent to returning an empty
   *         set.
   */
  public Set<Class<?>> getClasses();

  /**
   * Get a set of root resource, provider and {@link Feature feature} instances. Fields and properties of returned
   * instances are injected with their declared dependencies (see {@link Context}) by the runtime prior to use.
   * <p>
   * Implementations should warn about and ignore classes that do not conform to the requirements of root resource or
   * provider classes. Implementations should flag an error if the returned set includes more than one instance of the
   * same class. Implementations MUST NOT modify the returned set.
   * </p>
   * <p>
   * The default implementation returns an empty set.
   * </p>
   *
   * @return a set of root resource and provider instances. Returning {@code null} is equivalent to returning an empty
   *         set.
   */
  public Set<Object> getSingletons();

  /**
   * Get a map of custom application-wide properties.
   * <p>
   * The returned properties are reflected in the application {@link Configuration configuration} passed to the
   * server-side features or injected into server-side JAX-RS components.
   * </p>
   * <p>
   * The set of returned properties may be further extended or customized at deployment time using container-specific
   * features and deployment descriptors. For example, in a Servlet-based deployment scenario, web application's
   * {@code <context-param>} and Servlet {@code <init-param>} values may be used to extend or override values of the
   * properties programmatically returned by this method.
   * </p>
   * <p>
   * The default implementation returns an empty set.
   * </p>
   *
   * @return a map of custom application-wide properties. Returning {@code null} is equivalent to returning an empty
   *         set.
   * @since 2.0
   */
  public Map<String, Object> getProperties();
}

package com.diamondq.common.injection.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;

/**
 * This extension is used to cache and fire events that happen in the CDI environment without coupling the class to the
 * event. Basically, using the CDI @Observes in an object ensures that the object is started if the event happens.
 * However, if you only want to be notified of the event if the object is started for a different reason (ie a
 * dependency), then this class works. Simply register a callback for the appropriate event.
 */
public class CachedEventExtension implements Extension {

  private final Set<String>                        mFiredEvents                        = new HashSet<>();

  private final Map<String, Set<Consumer<String>>> mCallbacks                          = new HashMap<>();

  public static final String                       APPLICATION_SCOPED_INITIALIZED      =
    "APPLICATION_SCOPED_INITIALIZED";

  public static final String                       APPLICATION_SCOPED_BEFORE_DESTROYED =
    "APPLICATION_SCOPED_BEFORE_DESTROYED";

  public static final String                       APPLICATION_SCOPED_DESTROYED        = "APPLICATION_SCOPED_DESTROYED";

  public static final String                       BEFORE_BEAN_DISCOVERY               = "BEFORE_BEAN_DISCOVERY";

  public static final String                       AFTER_TYPE_DISCOVERY                = "AFTER_TYPE_DISCOVERY";

  public static final String                       AFTER_BEAN_DISCOVERY                = "AFTER_BEAN_DISCOVERY";

  public static final String                       AFTER_DEPLOYMENT_VALIDATION         = "AFTER_DEPLOYMENT_VALIDATION";

  public static final String                       BEFORE_SHUTDOWN                     = "BEFORE_SHUTDOWN";

  /**
   * Checks whether an event has been fired
   *
   * @param pEvent the event
   * @return true or false
   */
  public boolean hasFired(String pEvent) {
    synchronized (this) {
      return mFiredEvents.contains(pEvent);
    }
  }

  /**
   * Register an event listener. NOTE: If the event has already been fired, then the callback is called immediately (ie.
   * within the context of this method call)
   *
   * @param pEvent the event to register
   * @param pEventCallback the callback to register (the callback is called with the event)
   */
  public void registerEventListener(String pEvent, Consumer<String> pEventCallback) {
    synchronized (this) {
      if (mFiredEvents.contains(pEvent))
        pEventCallback.accept(pEvent);
      else {
        Set<Consumer<String>> set = mCallbacks.get(pEvent);
        if (set == null) {
          set = new HashSet<>();
          mCallbacks.put(pEvent, set);
        }
        set.add(pEventCallback);
      }
    }
  }

  /**
   * Internal method to fire all callbacks for a given event
   *
   * @param pEvent the event
   */
  private void fireEvent(String pEvent) {
    synchronized (this) {
      mFiredEvents.add(pEvent);
      Set<Consumer<String>> set = mCallbacks.remove(pEvent);
      if (set != null)
        set.forEach((c) -> c.accept(pEvent));
    }
  }

  /* These are all event listeners tied into the CDI environment */

  void onApplicationScopedInit(@Observes @Initialized(ApplicationScoped.class) Object pScope) {
    fireEvent(APPLICATION_SCOPED_INITIALIZED);
  }

  // Only available in CDI 2.0
  // void onApplicationScopedBeforeDestroyed(@Observes @BeforeDestroyed(ApplicationScoped.class) Object pScope) {
  // fireEvent(APPLICATION_SCOPED_BEFORE_DESTROYED);
  // }

  void onApplicationScopedDestroyed(@Observes @Destroyed(ApplicationScoped.class) Object pScope) {
    fireEvent(APPLICATION_SCOPED_DESTROYED);
  }

  void onBeforeBeanDiscovery(@Observes BeforeBeanDiscovery pEvent) {
    fireEvent(BEFORE_BEAN_DISCOVERY);
  }

  void afterTypeDiscovery(@Observes AfterTypeDiscovery pEvent) {
    fireEvent(AFTER_TYPE_DISCOVERY);
  }

  void afterBeanDiscovery(@Observes AfterBeanDiscovery pEvent) {
    fireEvent(AFTER_BEAN_DISCOVERY);
  }

  void afterDeploymentValidation(@Observes AfterDeploymentValidation pEvent) {
    fireEvent(AFTER_DEPLOYMENT_VALIDATION);
  }

  void beforeShutdown(@Observes BeforeShutdown pEvent) {
    fireEvent(BEFORE_SHUTDOWN);
  }
}

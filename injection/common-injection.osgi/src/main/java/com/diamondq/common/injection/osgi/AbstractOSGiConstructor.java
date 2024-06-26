package com.diamondq.common.injection.osgi;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.injection.osgi.ConstructorInfo.ConstructionArg;
import com.diamondq.common.injection.osgi.ConstructorInfo.SpecialTypes;
import com.diamondq.common.injection.osgi.i18n.Messages;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import org.javatuples.Triplet;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractOSGiConstructor {

  private static final Logger sLogger = LoggerFactory.getLogger(AbstractOSGiConstructor.class);

  private static final Set<String> sSKIP_PROPS;

  static {
    Set<String> b = new HashSet<>();
    b.add(Constants.SERVICE_BUNDLEID);
    b.add(Constants.SERVICE_DESCRIPTION);
    b.add(Constants.SERVICE_EXPORTED_CONFIGS);
    b.add(Constants.SERVICE_EXPORTED_INTENTS);
    b.add(Constants.SERVICE_EXPORTED_INTENTS_EXTRA);
    b.add(Constants.SERVICE_EXPORTED_INTERFACES);
    b.add(Constants.SERVICE_ID);
    b.add(Constants.SERVICE_IMPORTED);
    b.add(Constants.SERVICE_IMPORTED_CONFIGS);
    b.add(Constants.SERVICE_INTENTS);
    b.add(Constants.SERVICE_PID);
    /* SERVICE_RANKING is specifically allowed to pass through */
    b.add(Constants.SERVICE_SCOPE);
    b.add(Constants.SERVICE_VENDOR);
    b.add(ConfigurationAdmin.SERVICE_FACTORYPID);
    b.add(ConfigurationAdmin.SERVICE_BUNDLELOCATION);
    sSKIP_PROPS = Collections.unmodifiableSet(b);
  }

  protected final Map<String, FilterTracker> mTrackers = new HashMap<>();

  protected final ConstructorInfo mInfo;

  protected volatile ContextFactory mContextFactory;

  protected volatile ComponentContext mComponentContext;

  protected volatile Map<String, Object> mCurrentProps;

  protected @Nullable ServiceRegistration<?> mRegistration;

  protected @Nullable Object mServiceObject;

  @SuppressWarnings("null")
  public AbstractOSGiConstructor(ConstructorInfoBuilder pBuilder) {
    ContextFactory.staticReportTrace(AbstractOSGiConstructor.class, this, pBuilder);
    try {
      mInfo = pBuilder.build();
      if ((mInfo.method != null) && (AbstractOSGiConstructor.class.isAssignableFrom(mInfo.constructionClass) == false))
        throw new ExtendedIllegalArgumentException(Messages.METHOD_ONLY_ON_FACTORY);
      mComponentContext = null;
      mCurrentProps = Collections.emptyMap();
    }
    catch (RuntimeException ex) {
      throw ContextFactory.staticReportThrowable(AbstractOSGiConstructor.class, this, ex);
    }
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(AbstractOSGiConstructor.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  public void onActivate(ComponentContext pContext, Map<String, Object> pProps) {
    try (Context context = mContextFactory.newContext(AbstractOSGiConstructor.class, this, pContext, pProps)) {
      synchronized (this) {
        mComponentContext = pContext;
        mCurrentProps = Collections.unmodifiableMap(pProps);
      }
      processProperties();
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(AbstractOSGiConstructor.class, this, ex);
    }
  }

  public void onModified(ComponentContext pContext, Map<String, Object> pProps) {
    try (Context context = mContextFactory.newContext(AbstractOSGiConstructor.class, this, pContext, pProps)) {

      synchronized (this) {
        mComponentContext = pContext;
        mCurrentProps = Collections.unmodifiableMap(pProps);
      }
      processProperties();
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(AbstractOSGiConstructor.class, this, ex);
    }
  }

  public void onDeactivate(ComponentContext pContext) {
    try (Context context = mContextFactory.newContext(AbstractOSGiConstructor.class, this, pContext)) {
      ServiceRegistration<?> registration = mRegistration;
      if (registration != null) {
        sLogger.trace("Clearing old registration");
        registration.unregister();
        if (mInfo.deleteMethod != null) try {
          mInfo.deleteMethod.invoke(this, mServiceObject);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          throw new RuntimeException(ex);
        }
        mRegistration = null;
      }
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(AbstractOSGiConstructor.class, this, ex);
    }
  }

  protected void processProperties() {
    try (Context context = mContextFactory.newContext(AbstractOSGiConstructor.class, this)) {

      synchronized (this) {

        String errorId =
          mComponentContext.getBundleContext().getBundle().getSymbolicName() + " with " + mCurrentProps.toString();

        /*
         * Shut down all the service trackers. NOTE: With the rebuilding flag on, these should NOT update any of the
         * children, since they may rebuild back to the same objects
         */

        for (FilterTracker tracker : mTrackers.values())
          tracker.closeForRebuild();

        for (int i = 0; i < mInfo.filters.length; i++) {
          String filterStr = PropertiesParsing.getNullableString(mCurrentProps, mInfo.filters[i]);
          if (filterStr == null) continue;
          String finalFilterStr;
          if (filterStr.isEmpty() == true) finalFilterStr = "(objectClass=" + mInfo.filterClasses[i].getName() + ")";
          else finalFilterStr = "(&(objectClass=" + mInfo.filterClasses[i].getName() + ")" + filterStr + ")";
          Filter filter;
          try {
            filter = mComponentContext.getBundleContext().createFilter(finalFilterStr);
          }
          catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Unable to parse the filter " + finalFilterStr + " within " + errorId,
              ex
            );
          }

          /* Create or get a FilterTracker */

          FilterTracker filterTracker = mTrackers.get(mInfo.filters[i]);
          if (filterTracker == null) {
            filterTracker = new FilterTracker(mComponentContext.getBundleContext());
            mTrackers.put(mInfo.filters[i], filterTracker);
          }

          sLogger.trace("Starting ServiceTracker for {}", finalFilterStr);
          ServiceTracker<Object, Object> tracker = new ServiceTracker<>(mComponentContext.getBundleContext(),
            filter,
            filterTracker
          );
          filterTracker.setTracker(tracker);
          tracker.open();
        }

        build();

        for (FilterTracker tracker : mTrackers.values())
          tracker.setNotify((f) -> build());
      }

    }

  }

  protected void build() {
    try (Context context = mContextFactory.newContext(AbstractOSGiConstructor.class, this)) {

      synchronized (this) {

        /* Now that all the filters are active, create the initial output, and then turn on future notifications */

        boolean available = true;

        String errorId =
          mComponentContext.getBundleContext().getBundle().getSymbolicName() + " with " + mCurrentProps.toString();

        @Nullable Object[] args = new @Nullable Object[mInfo.constructionArgs.length];
        for (int i = 0; i < mInfo.constructionArgs.length; i++) {
          ConstructionArg arg = mInfo.constructionArgs[i];
          Object value = null;
          if (arg.propertyFilterKey != null) {
            FilterTracker tracker = mTrackers.get(arg.propertyFilterKey);
            if (tracker == null) {
              if (arg.required == true)
                throw new ExtendedIllegalArgumentException(Messages.NO_PROP_MATCHING_FILTER, mInfo.filters[i], errorId);
            } else {
              List<Triplet<Integer, Long, ServiceReference<Object>>> references = tracker.getReferences();
              if (references.isEmpty()) {
                if (arg.required == Boolean.TRUE) {
                  sLogger.trace("\tUnable to find references to arg #{}: propertyFilterKey={}",
                    i,
                    arg.propertyFilterKey
                  );
                  available = false;
                  break;
                }
              } else {
                if (arg.collection == true) {
                  List<Object> list = new ArrayList<>();
                  for (Triplet<Integer, Long, ServiceReference<Object>> triplet : references) {
                    ServiceReference<Object> ref = triplet.getValue2();
                    Object obj = mComponentContext.getBundleContext().getService(ref);
                    if (obj == null) {
                      if (arg.required == Boolean.TRUE) {
                        sLogger.trace("\tUnable to resolve reference to arg #{}: propertyFilterKey={} -> {}",
                          i,
                          arg.propertyFilterKey,
                          ref
                        );
                        list = null;
                        available = false;
                        break;
                      }
                    } else list.add(obj);
                  }
                  value = list;
                } else {
                  ServiceReference<Object> ref = references.iterator().next().getValue2();
                  Object obj = mComponentContext.getBundleContext().getService(ref);
                  if (obj == null) {
                    if (arg.required == Boolean.TRUE) {
                      sLogger.trace("\tUnable to resolve reference to arg #{}: propertyFilterKey={} -> {}",
                        i,
                        arg.propertyFilterKey,
                        ref
                      );
                      available = false;
                      break;
                    }
                  } else value = obj;
                }
              }
            }
          } else if (arg.propertyValueKey != null) {
            String propValue = arg.propertyValueKey;
            if (arg.argumentClass == String.class)
              value = PropertiesParsing.getNullableString(mCurrentProps, propValue);
            else if ((arg.argumentClass == Integer.class) || (arg.argumentClass == Integer.TYPE))
              value = PropertiesParsing.getNullableInt(mCurrentProps, propValue);
            else if ((arg.argumentClass == Long.class) || (arg.argumentClass == Long.TYPE))
              value = PropertiesParsing.getNullableLong(mCurrentProps, propValue);
            else if ((arg.argumentClass == Boolean.class) || (arg.argumentClass == Boolean.TYPE))
              value = PropertiesParsing.getNullableBoolean(mCurrentProps, propValue);
            else if (arg.argumentClass == File.class) {
              String valueStr = PropertiesParsing.getNullableString(mCurrentProps, propValue);
              if (valueStr != null) value = new File(valueStr);
            } else throw new UnsupportedOperationException();
            if (value == null) {
              if (arg.propertyValueSet == true) value = arg.propertyValue;
              if (value == null) {
                if (arg.required == Boolean.TRUE) {
                  sLogger.trace("\tUnable to resolve reference to arg #{}: propertyValueKey={}",
                    i,
                    arg.propertyValueKey
                  );
                  available = false;
                  break;
                }
              }
            }
          } else if (arg.specialType != SpecialTypes.NA) {
            switch (arg.specialType) {
              case BUNDLECONTEXT:
                value = mComponentContext.getBundleContext();
                break;
              case CONTEXTFACTORY:
                value = mContextFactory;
                break;
              case COMPONENTCONTEXT:
                value = mComponentContext;
                break;
              case NA:
                throw new IllegalStateException();
            }
          } else throw new UnsupportedOperationException();
          args[i] = value;
        }

        if (available == true) {
          @Nullable Object service;
          try {
            Constructor<?> c = mInfo.constructor;
            if (c != null) {
              Object serviceObj = c.newInstance(args);
              service = serviceObj;
            } else {
              Method m = mInfo.method;
              if (m != null) {
                Object serviceObj = m.invoke(this, args);
                service = serviceObj;
              } else throw new IllegalStateException();
            }
          }
          catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException ex) {
            throw new RuntimeException(ex);
          }
          if (service != null) registerService(service);
        } else {
          ServiceRegistration<?> registration = mRegistration;
          if (registration != null) {
            sLogger.trace("Clearing old registration");
            registration.unregister();
            if (mInfo.deleteMethod != null) try {
              mInfo.deleteMethod.invoke(this, mServiceObject);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
              throw new RuntimeException(ex);
            }
            mRegistration = null;
          }
        }

      }
    }
  }

  protected void registerService(Object pService) {
    Dictionary<String, Object> properties = new Hashtable<>();

    /* Add all the properties that do not start with a .period */

    for (Map.Entry<String, Object> pair : mCurrentProps.entrySet()) {
      String key = pair.getKey();
      if (key.startsWith(".")) continue;
      if (sSKIP_PROPS.contains(key) == true) continue;
      properties.put(key, pair.getValue());
    }

    sLogger.trace("Registering constructed service...");
    mServiceObject = pService;
    mRegistration = mComponentContext.getBundleContext()
      .registerService(mInfo.registrationClasses, pService, properties);
  }
}

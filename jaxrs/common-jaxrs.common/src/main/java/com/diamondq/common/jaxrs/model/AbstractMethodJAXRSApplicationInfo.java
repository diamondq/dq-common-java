package com.diamondq.common.jaxrs.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractMethodJAXRSApplicationInfo implements JAXRSApplicationInfo {
  private static final Logger   sLogger = LoggerFactory.getLogger(AbstractMethodJAXRSApplicationInfo.class);

  protected Set<Class<?>>       mClasses;

  protected Set<Object>         mSingletons;

  protected Map<String, Object> mProperties;

  public AbstractMethodJAXRSApplicationInfo() {
    mClasses = new HashSet<>();
    mSingletons = new HashSet<>();
    mProperties = new HashMap<>();
  }

  /**
   * @see com.diamondq.common.jaxrs.model.JAXRSApplicationInfo#getClasses()
   */
  @Override
  public Set<Class<?>> getClasses() {
    sLogger.trace("getClasses() from {}", this);
    return mClasses;
  }

  /**
   * @see com.diamondq.common.jaxrs.model.JAXRSApplicationInfo#getSingletons()
   */
  @Override
  public Set<Object> getSingletons() {
    sLogger.trace("getSingletons() from {}", this);
    return mSingletons;
  }

  /**
   * @see com.diamondq.common.jaxrs.model.JAXRSApplicationInfo#getProperties()
   */
  @Override
  public Map<String, Object> getProperties() {
    sLogger.trace("getProperties() from {}", this);
    return mProperties;
  }

}

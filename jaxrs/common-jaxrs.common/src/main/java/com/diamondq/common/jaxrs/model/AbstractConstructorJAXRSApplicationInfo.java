package com.diamondq.common.jaxrs.model;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AbstractConstructorJAXRSApplicationInfo implements JAXRSApplicationInfo {
  private static final Logger sLogger = LoggerFactory.getLogger(AbstractConstructorJAXRSApplicationInfo.class);

  private final Set<Class<?>> mClasses;

  private final Set<Object> mSingletons;

  private final Map<String, Object> mProperties;

  public AbstractConstructorJAXRSApplicationInfo(@Nullable Set<Class<?>> pClasses, @Nullable Set<Object> pSingletons,
    @Nullable Map<String, Object> pProperties) {
    mClasses = (pClasses != null ? pClasses : Collections.emptySet());
    mSingletons = (pSingletons != null ? pSingletons : Collections.emptySet());
    mProperties = (pProperties != null ? pProperties : Collections.emptyMap());
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

package com.diamondq.common.security.openaz.mappers;

import org.apache.openaz.pepapi.MapperRegistry;
import org.apache.openaz.pepapi.ObjectMapper;
import org.apache.openaz.pepapi.PepConfig;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractObjectMapper implements ObjectMapper {

  protected final Class<?> mClass;

  @Nullable
  protected MapperRegistry mRegistry;

  @Nullable
  protected PepConfig      mConfig;

  public AbstractObjectMapper(Class<?> pClass) {
    mClass = pClass;
  }

  /**
   * @see org.apache.openaz.pepapi.ObjectMapper#getMappedClass()
   */
  @Override
  public Class<?> getMappedClass() {
    return mClass;
  }

  /**
   * @see org.apache.openaz.pepapi.ObjectMapper#setMapperRegistry(org.apache.openaz.pepapi.MapperRegistry)
   */
  @Override
  public void setMapperRegistry(MapperRegistry pMapperRegistry) {
    mRegistry = pMapperRegistry;
  }

  /**
   * @see org.apache.openaz.pepapi.ObjectMapper#setPepConfig(org.apache.openaz.pepapi.PepConfig)
   */
  @Override
  public void setPepConfig(PepConfig pPepConfig) {
    mConfig = pPepConfig;
  }

}

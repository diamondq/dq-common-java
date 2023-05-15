package com.diamondq.common.injection.impl;

import com.diamondq.common.injection.InjectionContext;
import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InjectionContextImpl implements InjectionContext {

  private @Nullable ApplicationContext mAppContext;

  public InjectionContextImpl() {
  }

  public void setApplicationContext(ApplicationContext pAppContext) {
    mAppContext = pAppContext;
  }

  /**
   * @see com.diamondq.common.injection.InjectionContext#findBean(java.lang.Class, java.lang.String)
   */
  @Override
  public <T> Optional<T> findBean(Class<T> pBeanType, @Nullable String pName) {
    if (pName != null) {
      return Objects.requireNonNull(mAppContext).findBean(pBeanType, Qualifiers.byName(Objects.requireNonNull(pName)));
    } else {
      return Objects.requireNonNull(mAppContext).findBean(pBeanType);
    }
  }

  /**
   * @see com.diamondq.common.injection.InjectionContext#getBeansOfType(java.lang.Class, java.lang.String)
   */
  @Override
  public <T> Collection<T> getBeansOfType(Class<T> pBeanType, @Nullable String pName) {
    if (pName != null) {
      return Objects.requireNonNull(mAppContext)
        .getBeansOfType(pBeanType, Qualifiers.byName(Objects.requireNonNull(pName)));
    } else {
      return Objects.requireNonNull(mAppContext).getBeansOfType(pBeanType);
    }
  }

  /**
   * @see com.diamondq.common.injection.InjectionContext#getProperties(java.lang.String)
   */
  @Override
  public Map<String, Object> getProperties(String pPrefix) {
    return Objects.requireNonNull(mAppContext).getEnvironment().getProperties(pPrefix);
  }

  /**
   * @see java.io.Closeable#close()
   */
  @Override
  public void close() throws IOException {
    Objects.requireNonNull(mAppContext).close();
  }
}

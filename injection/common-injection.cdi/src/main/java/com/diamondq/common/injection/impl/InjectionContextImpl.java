package com.diamondq.common.injection.impl;

import com.diamondq.common.injection.InjectionContext;
import org.jetbrains.annotations.Nullable;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.enterprise.inject.se.SeContainer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InjectionContextImpl implements InjectionContext {

  private @Nullable SeContainer mAppContext;

  public InjectionContextImpl() {
  }

  public void setApplicationContext(SeContainer pAppContext) {
    mAppContext = pAppContext;
  }

  /**
   * @see com.diamondq.common.injection.InjectionContext#findBean(java.lang.Class, java.lang.String)
   */
  @Override
  public <T> Optional<T> findBean(Class<T> pBeanType, @Nullable String pName) {
    Instance<T> instance;
    if (pName != null) instance = Objects.requireNonNull(mAppContext).select(pBeanType, NamedLiteral.of(pName));
    else instance = Objects.requireNonNull(mAppContext).select(pBeanType);
    if (instance.isResolvable() == false) return Optional.empty();
    return Optional.of(instance.get());
  }

  /**
   * @see com.diamondq.common.injection.InjectionContext#getBeansOfType(java.lang.Class, java.lang.String)
   */
  @Override
  public <T> Collection<T> getBeansOfType(Class<T> pBeanType, @Nullable String pName) {
    List<T> list = new ArrayList<>();
    Instance<T> instance;
    if (pName != null) instance = Objects.requireNonNull(mAppContext).select(pBeanType, NamedLiteral.of(pName));
    else instance = Objects.requireNonNull(mAppContext).select(pBeanType);
    for (Iterator<T> i = instance.iterator(); i.hasNext(); )
      list.add(i.next());
    return list;
  }

  /**
   * @see com.diamondq.common.injection.InjectionContext#getProperties(java.lang.String)
   */
  @Override
  public Map<String, Object> getProperties(String pPrefix) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see java.io.Closeable#close()
   */
  @Override
  public void close() throws IOException {
    Objects.requireNonNull(mAppContext).close();
  }
}

package com.diamondq.common.injection.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.diamondq.common.injection.InjectionContext;
import com.diamondq.common.injection.InjectionStartupBuilder;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;

public class InjectionStartupBuilderImpl implements InjectionStartupBuilder {

  private final Set<String>               mEnvironmentTags;

  private final List<Map<String, Object>> mPropertiesList;

  private final Set<Object>               mSingletons;

  public InjectionStartupBuilderImpl() {
    mEnvironmentTags = new HashSet<>();
    mPropertiesList = new ArrayList<Map<String, Object>>();
    mSingletons = new HashSet<>();
  }

  /**
   * @see com.diamondq.common.injection.InjectionStartupBuilder#addEnvironmentTag(java.lang.String)
   */
  @Override
  public InjectionStartupBuilder addEnvironmentTag(String pTag) {
    synchronized (this) {
      mEnvironmentTags.add(pTag);
    }
    return this;
  }

  /**
   * @see com.diamondq.common.injection.InjectionStartupBuilder#addPropertyMap(java.util.Map)
   */
  @Override
  public InjectionStartupBuilder addPropertyMap(Map<String, Object> pProperties) {
    synchronized (this) {
      final Map<String, Object> copy = new HashMap<>(pProperties);
      mPropertiesList.add(copy);
    }
    return this;
  }

  /**
   * @see com.diamondq.common.injection.InjectionStartupBuilder#singletons(java.lang.Object[])
   */
  @Override
  public InjectionStartupBuilder singletons(@NonNull Object @NonNull... pSingletons) {
    synchronized (this) {
      for (final Object o : pSingletons)
        mSingletons.add(o);
    }
    return this;
  }

  /**
   * @see com.diamondq.common.injection.InjectionStartupBuilder#buildAndStart()
   */
  @Override
  public InjectionContext buildAndStart() {
    synchronized (this) {
      ApplicationContextBuilder builder = ApplicationContext.build();
      for (final String tag : mEnvironmentTags)
        builder = builder.environments(tag);
      for (final Map<String, Object> props : mPropertiesList)
        builder = builder.properties(props);
      final InjectionContextImpl injectionContext = new InjectionContextImpl();
      builder = builder.singletons(injectionContext);
      if (mSingletons.isEmpty() == false)
        builder = builder.singletons(mSingletons);
      final ApplicationContext appContext = builder.build();
      injectionContext.setApplicationContext(appContext);
      appContext.start();
      return injectionContext;
    }
  }

}
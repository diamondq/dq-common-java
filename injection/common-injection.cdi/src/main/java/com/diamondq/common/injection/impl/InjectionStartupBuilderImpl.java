package com.diamondq.common.injection.impl;

import com.diamondq.common.injection.InjectionContext;
import com.diamondq.common.injection.InjectionStartupBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import org.checkerframework.checker.nullness.qual.NonNull;

public class InjectionStartupBuilderImpl implements InjectionStartupBuilder {

  private final Set<String>               mEnvironmentTags;

  private final List<Map<String, Object>> mPropertiesList;

  private final Set<Object>               mSingletons;

  private final Map<Object, String>       mNamedSingletons;

  public InjectionStartupBuilderImpl() {
    mEnvironmentTags = new HashSet<>();
    mPropertiesList = new ArrayList<Map<String, Object>>();
    mSingletons = new HashSet<>();
    mNamedSingletons = new HashMap<>();
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
   * @see com.diamondq.common.injection.InjectionStartupBuilder#singleton(java.lang.Object, java.lang.String)
   */
  @Override
  public InjectionStartupBuilder singleton(Object pSingleton, String pName) {
    synchronized (this) {
      mNamedSingletons.put(pSingleton, pName);
    }
    return this;
  }

  /**
   * @see com.diamondq.common.injection.InjectionStartupBuilder#parent(com.diamondq.common.injection.InjectionContext)
   */
  @Override
  public InjectionStartupBuilder parent(InjectionContext pParent) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.injection.InjectionStartupBuilder#buildAndStart()
   */
  @Override
  public InjectionContext buildAndStart() {
    synchronized (this) {
      SeContainerInitializer initializer = SeContainerInitializer.newInstance();
      Map<String, Object> allProps = new HashMap<>();
      for (final Map<String, Object> props : mPropertiesList)
        allProps.putAll(props);
      initializer.setProperties(allProps);
      final InjectionContextImpl injectionContext = new InjectionContextImpl();
      initializer.addExtensions(new Extension() {
        @SuppressWarnings("unused")
        void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
          event.addBean().types(InjectionContext.class).qualifiers(new AnnotationLiteral<Default>() {
            private static final long serialVersionUID = 1L;
          }, new AnnotationLiteral<Any>() {
            private static final long serialVersionUID = 1L;
          }).scope(ApplicationScoped.class).name(InjectionContext.class.getName()).beanClass(InjectionContext.class)
            .createWith(creationalContext -> {
              return injectionContext;
            });
          for (Object singleton : mSingletons) {
            event.addBean().types(singleton.getClass()).qualifiers(new AnnotationLiteral<Default>() {
              private static final long serialVersionUID = 1L;
            }, new AnnotationLiteral<Any>() {
              private static final long serialVersionUID = 1L;
            }).scope(ApplicationScoped.class).name(singleton.getClass().getName()).beanClass(singleton.getClass())
              .createWith(creationalContext -> {
                return singleton;
              });
          }
          for (Map.Entry<Object, String> pair : mNamedSingletons.entrySet()) {
            Object singleton = pair.getKey();
            String name = pair.getValue();
            event.addBean().types(singleton.getClass()).qualifiers(new AnnotationLiteral<Default>() {
              private static final long serialVersionUID = 1L;
            }, new AnnotationLiteral<Any>() {
              private static final long serialVersionUID = 1L;
            }).scope(ApplicationScoped.class).name(name).beanClass(singleton.getClass())
              .createWith(creationalContext -> {
                return singleton;
              });
          }
        }
      });
      SeContainer container = initializer.initialize();
      injectionContext.setApplicationContext(container);
      return injectionContext;
    }
  }

}

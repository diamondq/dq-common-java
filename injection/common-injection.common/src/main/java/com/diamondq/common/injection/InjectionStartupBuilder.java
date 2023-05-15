package com.diamondq.common.injection;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface InjectionStartupBuilder {

  /**
   * Add an environment tag which is used to filter out classes of injectable sources
   *
   * @param pTag the tag
   * @return the builder for fluent use
   */
  public InjectionStartupBuilder addEnvironmentTag(String pTag);

  /**
   * Adds a Map of properties to be used by the injection engine
   *
   * @param pProperties the properties
   * @return the builder for fluent use
   */
  public InjectionStartupBuilder addPropertyMap(Map<String, Object> pProperties);

  /**
   * Adds singletons to the injection engine
   *
   * @param pSingletons
   * @return the builder for fluent use
   */
  public InjectionStartupBuilder singletons(@NotNull Object... pSingletons);

  /**
   * Adds a singleton to the injection engine with the given name
   *
   * @param pSingleton the singleton
   * @param pName the name
   * @return the builder for fluent use
   */
  public InjectionStartupBuilder singleton(Object pSingleton, String pName);

  /**
   * Defines the parent. Multiple calls just replace the previous call.
   *
   * @param pParent the parent to use
   * @return the builder for fluent use
   */
  public InjectionStartupBuilder parent(InjectionContext pParent);

  /**
   * Defines an alternative classloader. If not provided, then the classloader that was used to load the
   * InjectionStartupBuilder class is used.
   *
   * @param pLoader the loader
   * @return the builder for fluent use
   */
  public InjectionStartupBuilder classLoader(ClassLoader pLoader);

  /**
   * Build the Injection engine and start it
   *
   * @return the InjectionStartup
   */
  public InjectionContext buildAndStart();

}

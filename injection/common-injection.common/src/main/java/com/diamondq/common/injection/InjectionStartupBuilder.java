package com.diamondq.common.injection;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface InjectionStartupBuilder
{

	/**
	 * Add an environment tag which is used to filter out classes of injectable sources
	 *
	 * @param pTag
	 *            the tag
	 * @return the builder for fluent use
	 */
	public InjectionStartupBuilder addEnvironmentTag(String pTag);

	/**
	 * Adds a Map of properties to be used by the injection engine
	 *
	 * @param pProperties
	 *            the properties
	 * @return the builder for fluent use
	 */
	public InjectionStartupBuilder addPropertyMap(Map<String, Object> pProperties);

	/**
	 * Adds singletons to the injection engine
	 *
	 * @param pSingletons
	 * @return the builder for fluent use
	 */
	public InjectionStartupBuilder singletons(@NonNull Object... pSingletons);

	/**
	 * Adds a singleton to the injection engine with the given name
	 * 
	 * @param pSingleton
	 *            the singleton
	 * @param pName
	 *            the name
	 * @return the builder for fluent use
	 */
	public InjectionStartupBuilder singleton(Object pSingleton, String pName);

	/**
	 * Build the Injection engine and start it
	 *
	 * @return the InjectionStartup
	 */
	public InjectionContext buildAndStart();

}

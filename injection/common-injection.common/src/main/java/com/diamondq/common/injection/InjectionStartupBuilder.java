package com.diamondq.common.injection;

import java.util.Map;

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
	 * Build the Injection engine and start it
	 *
	 * @return the InjectionStartup
	 */
	public InjectionContext buildAndStart();

}

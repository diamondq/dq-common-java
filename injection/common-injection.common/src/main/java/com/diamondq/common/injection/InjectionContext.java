package com.diamondq.common.injection;

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This represents the injection context that is being used
 */
public interface InjectionContext extends Closeable
{

	public static InjectionStartupBuilder builder()
	{
		try
		{
			@SuppressWarnings("unchecked")
			final Class<InjectionStartupBuilder> startupClass = (Class<InjectionStartupBuilder>) Class
					.forName("com.diamondq.common.injection.impl.InjectionStartupBuilderImpl");
			final InjectionStartupBuilder instance = startupClass.newInstance();
			return instance;
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Finds a Bean for the given type and qualifier.
	 *
	 * @param pBeanType
	 *            The bean type
	 * @param pName
	 *            the optional name
	 * @param <T>
	 *            The bean type parameter
	 * @return An instance of {@link Optional} that is either empty or containing the specified bean
	 */
	public <T> Optional<T> findBean(Class<T> pBeanType, @Nullable String pName);

	/**
	 * Get all beans of the given type.
	 *
	 * @param pBeanType
	 *            The bean type
	 * @param pName
	 *            the optional name
	 * @param <T>
	 *            The bean type parameter
	 * @return The found beans
	 */
	public <T> Collection<T> getBeansOfType(Class<T> pBeanType, @Nullable String pName);

	public Map<String, Object> getProperties(String pPrefix);
}
package com.diamondq.common.injection;

import org.jspecify.annotations.Nullable;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * This represents the injection context that is being used
 */
public interface InjectionContext extends Closeable {

  public static InjectionStartupBuilder builder() {
    try {
      /*
       * NOTE: This hack is due to the OSGi code determining that there needs to be a Import-Package for the
       * Class.forName call. Technically there is, as without it, this wouldn't work. However, this type of Injection is
       * not used in the OSGi model but other classes within this bundle are, so this is done to just make the
       * MANIFEST.MF clean
       */
      StringBuilder sb = new StringBuilder();
      sb.append("com")
        .append(".")
        .append("diamondq")
        .append(".")
        .append("common")
        .append(".")
        .append("injection")
        .append(".")
        .append("impl")
        .append(".")
        .append("InjectionStartupBuilderImpl");
      @SuppressWarnings(
        "unchecked") final Class<InjectionStartupBuilder> startupClass = (Class<InjectionStartupBuilder>) Class.forName(
        sb.toString());
      Constructor<InjectionStartupBuilder> constructor = startupClass.getConstructor((Class<?>[]) null);
      final InjectionStartupBuilder instance = constructor.newInstance((Object[]) null);
      return instance;
    }
    catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
           IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Finds a Bean for the given type and qualifier.
   *
   * @param pBeanType The bean type
   * @param pName the optional name
   * @param <T> The bean type parameter
   * @return An instance of {@link Optional} that is either empty or containing the specified bean
   */
  public <T> Optional<T> findBean(Class<T> pBeanType, @Nullable String pName);

  /**
   * Get all beans of the given type.
   *
   * @param pBeanType The bean type
   * @param pName the optional name
   * @param <T> The bean type parameter
   * @return The found beans
   */
  public <T> Collection<T> getBeansOfType(Class<T> pBeanType, @Nullable String pName);

  public Map<String, Object> getProperties(String pPrefix);
}

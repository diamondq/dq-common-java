package com.diamondq.common.i18n;

import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Allows the lookup of message bundles to be customized, without depending on ResourceBundle.Control (which is not
 * supported in named modules). Implementations are discovered through the standard ServiceLoader mechanism, or can be
 * registered directly with {@link I18N#addResourceBundleResolver(ResourceBundleResolver)} (which is what an OSGi component would do when
 * a ResourceBundleResolver service appears).
 */
public interface ResourceBundleResolver {

  /**
   * Returns the bundle to use, or null if this resolver has nothing to say about the given bundle, in which case the
   * next resolver (and eventually the standard lookup) is used. The first resolver to return a bundle wins, and its
   * bundle is used as is, so a resolver that only overrides some keys should make the bundle it returns fall back to the
   * default one (by setting a parent).
   *
   * @param pBasename the base name of the bundle
   * @param pLocale the locale
   * @param pClassLoader the ClassLoader that the standard lookup would use
   * @return the bundle, or null
   */
  @Nullable ResourceBundle getBundle(String pBasename, Locale pLocale, ClassLoader pClassLoader);
}

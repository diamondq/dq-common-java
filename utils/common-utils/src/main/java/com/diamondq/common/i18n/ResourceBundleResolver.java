package com.diamondq.common.i18n;

import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Allows the lookup of message bundles to be customized, without depending on ResourceBundle.Control (which is not
 * supported in named modules). Implementations are discovered through the standard ServiceLoader mechanism, or can be
 * registered directly with {@link I18N#addResourceBundleResolver(ResourceBundleResolver)} (which is what an OSGi
 * component would do when a ResourceBundleResolver service appears).
 * <p>
 * Resolvers form a chain. {@link I18N#getBundle(Class, String, Locale)} starts with the standard ResourceBundle lookup,
 * and then gives each resolver, in registration order, the bundle produced so far. A resolver can leave it alone, replace
 * it, or layer its own text on top of it (see {@link LayeredResourceBundle}), so several resolvers can each override
 * different messages.
 */
public interface ResourceBundleResolver {

  /**
   * Returns the bundle to use, or null if this resolver has nothing to change for the given bundle.
   * <p>
   * A resolver that only overrides some messages should return a bundle that falls back to pCurrent for the rest, such
   * as a {@link LayeredResourceBundle}. A resolver that returns a bundle that doesn't fall back to pCurrent replaces
   * everything from the earlier layers.
   * <p>
   * This is called once per bundle and locale (the result is cached until a resolver is added or removed), but it is
   * called for every locale that is asked about, so a resolver should return null quickly for locales it has no text for.
   * Note that a ResourceBundle looks up a message in its own text before consulting its parent, so text that a layer
   * provides for a general locale (such as the root locale) wins over more specific text from the layers below it.
   *
   * @param pBasename the base name of the bundle
   * @param pLocale the locale
   * @param pClassLoader the ClassLoader that the standard lookup uses
   * @param pCurrent the bundle produced by the standard lookup and any earlier resolvers, or null if no bundle exists
   *   yet
   * @return the bundle, or null to leave pCurrent as it is
   */
  @Nullable ResourceBundle getBundle(String pBasename, Locale pLocale, ClassLoader pClassLoader,
    @Nullable ResourceBundle pCurrent);
}

package com.diamondq.common.i18n;

import org.jspecify.annotations.Nullable;

import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.spi.ResourceBundleControlProvider;

/**
 * Internationalization helper class
 */
public class I18N {

  /**
   * The cached formats depend on the default locale (locales that resolve to the same bundle as the default are left
   * out), so the default locale is part of the key
   */
  private record CacheKey(MessagesEnum key, Locale defaultLocale) {
  }

  private static final ConcurrentMap<CacheKey, Map<Locale, String>> sCachedStrings = new ConcurrentHashMap<>();

  private static final @Nullable List<ResourceBundleControlProvider> providers;

  private static final Control INSTANCE;

  private static final List<ResourceBundleResolver> sResourceBundleResolvers = new CopyOnWriteArrayList<>();

  private record BundleKey(String basename, Locale locale) {
  }

  /**
   * The bundles that were built by running the resolvers, by ClassLoader. The ClassLoader is held weakly and the
   * bundles softly, since a bundle refers to the ClassLoader that loaded it.
   */
  private static final Map<ClassLoader, ConcurrentMap<BundleKey, SoftReference<ResourceBundle>>> sComposedBundles = Collections.synchronizedMap(
    new WeakHashMap<>());

  /**
   * Incremented whenever a resolver is added or removed
   */
  private static final AtomicLong sResolverVersion = new AtomicLong();

  static {
    try {
      for (ResourceBundleResolver resolver : ServiceLoader.load(ResourceBundleResolver.class, I18N.class.getClassLoader())) {
        sResourceBundleResolvers.add(resolver);
      }
    }
    catch (ServiceConfigurationError ex) {
      /* A broken provider entry must not prevent I18N from loading. Resolvers can still be added explicitly */
    }

    List<ResourceBundleControlProvider> list = null;
    ServiceLoader<ResourceBundleControlProvider> serviceLoaders = ServiceLoader.loadInstalled(
      ResourceBundleControlProvider.class);
    for (ResourceBundleControlProvider provider : serviceLoaders) {
      if (list == null) {
        list = new ArrayList<>();
      }
      list.add(provider);
    }
    providers = list;
    INSTANCE = new ResourceBundle.Control() {
    };
  }

  /**
   * Formats a given string (provided by a resource key) with arguments into a locale-specific value.
   *
   * @param pLocale the locale
   * @param pFormatKey the key
   * @param pArgs the arguments
   * @return the formated string
   */
  public static String getFormat(Locale pLocale, MessagesEnum pFormatKey, @Nullable Object @Nullable ... pArgs) {

    ResourceBundle bundle = pFormatKey.getBundle(pLocale);
    String format = bundle.getString(pFormatKey.getCode());
    MessageFormat messageFormat = new MessageFormat(format, pLocale);
    return messageFormat.format(resolveArgs(pLocale, pArgs));
  }

  /**
   * Replaces any nested I18NString arguments with their text in the given locale. Otherwise MessageFormat would call
   * their toString(), which always uses the default locale.
   *
   * @param pLocale the locale
   * @param pArgs the arguments
   * @return the original array if nothing needed replacing, otherwise a copy with the replacements
   */
  private static @Nullable Object @Nullable [] resolveArgs(Locale pLocale, @Nullable Object @Nullable [] pArgs) {
    if (pArgs == null) return null;
    @Nullable Object[] result = pArgs;
    for (int i = 0; i < pArgs.length; i++) {
      if (pArgs[i] instanceof I18NString nested) {
        if (result == pArgs) result = pArgs.clone();
        result[i] = getFormat(pLocale, nested.message, nested.params);
      }
    }
    return result;
  }

  public static Map<Locale, String> getAllFormats(MessagesEnum pFormatKey, @Nullable Object @Nullable ... pArgs) {

    /* This is a really expensive lookup, so the getString will be cached */

    Locale defaultLocale = Locale.getDefault();
    CacheKey cacheKey = new CacheKey(pFormatKey, defaultLocale);
    Map<Locale, String> map = sCachedStrings.get(cacheKey);
    if (map == null) {

      /* Perform the expensive iteration over all possible locales */

      ResourceBundle defaultBundle;
      try {
        defaultBundle = pFormatKey.getBundle(defaultLocale);
      }
      catch (MissingResourceException ex) {
        /* No bundle for the default locale, so there is nothing to de-duplicate against */
        defaultBundle = null;
      }

      Map<Locale, String> newMap = new HashMap<>();
      for (Locale locale : Locale.getAvailableLocales()) {
        try {
          ResourceBundle bundle = pFormatKey.getBundle(locale);
          if ((!locale.equals(defaultLocale)) && (bundle.equals(defaultBundle))) continue;
          String format = bundle.getString(pFormatKey.getCode());
          newMap.put(locale, format);
        }
        catch (MissingResourceException ex) {
          // Quietly ignore the exception
        }
      }
      Map<Locale, String> immutableMap = Collections.unmodifiableMap(newMap);
      if ((map = sCachedStrings.putIfAbsent(cacheKey, immutableMap)) == null) map = immutableMap;
    }

    Map<Locale, String> resultMap = new HashMap<>();
    for (Map.Entry<Locale, String> pair : map.entrySet()) {
      MessageFormat messageFormat = new MessageFormat(pair.getValue(), pair.getKey());
      resultMap.put(pair.getKey(), messageFormat.format(resolveArgs(pair.getKey(), pArgs)));
    }
    return resultMap;
  }

  public static Locale getDefaultLocale() {
    return Locale.getDefault();
  }

  /**
   * Registers a resolver that can customize the lookup of message bundles. Resolvers are asked in the order they were
   * added. Resolvers found by the ServiceLoader are registered automatically, so this is only needed for resolvers
   * that come from somewhere else (for example, OSGi services).
   *
   * @param pResolver the resolver
   */
  public static void addResourceBundleResolver(ResourceBundleResolver pResolver) {
    sResourceBundleResolvers.add(pResolver);
    resolversChanged();
  }

  /**
   * Removes a resolver that was previously added.
   *
   * @param pResolver the resolver
   * @return true if it was registered
   */
  public static boolean removeResourceBundleResolver(ResourceBundleResolver pResolver) {
    boolean removed = sResourceBundleResolvers.remove(pResolver);
    if (removed) resolversChanged();
    return removed;
  }

  private static void resolversChanged() {
    sResolverVersion.incrementAndGet();
    sComposedBundles.clear();
  }

  /**
   * Looks up the bundle for a MessagesEnum. Generated MessagesEnum classes should call this from their getBundle
   * method. It starts with the standard ResourceBundle lookup, using the ClassLoader from
   * {@link #getClassLoader(Class, String)}, and then passes the result through each registered
   * {@link ResourceBundleResolver} in registration order, so resolvers can layer their own messages on top of each
   * other. No ResourceBundle.Control is used, so this works in named modules and in OSGi (where resources can be
   * supplied by fragment bundles attached to the bundle that contains the enum).
   * <p>
   * When there are resolvers, the result is cached until a resolver is added or removed, so the same bundle instance is
   * returned for repeated lookups. The cache holds its bundles softly, so it doesn't keep the class loader of an
   * uninstalled OSGi bundle alive.
   *
   * @param pClass the MessagesEnum class
   * @param pBasename the base name of the bundle
   * @param pLocale the locale
   * @return the bundle
   * @throws MissingResourceException if no bundle can be found
   */
  public static ResourceBundle getBundle(Class<? extends MessagesEnum> pClass, String pBasename, Locale pLocale) {
    ClassLoader classLoader = getClassLoader(pClass, pBasename);
    if (sResourceBundleResolvers.isEmpty()) return ResourceBundle.getBundle(pBasename, pLocale, classLoader);

    BundleKey key = new BundleKey(pBasename, pLocale);
    ConcurrentMap<BundleKey, SoftReference<ResourceBundle>> perLoader = sComposedBundles.computeIfAbsent(classLoader,
      k -> new ConcurrentHashMap<>());
    SoftReference<ResourceBundle> ref = perLoader.get(key);
    ResourceBundle cached = (ref == null ? null : ref.get());
    if (cached != null) return cached;

    /* Only cache the result if no resolver was added or removed while it was being built */

    long version = sResolverVersion.get();
    ResourceBundle composed = composeBundle(pBasename, pLocale, classLoader);
    if (version == sResolverVersion.get()) perLoader.put(key, new SoftReference<>(composed));
    return composed;
  }

  private static ResourceBundle composeBundle(String pBasename, Locale pLocale, ClassLoader pClassLoader) {
    ResourceBundle current;
    MissingResourceException missing;
    try {
      current = ResourceBundle.getBundle(pBasename, pLocale, pClassLoader);
      missing = null;
    }
    catch (MissingResourceException ex) {
      /* A resolver might still supply the bundle */
      current = null;
      missing = ex;
    }
    for (ResourceBundleResolver resolver : sResourceBundleResolvers) {
      ResourceBundle next = resolver.getBundle(pBasename, pLocale, pClassLoader, current);
      if (next != null) current = next;
    }
    if (current == null) throw Objects.requireNonNull(missing);
    return current;
  }

  /**
   * This is called by MessageEnum classes that are generated to get the ClassLoader. The default behavior is to just
   * return the ClassLoader that loaded the enum class.
   *
   * @param pClass the MessagesEnum class
   * @param pBasename the base name
   * @return a ClassLoader
   */
  public static ClassLoader getClassLoader(Class<? extends MessagesEnum> pClass, String pBasename) {
    return pClass.getClassLoader();
  }

  /**
   * This is called by MessageEnum classes that are generated to get the Control. The default behavior is to return
   * null.
   *
   * @param pClass the MessagesEnum class
   * @param pBasename the base name
   * @return a Control
   */
  public static Control getControl(Class<? extends MessagesEnum> pClass, String pBasename) {
    if (providers != null) {
      for (ResourceBundleControlProvider provider : providers) {
        Control control = provider.getControl(pBasename);
        if (control != null) {
          return control;
        }
      }
    }
    return INSTANCE;
  }
}

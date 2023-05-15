package com.diamondq.common.i18n;

import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.ResourceBundleControlProvider;

/**
 * Internationalization helper class
 */
public class I18N {

  private static ConcurrentMap<String, Map<Locale, String>> sCachedStrings = new ConcurrentHashMap<>();

  private static final @Nullable List<ResourceBundleControlProvider> providers;

  private static final Control INSTANCE;

  static {
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
   * Formats a given string (provided by resource key) with arguments into a locale specific value.
   *
   * @param pLocale the locale
   * @param pFormatKey the key
   * @param pArgs the arguments
   * @return the formated string
   */
  public static String getFormat(Locale pLocale, MessagesEnum pFormatKey, @Nullable Object @Nullable ... pArgs) {

    ResourceBundle bundle = pFormatKey.getBundle(pLocale);
    String format = bundle.getString(pFormatKey.getCode());
    MessageFormat messageFormat = new MessageFormat(format);
    messageFormat.setLocale(pLocale);
    return messageFormat.format(pArgs);
  }

  public static Map<Locale, String> getAllFormats(MessagesEnum pFormatKey, @Nullable Object @Nullable ... pArgs) {

    /* This is a really expensive lookup, so the getString will be cached */

    Map<Locale, String> map = sCachedStrings.get(pFormatKey.getCode());
    if (map == null) {

      /* Perform the expensive iteration over all possible locales */

      Locale defaultLocale = Locale.getDefault();
      ResourceBundle defaultBundle = pFormatKey.getBundle(defaultLocale);

      Map<Locale, String> newMap = new HashMap<>();
      for (Locale locale : Locale.getAvailableLocales()) {
        try {
          ResourceBundle bundle = pFormatKey.getBundle(locale);
          if ((locale.equals(defaultLocale) == false) && (bundle.equals(defaultBundle) == true)) continue;
          String format = bundle.getString(pFormatKey.getCode());
          newMap.put(locale, format);
        }
        catch (MissingResourceException ex) {
        }
      }
      if ((map = sCachedStrings.putIfAbsent(pFormatKey.getCode(), newMap)) == null) map = newMap;
    }

    Map<Locale, String> resultMap = new HashMap<>();
    for (Map.Entry<Locale, String> pair : map.entrySet()) {
      MessageFormat messageFormat = new MessageFormat(pair.getValue());
      messageFormat.setLocale(pair.getKey());
      resultMap.put(pair.getKey(), messageFormat.format(pArgs));
    }
    return resultMap;
  }

  public static Locale getDefaultLocale() {
    return Locale.getDefault();
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

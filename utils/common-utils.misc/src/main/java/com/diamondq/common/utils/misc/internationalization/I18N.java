package com.diamondq.common.utils.misc.internationalization;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Internationalization helper class
 */
public class I18N {

  private static ConcurrentMap<String, Map<Locale, String>> sCachedStrings = new ConcurrentHashMap<>();

  /**
   * Formats a given string (provided by resource key) with arguments into a locale specific value.
   * 
   * @param pLocale the locale
   * @param pFormatKey the key
   * @param pArgs the arguments
   * @return the formated string
   */
  public static String getFormat(Locale pLocale, MessagesEnum pFormatKey, @Nullable Object @Nullable... pArgs) {

    ResourceBundle bundle = pFormatKey.getBundle(pLocale);
    String format = bundle.getString(pFormatKey.getCode());
    MessageFormat messageFormat = new MessageFormat(format);
    messageFormat.setLocale(pLocale);
    return messageFormat.format(pArgs);
  }

  public static Map<Locale, String> getAllFormats(MessagesEnum pFormatKey, @Nullable Object @Nullable... pArgs) {

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
          if ((locale.equals(defaultLocale) == false) && (bundle.equals(defaultBundle) == true))
            continue;
          String format = bundle.getString(pFormatKey.getCode());
          newMap.put(locale, format);
        }
        catch (MissingResourceException ex) {
        }
      }
      if ((map = sCachedStrings.putIfAbsent(pFormatKey.getCode(), newMap)) == null)
        map = newMap;
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
}

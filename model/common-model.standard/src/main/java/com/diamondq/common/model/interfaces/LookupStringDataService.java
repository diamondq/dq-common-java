package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.Locale;

public interface LookupStringDataService {

  /**
   * Looks up a string
   *
   * @param pPreferredLocale the preferred locale of the string
   * @param pKey the key to lookup
   * @return the result or null if there is no possible match
   */
  @Nullable
  String lookupString(Locale pPreferredLocale, String pKey);

}

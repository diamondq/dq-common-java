package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.TranslatableString;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

public class GenericTranslatableString implements TranslatableString {

  private final Scope mScope;

  private final String mKey;

  public GenericTranslatableString(Scope pScope, String pKey) {
    mScope = pScope;
    mKey = pKey;
  }

  /**
   * @see com.diamondq.common.model.interfaces.TranslatableString#getKey()
   */
  @Override
  public String getKey() {
    return mKey;
  }

  /**
   * @see com.diamondq.common.model.interfaces.TranslatableString#resolve(java.util.Locale, java.lang.Object[])
   */
  @Override
  public String resolve(@Nullable Locale pLocale, @Nullable Object @Nullable ... pArgs) {
    String resourceString = mScope.getToolkit().lookupResourceString(mScope, pLocale, mKey);
    if (resourceString == null) resourceString = mKey;
    String result;
    if ((pArgs == null) || (pArgs.length == 0)) result = resourceString;
    else result = String.format(resourceString, pArgs);
    return result;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mScope, mKey);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj) return true;
    if (pObj == null) return false;
    if (getClass() != pObj.getClass()) return false;
    GenericTranslatableString other = (GenericTranslatableString) pObj;
    return Objects.equals(mScope, other.mScope) && Objects.equals(mKey, other.mKey);
  }
}

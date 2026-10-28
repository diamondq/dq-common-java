package com.diamondq.common.i18n;

import com.diamondq.common.errors.I18NStringAndException;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * This represents a String that has not yet been localized. It is only serializable if all of its params are.
 */
public class I18NString implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public final MessagesEnum message;

  /**
   * The params. This is a private copy of the array passed to the constructor, so it should be treated as read-only.
   */
  public final @Nullable Object @Nullable [] params;

  public I18NString(MessagesEnum pMessage, @Nullable Object @Nullable ... pParams) {
    message = pMessage;
    params = (pParams == null ? null : pParams.clone());
  }

  public I18NStringAndException and(@Nullable Throwable ex) {
    return new I18NStringAndException(this, ex);
  }

  public I18NStringAndException noException() {
    return new I18NStringAndException(this, null);
  }

  /**
   * Localizes this string for the given locale. Any nested I18NString params are localized to the same locale.
   *
   * @param pLocale the locale
   * @return the localized text
   */
  public String format(Locale pLocale) {
    return I18N.getFormat(pLocale, message, params);
  }

  @Override
  public String toString() {
    try {
      return I18N.getFormat(I18N.getDefaultLocale(), message, params);
    }
    catch (MissingResourceException | IllegalArgumentException ex) {
      /* toString must never throw, so fall back to the code and raw params */
      return message.getCode() + (params == null ? "" : Arrays.toString(params));
    }
  }

  @Override
  public boolean equals(@Nullable Object pO) {
    if (this == pO) return true;
    if (pO == null || getClass() != pO.getClass()) return false;
    final I18NString that = (I18NString) pO;
    return message.equals(that.message) && Arrays.deepEquals(params, that.params);
  }

  @Override
  public int hashCode() {
    return 31 * message.hashCode() + Arrays.deepHashCode(params);
  }
}

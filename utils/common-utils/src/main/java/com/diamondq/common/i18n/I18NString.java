package com.diamondq.common.i18n;

import com.diamondq.common.errors.I18NStringAndException;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This represents a String that has not yet been localized
 */
public class I18NString {

  public final MessagesEnum                  message;

  public final @Nullable Object @Nullable [] params;

  public I18NString(MessagesEnum pMessage, @Nullable Object @Nullable... pParams) {
    message = pMessage;
    params = pParams;
  }

  public I18NStringAndException and(@Nullable Throwable ex) {
    return new I18NStringAndException(this, ex);
  }

  public I18NStringAndException noException() {
    return new I18NStringAndException(this, null);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return I18N.getFormat(I18N.getDefaultLocale(), message, params);
  }
}
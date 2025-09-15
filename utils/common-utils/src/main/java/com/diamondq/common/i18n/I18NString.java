package com.diamondq.common.i18n;

import com.diamondq.common.errors.I18NStringAndException;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * This represents a String that has not yet been localized
 */
public class I18NString {

  public final MessagesEnum message;

  public final @Nullable Object @Nullable [] params;

  public I18NString(MessagesEnum pMessage, @Nullable Object @Nullable ... pParams) {
    message = pMessage;
    params = pParams;
  }

  public I18NStringAndException and(@Nullable Throwable ex) {
    return new I18NStringAndException(this, ex);
  }

  public I18NStringAndException noException() {
    return new I18NStringAndException(this, null);
  }

  @Override
  public String toString() {
    return I18N.getFormat(I18N.getDefaultLocale(), message, params);
  }

  @Override
  public boolean equals(@Nullable Object pO) {
    if (this == pO) return true;
    if (pO == null || getClass() != pO.getClass()) return false;
    final I18NString that = (I18NString) pO;
    return message.equals(that.message) && Arrays.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(message);
    result = 31 * result + Arrays.hashCode(params);
    return result;
  }
}

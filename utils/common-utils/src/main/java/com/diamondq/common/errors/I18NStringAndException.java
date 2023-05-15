package com.diamondq.common.errors;

import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class I18NStringAndException extends I18NString {

  private final @Nullable Throwable mThrowable;

  public I18NStringAndException(I18NString pString, @Nullable Throwable pEx) {
    super(pString.message, pString.params);
    mThrowable = pEx;
  }

  public I18NStringAndException(MessagesEnum pMessage, @Nullable Throwable pThrowable,
    @Nullable Object @Nullable ... pParams) {
    super(pMessage, pParams);
    mThrowable = pThrowable;
  }

  public I18NString getMessage() {
    return this;
  }

  public @Nullable Throwable getThrowable() {
    return mThrowable;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    if (mThrowable != null) {
      try (StringWriter sw = new StringWriter()) {
        try (PrintWriter pw = new PrintWriter(sw)) {
          mThrowable.printStackTrace(pw);
        }
        sw.flush();
        sb.append("\n").append(sw);
      }
      catch (IOException ignored) {
      }
    }
    return sb.toString();
  }

  @Override
  public boolean equals(@Nullable Object pO) {
    if (this == pO) return true;
    if (pO == null || getClass() != pO.getClass()) return false;
    if (!super.equals(pO)) return false;
    final I18NStringAndException that = (I18NStringAndException) pO;
    return Objects.equals(mThrowable, that.mThrowable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), mThrowable);
  }
}

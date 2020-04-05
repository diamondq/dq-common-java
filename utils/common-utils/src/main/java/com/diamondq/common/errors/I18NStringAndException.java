package com.diamondq.common.errors;

import com.diamondq.common.i18n.I18NString;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.checkerframework.checker.nullness.qual.Nullable;

public class I18NStringAndException {

  private final I18NString          mMessage;

  private final @Nullable Throwable mThrowable;

  public I18NStringAndException(I18NString pString, @Nullable Throwable pEx) {
    mMessage = pString;
    mThrowable = pEx;
  }

  public I18NString getMessage() {
    return mMessage;
  }

  public @Nullable Throwable getThrowable() {
    return mThrowable;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(mMessage.toString());
    if (mThrowable != null) {
      try (StringWriter sw = new StringWriter()) {
        try (PrintWriter pw = new PrintWriter(sw)) {
          mThrowable.printStackTrace(pw);
        }
        sw.flush();
        sb.append("\n").append(sw.toString());
      }
      catch (IOException ex) {
      }

    }
    return sb.toString();
  }
}

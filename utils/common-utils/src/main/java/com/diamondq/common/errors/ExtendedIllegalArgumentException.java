package com.diamondq.common.errors;

import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ExtendedIllegalArgumentException extends ExtendedRuntimeException {

  private static final long serialVersionUID = -1846753211536627551L;

  public ExtendedIllegalArgumentException(I18NString pString) {
    super(pString);
  }

  public ExtendedIllegalArgumentException(MessagesEnum pCode, @Nullable Object @Nullable... pParams) {
    super(pCode, pParams);
  }

  public ExtendedIllegalArgumentException(Throwable pCause, MessagesEnum pCode, @Nullable Object @Nullable... pParams) {
    super(pCause, pCode, pParams);
  }
}

package com.diamondq.common.errors;

import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;
import org.jetbrains.annotations.Nullable;

public class ExtendedIllegalStateException extends ExtendedRuntimeException {

  private static final long serialVersionUID = -6076912096343098937L;

  public ExtendedIllegalStateException(I18NString pString) {
    super(pString);
  }

  public ExtendedIllegalStateException(MessagesEnum pCode, @Nullable Object @Nullable ... pParams) {
    super(pCode, pParams);
  }

  public ExtendedIllegalStateException(Throwable pCause, MessagesEnum pCode, @Nullable Object @Nullable ... pParams) {
    super(pCause, pCode, pParams);
  }
}

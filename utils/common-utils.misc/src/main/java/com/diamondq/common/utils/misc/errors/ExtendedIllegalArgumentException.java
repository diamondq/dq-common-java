package com.diamondq.common.utils.misc.errors;

import com.diamondq.common.utils.misc.internationalization.I18N;
import com.diamondq.common.utils.misc.internationalization.MessagesEnum;

import java.util.Arrays;
import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ExtendedIllegalArgumentException extends IllegalArgumentException {

  private static final long          serialVersionUID = 4687502775610417335L;

  protected final MessagesEnum       mCode;

  protected final @Nullable Object[] mParams;

  public ExtendedIllegalArgumentException(MessagesEnum pCode, @Nullable Object @Nullable... pParams) {
    super();
    mCode = pCode;
    if (pParams == null)
      mParams = new String[0];
    else
      mParams = Arrays.copyOf(pParams, pParams.length);
  }

  public ExtendedIllegalArgumentException(Throwable pCause, MessagesEnum pCode, @Nullable Object @Nullable... pParams) {
    super(null, pCause);
    mCode = pCode;
    if (pParams == null)
      mParams = new String[0];
    else
      mParams = Arrays.copyOf(pParams, pParams.length);
  }

  public MessagesEnum getCode() {
    return mCode;
  }

  /**
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public String getMessage() {
    return getLocalizedMessage(I18N.getDefaultLocale());
  }

  /**
   * @see java.lang.Throwable#getLocalizedMessage()
   */
  @Override
  public String getLocalizedMessage() {
    return getLocalizedMessage(I18N.getDefaultLocale());
  }

  /**
   * Returns the error message localized for the given locale
   * 
   * @param pLocale the locale
   * @return the message
   */
  public String getLocalizedMessage(Locale pLocale) {
    return I18N.getFormat(pLocale, mCode, mParams);
  }
}

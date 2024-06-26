package com.diamondq.common.errors;

import com.diamondq.common.i18n.I18N;
import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

public class ExtendedRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 4769451435817098743L;

  protected final MessagesEnum mCode;

  protected final @Nullable Object[] mParams;

  public ExtendedRuntimeException(I18NStringAndException pStrAndEx) {
    super(pStrAndEx.getThrowable());
    I18NString message = pStrAndEx.getMessage();
    mCode = message.message;
    @Nullable Object @Nullable [] params = message.params;
    if (params == null) mParams = new String[0];
    else mParams = Arrays.copyOf(params, params.length);
  }

  public ExtendedRuntimeException(I18NString pString) {
    super();
    mCode = pString.message;
    @Nullable Object @Nullable [] params = pString.params;
    if (params == null) mParams = new String[0];
    else mParams = Arrays.copyOf(params, params.length);
  }

  public ExtendedRuntimeException(MessagesEnum pCode, @Nullable Object @Nullable ... pParams) {
    super();
    mCode = pCode;
    if (pParams == null) mParams = new String[0];
    else mParams = Arrays.copyOf(pParams, pParams.length);
  }

  public ExtendedRuntimeException(Throwable pCause, MessagesEnum pCode, @Nullable Object @Nullable ... pParams) {
    super(null, pCause);
    mCode = pCode;
    if (pParams == null) mParams = new String[0];
    else mParams = Arrays.copyOf(pParams, pParams.length);
  }

  public MessagesEnum getCode() {
    return mCode;
  }

  public @Nullable Object[] getParams() {
    return mParams;
  }

  /**
   * @see java.lang.Throwable#getMessage()
   */
  @Override
  public @Nullable String getMessage() {
    return getLocalizedMessage(I18N.getDefaultLocale());
  }

  /**
   * @see java.lang.Throwable#getLocalizedMessage()
   */
  @Override
  public @Nullable String getLocalizedMessage() {
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

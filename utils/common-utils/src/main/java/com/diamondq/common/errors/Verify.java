package com.diamondq.common.errors;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class Verify {

  public static <T extends @Nullable Object> @NonNull T notNullArg(@Nullable T pObj, MessagesEnum pMessage,
    @Nullable Object @Nullable ... pArgs) {
    if (pObj != null) return pObj;

    throw new ExtendedIllegalArgumentException(pMessage, pArgs);
  }

  public static <T> T notNullArg(@Nullable T pObj, I18NString pMessage) {
    if (pObj != null) return pObj;

    throw new ExtendedIllegalArgumentException(pMessage);
  }

  /**
   * Quick assertion that converts a Nullable object into a NonNullable object (or throws an exception)
   *
   * @param pObj the object that shouldn't be null (but could be)
   * @return the object guaranteed to be non-nullable
   */
  public static <T> T notNull(@Nullable T pObj) {
    if (pObj != null) return pObj;
    throw new ExtendedIllegalArgumentException(UtilMessages.VERIFY_NULL);
  }

  public static void throwRuntimeException(@Nullable Throwable pThrowable) {
    throw getRuntimeException(pThrowable);
  }

  public static String notEmptyArg(@Nullable String pObj, MessagesEnum pMessage, @Nullable Object @Nullable ... pArgs) {
    if ((pObj != null) && (!pObj.isEmpty())) return pObj;

    throw new ExtendedIllegalArgumentException(pMessage, pArgs);
  }

  public static RuntimeException getRuntimeException(@Nullable Throwable pThrowable) {
    if (pThrowable == null) return new ExtendedIllegalArgumentException(UtilMessages.VERIFY_NULL);
    if (pThrowable instanceof RuntimeException) return (RuntimeException) pThrowable;
    throw new RuntimeException(pThrowable);
  }
}

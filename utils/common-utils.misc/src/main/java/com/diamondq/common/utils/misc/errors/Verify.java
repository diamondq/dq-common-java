package com.diamondq.common.utils.misc.errors;

import com.diamondq.common.utils.misc.MiscMessages;
import com.diamondq.common.utils.misc.internationalization.MessagesEnum;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Verify {

  public static <T> @NonNull T notNullArg(@Nullable T pObj, MessagesEnum pMessage,
    @Nullable Object @Nullable... pArgs) {
    if (pObj != null)
      return pObj;

    throw new ExtendedIllegalArgumentException(pMessage, pArgs);
  }

  /**
   * Quick assertion that converts a Nullable object into a NonNullable object (or throws an exception)
   * 
   * @param pObj the object that shouldn't be null (but could be)
   * @return the object guaranteed to be non-nullable
   */
  public static <T> @NonNull T notNull(@Nullable T pObj) {
    if (pObj != null)
      return pObj;
    throw new ExtendedIllegalArgumentException(MiscMessages.VERIFY_NULL);
  }

  public static void throwRuntimeException(@Nullable Throwable pThrowable) {
    throw getRuntimeException(pThrowable);
  }

  public static String notEmptyArg(@Nullable String pObj, MessagesEnum pMessage, @Nullable Object @Nullable... pArgs) {
    if ((pObj != null) && (pObj.isEmpty() == false))
      return pObj;

    throw new ExtendedIllegalArgumentException(pMessage, pArgs);
  }

  public static RuntimeException getRuntimeException(@Nullable Throwable pThrowable) {
    if (pThrowable == null)
      return new ExtendedIllegalArgumentException(MiscMessages.VERIFY_NULL);
    if (pThrowable instanceof RuntimeException)
      return (RuntimeException) pThrowable;
    throw new RuntimeException(pThrowable);
  }
}

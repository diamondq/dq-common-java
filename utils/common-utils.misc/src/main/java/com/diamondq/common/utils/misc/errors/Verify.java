package com.diamondq.common.utils.misc.errors;

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

  public static String notEmptyArg(@Nullable String pObj, MessagesEnum pMessage, @Nullable Object @Nullable... pArgs) {
    if ((pObj != null) && (pObj.isEmpty() == false))
      return pObj;

    throw new ExtendedIllegalArgumentException(pMessage, pArgs);
  }
}

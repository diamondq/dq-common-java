package com.diamondq.common.utils.misc.errors;

import com.diamondq.common.utils.misc.internationalization.MessagesEnum;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Verify {

	public static void notNullArg(@Nullable Object pObj, MessagesEnum pMessage, @Nullable Object @Nullable... pArgs) {
		if (pObj != null)
			return;

		throw new ExtendedIllegalArgumentException(pMessage, pArgs);
	}

	public static void notEmptyArg(@Nullable String pObj, MessagesEnum pMessage, @Nullable Object @Nullable... pArgs) {
		if ((pObj != null) && (pObj.isEmpty() == false))
			return;

		throw new ExtendedIllegalArgumentException(pMessage, pArgs);
	}
}

package com.diamondq.common.model.interfaces;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface LookupStringDataService {

	/**
	 * Looks up a string
	 *
	 * @param pPreferredLocale the preferred locale of the string
	 * @param pKey the key to lookup
	 * @return the result or null if there is no possible match
	 */
	public @Nullable String lookupString(Locale pPreferredLocale, String pKey);

}

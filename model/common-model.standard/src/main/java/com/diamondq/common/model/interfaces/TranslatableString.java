package com.diamondq.common.model.interfaces;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface TranslatableString {

	/**
	 * Returns the key for this translatable String
	 * 
	 * @return the key
	 */
	public String getKey();

	/**
	 * Resolves a translatable string to the actual localized string.
	 * 
	 * @param pLocale the locale to use or null to use the 'default' locale (toolkit implementation specific)
	 * @param pArgs the arguments to be used to inject into the string
	 * @return the actual human readable string
	 */
	public String resolve(@Nullable Locale pLocale, @Nullable Object @Nullable... pArgs);

}

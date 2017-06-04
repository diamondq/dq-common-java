package com.diamondq.common.model.interfaces;

import java.util.Locale;

import javax.annotation.Nonnull;

public interface TranslatableString {

	/**
	 * Returns the key for this translatable String
	 * 
	 * @return the key
	 */
	@Nonnull
	public String getKey();

	/**
	 * Resolves a translatable string to the actual localized string.
	 * 
	 * @param pLocale the locale to use or null to use the 'default' locale (toolkit implementation specific)
	 * @param pArgs the arguments to be used to inject into the string
	 * @return the actual human readable string
	 */
	public String resolve(Locale pLocale, Object... pArgs);

}

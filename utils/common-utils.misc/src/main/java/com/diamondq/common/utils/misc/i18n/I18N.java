package com.diamondq.common.utils.misc.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Internationalization helper class
 */
public class I18N {

	/**
	 * Formats a given string (provided by resource key) with arguments into a locale specific value.
	 * 
	 * @param pLocale the locale
	 * @param pFormatKey the key
	 * @param pArgs the arguments
	 * @return the formated string
	 */
	public static String getFormat(Locale pLocale, MessagesEnum pFormatKey, @Nullable Object @Nullable... pArgs) {

		ResourceBundle bundle = pFormatKey.getBundle(pLocale);
		String format = bundle.getString(pFormatKey.getCode());
		return MessageFormat.format(format, pArgs);

	}

	public static Locale getDefaultLocale() {
		return Locale.getDefault();
	}
}

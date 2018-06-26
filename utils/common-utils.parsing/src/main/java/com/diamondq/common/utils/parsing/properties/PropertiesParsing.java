package com.diamondq.common.utils.parsing.properties;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PropertiesParsing {

	public static boolean getNonNullBoolean(Map<String, Object> pProps, String pKey, Boolean pDefault) {
		Object propObj = pProps.get(pKey);
		Boolean bool;
		if (propObj == null)
			bool = pDefault;
		else
			bool = Boolean.parseBoolean(propObj.toString());
		return bool;
	}

	public static @Nullable Boolean getNullableBoolean(Map<String, Object> pProps, String pKey) {
		Object propObj = pProps.get(pKey);
		@Nullable
		Boolean bool;
		if (propObj == null)
			bool = null;
		else
			bool = Boolean.parseBoolean(propObj.toString());
		return bool;
	}

	public static String getNonNullString(Map<String, Object> pProps, String pKey, String pDefault) {
		Object propObj = pProps.get(pKey);
		String val;
		if (propObj == null)
			val = pDefault;
		else
			val = propObj.toString();
		return val;
	}

	public static @Nullable String getNullableString(Map<String, Object> pProps, String pKey) {
		Object propObj = pProps.get(pKey);
		@Nullable
		String val;
		if (propObj == null)
			val = null;
		else
			val = propObj.toString();
		return val;
	}

	public static int getNonNullInt(Map<String, Object> pProps, String pKey, int pDefault) {
		Object propObj = pProps.get(pKey);
		int val;
		if (propObj == null)
			val = pDefault;
		else
			val = Integer.parseInt(propObj.toString());
		return val;
	}

	public static @Nullable Integer getNullableInt(Map<String, Object> pProps, String pKey) {
		Object propObj = pProps.get(pKey);
		@Nullable
		Integer val;
		if (propObj == null)
			val = null;
		else
			val = Integer.parseInt(propObj.toString());
		return val;
	}

	public static long getNonNullLong(Map<String, Object> pProps, String pKey, long pDefault) {
		Object propObj = pProps.get(pKey);
		long val;
		if (propObj == null)
			val = pDefault;
		else
			val = Long.parseLong(propObj.toString());
		return val;
	}

	public static @Nullable Long getNullableLong(Map<String, Object> pProps, String pKey) {
		Object propObj = pProps.get(pKey);
		@Nullable
		Long val;
		if (propObj == null)
			val = null;
		else
			val = Long.parseLong(propObj.toString());
		return val;
	}

	public static @NonNull String[] getStringArray(Map<String, Object> pProps, String pKey) {
		Object propObj = pProps.get(pKey);
		if (propObj == null)
			return new String[0];
		String val = propObj.toString();
		@NonNull
		String[] results = val.split(",");
		return results;
	}

	public static void removeAll(Map<String, Object> pProps, String... pKeys) {
		for (String key : pKeys)
			pProps.remove(key);
	}
}

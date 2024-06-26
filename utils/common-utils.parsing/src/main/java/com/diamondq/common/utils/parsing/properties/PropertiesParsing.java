package com.diamondq.common.utils.parsing.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Dictionary;
import java.util.Map;

public class PropertiesParsing {

  public static boolean getNonNullBoolean(Map<String, Object> pProps, String pKey, Boolean pDefault) {
    Object propObj = pProps.get(pKey);
    Boolean bool;
    if (propObj == null) bool = pDefault;
    else bool = Boolean.parseBoolean(propObj.toString());
    return bool;
  }

  public static boolean getNonNullBoolean(Dictionary<String, Object> pProps, String pKey, Boolean pDefault) {
    Object propObj = pProps.get(pKey);
    Boolean bool;
    if (propObj == null) bool = pDefault;
    else bool = Boolean.parseBoolean(propObj.toString());
    return bool;
  }

  public static @Nullable Boolean getNullableBoolean(Map<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable Boolean bool;
    if (propObj == null) bool = null;
    else bool = Boolean.parseBoolean(propObj.toString());
    return bool;
  }

  public static @Nullable Boolean getNullableBoolean(Dictionary<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable Boolean bool;
    if (propObj == null) bool = null;
    else bool = Boolean.parseBoolean(propObj.toString());
    return bool;
  }

  public static String getNonNullString(Map<String, Object> pProps, String pKey, String pDefault) {
    Object propObj = pProps.get(pKey);
    String val;
    if (propObj == null) val = pDefault;
    else val = propObj.toString();
    return val;
  }

  public static String getNonNullString(Dictionary<String, Object> pProps, String pKey, String pDefault) {
    Object propObj = pProps.get(pKey);
    String val;
    if (propObj == null) val = pDefault;
    else val = propObj.toString();
    return val;
  }

  public static @Nullable String getNullableString(Map<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable String val;
    if (propObj == null) val = null;
    else val = propObj.toString();
    return val;
  }

  public static @Nullable String getNullableString(Dictionary<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable String val;
    if (propObj == null) val = null;
    else val = propObj.toString();
    return val;
  }

  public static int getNonNullInt(Map<String, Object> pProps, String pKey, int pDefault) {
    Object propObj = pProps.get(pKey);
    int val;
    if (propObj == null) val = pDefault;
    else val = Integer.parseInt(propObj.toString());
    return val;
  }

  public static int getNonNullInt(Dictionary<String, Object> pProps, String pKey, int pDefault) {
    Object propObj = pProps.get(pKey);
    int val;
    if (propObj == null) val = pDefault;
    else val = Integer.parseInt(propObj.toString());
    return val;
  }

  public static @Nullable Integer getNullableInt(Map<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable Integer val;
    if (propObj == null) val = null;
    else val = Integer.parseInt(propObj.toString());
    return val;
  }

  public static @Nullable Integer getNullableInt(Dictionary<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable Integer val;
    if (propObj == null) val = null;
    else val = Integer.parseInt(propObj.toString());
    return val;
  }

  public static long getNonNullLong(Map<String, Object> pProps, String pKey, long pDefault) {
    Object propObj = pProps.get(pKey);
    long val;
    if (propObj == null) val = pDefault;
    else val = Long.parseLong(propObj.toString());
    return val;
  }

  public static long getNonNullLong(Dictionary<String, Object> pProps, String pKey, long pDefault) {
    Object propObj = pProps.get(pKey);
    long val;
    if (propObj == null) val = pDefault;
    else val = Long.parseLong(propObj.toString());
    return val;
  }

  public static @Nullable Long getNullableLong(Map<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable Long val;
    if (propObj == null) val = null;
    else val = Long.parseLong(propObj.toString());
    return val;
  }

  public static @Nullable Long getNullableLong(Dictionary<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    @Nullable Long val;
    if (propObj == null) val = null;
    else val = Long.parseLong(propObj.toString());
    return val;
  }

  public static @NotNull String[] getStringArray(Map<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    if (propObj == null) return new String[0];
    String val = propObj.toString();
    @NotNull String[] results = val.split(",");
    return results;
  }

  public static @NotNull String[] getStringArray(Dictionary<String, Object> pProps, String pKey) {
    Object propObj = pProps.get(pKey);
    if (propObj == null) return new String[0];
    String val = propObj.toString();
    @NotNull String[] results = val.split(",");
    return results;
  }

  public static void removeAll(Map<String, Object> pProps, String... pKeys) {
    for (String key : pKeys)
      pProps.remove(key);
  }

  public static void removeAll(Dictionary<String, Object> pProps, String... pKeys) {
    for (String key : pKeys)
      pProps.remove(key);
  }
}

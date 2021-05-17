package com.diamondq.common.types;

import com.diamondq.common.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class Types {

  private Types() {
  }

  public static final Type MAP_STRING_TO_STRING = new TypeReference<Map<String, String>>() {
                                                }.getType();

  public static final Type LIST_STRING          = new TypeReference<List<String>>() {
                                                }.getType();

  public static final Type LIST_LONG            = new TypeReference<List<Long>>() {
                                                }.getType();

}

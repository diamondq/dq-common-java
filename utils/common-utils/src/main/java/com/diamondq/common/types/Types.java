package com.diamondq.common.types;

import com.diamondq.common.TypeReference;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class Types {

  private Types() {
  }

  public static final Type MAP_STRING_TO_STRING = new TypeReference<Map<String, String>>() {
  }.getType();
  
}

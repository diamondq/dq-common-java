package com.diamondq.common.bpm.camunda;

import org.camunda.model.VariableValueDto;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class TypesUtil {

  private TypesUtil() {
  }

  public static @Nullable Map<String, VariableValueDto> toVarMap(Map<String, Object> pVars) {
    if (pVars.isEmpty() == true) return null;
    Map<String, VariableValueDto> result = new HashMap<>();
    for (Map.Entry<String, Object> pair : pVars.entrySet()) {
      result.put(pair.getKey(), new VariableValueDto(pair.getValue(), getType(pair.getValue()), null));
    }
    return result;
  }

  public static String getType(@Nullable Object pValue) {
    if (pValue == null) return "null";
    if (pValue instanceof String) return "string";
    else if (pValue instanceof Boolean) return "boolean";
    else if (pValue instanceof byte[]) return "bytes";
    else if (pValue instanceof Short) return "short";
    else if (pValue instanceof Integer) return "integer";
    else if (pValue instanceof Long) return "long";
    else if (pValue instanceof Double) return "double";
    else if (pValue instanceof Date) return "date";
    else throw new UnsupportedOperationException();
  }
}

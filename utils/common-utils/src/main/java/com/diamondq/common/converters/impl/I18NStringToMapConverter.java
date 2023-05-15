package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.AbstractConverter;
import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.types.Types;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class I18NStringToMapConverter extends AbstractConverter<I18NString, Map<String, @Nullable Object>> {

  @Inject
  public I18NStringToMapConverter() {
    super(Types.I18NSTRING, Types.MAP_OF_STRING_TO_NULLABLE_OBJECT, null);
  }

  private @Nullable Object recursiveWrite(@Nullable Object pObj) {
    if (pObj == null) return null;
    else {
      if (pObj instanceof Boolean || pObj instanceof String || pObj instanceof Number) return pObj;
      if (pObj instanceof Map) {
        @SuppressWarnings("unchecked") Map<Object, @Nullable Object> map = (Map<Object, @Nullable Object>) pObj;
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<Object, @Nullable Object> pair : map.entrySet()) {
          map.put(pair.getKey().toString(), recursiveWrite(pair.getValue()));
        }
        return result;
      }
      if (pObj instanceof Collection) {
        @SuppressWarnings("unchecked") Collection<@Nullable Object> coll = (Collection<@Nullable Object>) pObj;
        List<@Nullable Object> result = new ArrayList<>();
        for (@Nullable Object elem : coll)
          result.add(recursiveWrite(elem));
        return result;
      }
      throw new IllegalArgumentException(
        "Unable to convert " + pObj.getClass().getName() + " into something that can be stored in a JSON object");
    }
  }

  @Override
  public Map<String, @Nullable Object> convert(I18NString pInput) {
    Map<String, @Nullable Object> result = new HashMap<>();
    if (pInput.message instanceof Enum<?>) {
      Enum<?> messageEnum = (Enum<?>) pInput.message;
      String enumName = messageEnum.name();
      final Class<?> enumClass = messageEnum.getDeclaringClass();
      result.put("n", enumName);
      result.put("c", enumClass.getName());
    } else throw new IllegalArgumentException(
      "Unable to convert a MessageEnum of type " + pInput.message.getClass().getName() + " to a Map");
    int size = pInput.params == null ? -1 : pInput.params.length;
    result.put("s", size);
    if (pInput.params != null) {
      List<@Nullable Object> paramList = new ArrayList<>();
      for (@Nullable Object param : pInput.params) {
        paramList.add(recursiveWrite(param));
      }
      result.put("p", paramList);
    }
    return result;
  }
}

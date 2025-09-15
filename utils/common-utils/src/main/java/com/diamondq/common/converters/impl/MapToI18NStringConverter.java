package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.AbstractConverter;
import com.diamondq.common.i18n.I18NString;
import com.diamondq.common.i18n.MessagesEnum;
import com.diamondq.common.types.Types;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class MapToI18NStringConverter extends AbstractConverter<Map<String, @Nullable Object>, I18NString> {

  @Inject
  public MapToI18NStringConverter() {
    super(Types.MAP_OF_STRING_TO_NULLABLE_OBJECT, Types.I18NSTRING, null);
  }

  @Override
  public I18NString convert(Map<String, @Nullable Object> pInput) {
    String enumName = Objects.requireNonNull((String) pInput.get("n"));
    String enumClassName = Objects.requireNonNull((String) pInput.get("c"));
    Object enumValue = getEnum(enumName, enumClassName);
    if (!(enumValue instanceof final MessagesEnum messageEnum))
      throw new IllegalArgumentException("Unable to case " + enumValue.getClass().getName() + " to MessageEnum");

    /* Get the params */

    @Nullable Object[] params;
    int size = Objects.requireNonNull((Integer) pInput.get("s"));
    if (size < 0) params = null;
    else if (size == 0) params = new Object[0];
    else {
      params = new Object[size];
      @SuppressWarnings(
        "unchecked") List<@Nullable Object> paramList = Objects.requireNonNull((List<@Nullable Object>) pInput.get("p"));
      if (paramList.size() != size) throw new IllegalArgumentException();
      for (int i = 0; i < size; i++) {
        Object param = paramList.get(i);
        params[i] = param;
      }
    }
    return new I18NString(messageEnum, params);
  }

  private <T extends Enum<T>> T getEnum(String enumName, String enumClassName) {
    try {
      @SuppressWarnings("unchecked") Class<T> enumClass = (Class<T>) Class.forName(enumClassName);
      return Enum.valueOf(enumClass, enumName);
    }
    catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }
}

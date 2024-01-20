package com.diamondq.common.converters.impl;

import com.diamondq.common.TypeReference;
import com.diamondq.common.converters.AbstractConverter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.types.Types;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public class MapToStringConverter extends AbstractConverter<Map<?, ?>, String> {

  private final Provider<ConverterManager> mConverterManager;

  private static final TypeReference<Map<?, ?>> sMAP_OF_WILD = new TypeReference<Map<?, ?>>() {
  };

  @Inject
  public MapToStringConverter(Provider<ConverterManager> pConverterManager) {
    super(sMAP_OF_WILD, Types.STRING, null);
    mConverterManager = pConverterManager;
  }

  @Override
  public String convert(Map<?, ?> pInput) {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<?, ?> obj : pInput.entrySet()) {
      if (first) first = false;
      else sb.append(',');
      sb.append(mConverterManager.get().convert(obj.getKey(), String.class));
      sb.append("=");
      sb.append(mConverterManager.get().convert(obj.getValue(), String.class));
    }
    sb.append("}");
    return sb.toString();
  }
}

package com.diamondq.common.converters.impl;

import com.diamondq.common.TypeReference;
import com.diamondq.common.converters.AbstractConverter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.types.Types;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.Collection;

@Singleton
public class CollectionToStringConverter extends AbstractConverter<Collection<?>, String> {

  private final Provider<ConverterManager> mConverterManager;

  private static final TypeReference<Collection<?>> sCOLLECTION_OF_WILD = new TypeReference<Collection<?>>() {
  };

  @Inject
  public CollectionToStringConverter(Provider<ConverterManager> pConverterManager) {
    super(sCOLLECTION_OF_WILD, Types.STRING, null);
    mConverterManager = pConverterManager;
  }

  @Override
  public String convert(Collection<?> pInput) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object obj : pInput) {
      if (first) first = false;
      else sb.append(',');
      sb.append(mConverterManager.get().convert(obj, String.class));
    }
    return sb.toString();
  }
}

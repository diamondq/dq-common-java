package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.AbstractConverter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.types.Types;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class OptionalToStringConverter extends AbstractConverter<Optional<?>, @Nullable String> {

  private final Provider<ConverterManager> mConverterManager;

  @Inject
  public OptionalToStringConverter(Provider<ConverterManager> pConverterManager) {
    super(Types.OPTIONAL_OF_WILD, Types.STRING, null);
    mConverterManager = pConverterManager;
  }

  @Override
  public @Nullable String convert(Optional<?> pInput) {
    return pInput.map(pO -> mConverterManager.get().convert(pO, String.class)).orElse(null);
  }
}

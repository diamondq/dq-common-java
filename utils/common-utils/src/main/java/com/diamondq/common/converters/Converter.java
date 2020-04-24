package com.diamondq.common.converters;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Converter {

  public Class<?> getInputClass();

  public Class<?> getOutputClass();

  public @Nullable Object convert(Object pInput);
}

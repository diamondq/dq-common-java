package com.diamondq.common.converters;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

public interface Converter<I extends @Nullable Object, O extends @Nullable Object> {

  /**
   * Returns a group name to group a set of converters together
   *
   * @return the optional name. Null represents the default group
   */
  @Nullable
  String getGroupName();

  /**
   * Returns the type (Class, ParameterizedType, etc.) that this convert takes as an input
   *
   * @return the input type
   */
  Type getInputType();

  /**
   * Returns the type (Class, ParameterizedType, etc.) that this convert returns as an output
   *
   * @return the output type
   */
  Type getOutputType();

  /**
   * Converts an input to an output
   *
   * @param pInput the input
   * @return the output
   */
  O convert(I pInput);
}

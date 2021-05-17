package com.diamondq.common.converters;

import java.lang.reflect.Type;

public interface Converter<I, O> {

  /**
   * Returns the type (Class, ParameterizedType, etc) that this convert takes as an input
   * 
   * @return the input type
   */
  public Type getInputType();

  /**
   * Returns the type (Class, ParameterizedType, etc) that this convert returns as an output
   * 
   * @return the output type
   */
  public Type getOutputType();

  /**
   * Converts an input to an output
   * 
   * @param pInput the input
   * @return the output
   */
  public O convert(I pInput);
}

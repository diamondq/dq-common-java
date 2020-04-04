package com.diamondq.common.converters;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ConverterManager {

  /**
   * Converts an output from an input type to an output type
   * 
   * @param pInput the input object
   * @param pOutputClass the requested output class
   * @return the object
   */
  public <I, O> O convert(@NonNull I pInput, Class<O> pOutputClass);
}

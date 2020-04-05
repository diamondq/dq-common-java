package com.diamondq.common.converters;

import com.diamondq.common.errors.ExtendedIllegalArgumentException;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ConverterManager {

  /**
   * Converts an output from an input type to an output type
   * 
   * @param pInput the input object
   * @param pOutputClass the requested output class
   * @return the object
   * @throws ExtendedIllegalArgumentException if there is no possible converter, then it will throw an exception with a
   *           code of UtilMessages.CONVERTERMANAGER_NO_MATCH
   */
  public <I, O> O convert(@NonNull I pInput, Class<O> pOutputClass);
}

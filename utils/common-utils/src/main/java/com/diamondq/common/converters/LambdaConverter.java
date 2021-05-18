package com.diamondq.common.converters;

import com.diamondq.common.TypeReference;

import java.util.function.Function;

public class LambdaConverter<I, O> extends AbstractConverter<I, O> {

  protected final Function<I, O> mConverter;

  public LambdaConverter(Class<I> pInputClass, Class<O> pOutputClass, Function<I, O> pConverter) {
    super(pInputClass, pOutputClass);
    mConverter = pConverter;
  }

  public LambdaConverter(TypeReference<I> pInputClass, TypeReference<O> pOutputClass, Function<I, O> pConverter) {
    super(pInputClass, pOutputClass);
    mConverter = pConverter;
  }

  /**
   * @see com.diamondq.common.converters.Converter#convert(java.lang.Object)
   */
  @Override
  public O convert(I pInput) {
    if (mInputType instanceof Class)
      if (((Class<?>) mInputType).isInstance(pInput) == false)
        throw new IllegalArgumentException(
          "The input which is a " + (pInput == null ? "(null)" : pInput.getClass().toString())
            + " was expected to be a " + mInputType.toString());
    return mConverter.apply(pInput);
  }
}

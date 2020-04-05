package com.diamondq.common.converters;

import java.util.function.Function;

public class LambdaConverter<I, O> extends AbstractConverter<I, O> {

  protected final Function<I, O> mConverter;

  public LambdaConverter(Class<I> pInputClass, Class<O> pOutputClass, Function<I, O> pConverter) {
    super(pInputClass, pOutputClass);
    mConverter = pConverter;
  }

  /**
   * @see com.diamondq.common.converters.Converter#convert(java.lang.Object)
   */
  @Override
  public O convert(I pInput) {
    return mConverter.apply(pInput);
  }
}

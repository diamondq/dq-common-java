package com.diamondq.common.converters;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LambdaConverter<@NonNull I, O> extends AbstractConverter<I, O> {

  protected final Function<I, O> mConverter;

  public LambdaConverter(Class<I> pInputClass, Class<O> pOutputClass, Function<I, O> pConverter) {
    super(pInputClass, pOutputClass);
    mConverter = pConverter;
  }

  /**
   * @see com.diamondq.common.converters.Converter#convert(java.lang.Object)
   */
  @Override
  public @Nullable Object convert(Object pInput) {
    if (mInputClass.isInstance(pInput) == false)
      throw new IllegalArgumentException();
    @SuppressWarnings("unchecked")
    I input = (I) pInput;
    return mConverter.apply(input);
  }
}

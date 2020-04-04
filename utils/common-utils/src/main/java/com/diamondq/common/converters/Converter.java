package com.diamondq.common.converters;

public interface Converter<I, O> {

  public Class<I> getInputClass();

  public Class<O> getOutputClass();

  public O convert(I pInput);
}

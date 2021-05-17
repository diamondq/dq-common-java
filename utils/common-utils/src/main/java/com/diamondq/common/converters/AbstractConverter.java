package com.diamondq.common.converters;

import java.lang.reflect.Type;

public abstract class AbstractConverter<I, O> implements Converter<I, O> {

  protected final Type mInputType;

  protected final Type mOutputType;

  protected AbstractConverter(Class<I> pInputClass, Class<O> pOutputClass) {
    mInputType = pInputClass;
    mOutputType = pOutputClass;
  }

  protected AbstractConverter(Type pInputType, Type pOutputType) {
    mInputType = pInputType;
    mOutputType = pOutputType;
  }

  /**
   * @see com.diamondq.common.converters.Converter#getInputType()
   */
  @Override
  public Type getInputType() {
    return mInputType;
  }

  /**
   * @see com.diamondq.common.converters.Converter#getOutputType()
   */
  @Override
  public Type getOutputType() {
    return mOutputType;
  }
}

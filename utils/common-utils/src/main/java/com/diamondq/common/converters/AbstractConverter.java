package com.diamondq.common.converters;

public abstract class AbstractConverter<I, O> implements Converter {

  protected final Class<I> mInputClass;

  protected final Class<O> mOutputClass;

  public AbstractConverter(Class<I> pInputClass, Class<O> pOutputClass) {
    mInputClass = pInputClass;
    mOutputClass = pOutputClass;
  }

  /**
   * @see com.diamondq.common.converters.Converter#getInputClass()
   */
  @Override
  public Class<?> getInputClass() {
    return mInputClass;
  }

  /**
   * @see com.diamondq.common.converters.Converter#getOutputClass()
   */
  @Override
  public Class<?> getOutputClass() {
    return mOutputClass;
  }
}

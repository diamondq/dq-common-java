package com.diamondq.common.converters;

import com.diamondq.common.TypeReference;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public abstract class AbstractConverter<I, O> implements Converter<I, O> {

  protected final @Nullable String mGroupName;

  protected final Type mInputType;

  protected final Type mOutputType;

  protected AbstractConverter(Class<I> pInputClass, Class<O> pOutputClass, @Nullable String pGroupName) {
    mInputType = pInputClass;
    mOutputType = pOutputClass;
    mGroupName = pGroupName;
  }

  protected AbstractConverter(TypeReference<I> pInputType, TypeReference<O> pOutputType, @Nullable String pGroupName) {
    mInputType = pInputType.getType();
    mOutputType = pOutputType.getType();
    mGroupName = pGroupName;
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

  /**
   * @see com.diamondq.common.converters.Converter#getGroupName()
   */
  @Override
  public @Nullable String getGroupName() {
    return mGroupName;
  }
}

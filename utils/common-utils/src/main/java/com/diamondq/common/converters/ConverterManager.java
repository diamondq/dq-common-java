package com.diamondq.common.converters;

import com.diamondq.common.TypeReference;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;

public interface ConverterManager {

  void addConverter(Converter<?, ?> pConverter);

  <O> Collection<Converter<?, O>> getConvertersByOutput(TypeReference<O> pOutputType);

  <I> Collection<Converter<I, ?>> getConvertersByInput(TypeReference<I> pInputType);

  /**
   * Converts an output from an input type to an output type
   *
   * @param pInput the input object
   * @param pOutputClass the requested output class
   * @return the object
   * @throws ExtendedIllegalArgumentException if there is no possible converter, then it will throw an exception with a
   *                                          code of UtilMessages.CONVERTERMANAGER_NO_MATCH
   */
  <I, O> O convert(I pInput, Class<O> pOutputClass);

  <I, O> O convert(I pInput, Class<O> pOutputClass, @Nullable String pGroupName);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, Class<O> pOutputClass);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, Class<O> pOutputClass,
    @Nullable String pGroupName);

  <I, O> O convert(I pInput, Type pOutputType);

  <I, O> O convert(I pInput, Type pOutputType, @Nullable String pGroupName);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, Type pOutputType);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, Type pOutputType,
    @Nullable String pGroupName);

  <I, O> O convert(I pInput, Type pInputType, Type pOutputType);

  <I, O> O convert(I pInput, Type pInputType, Type pOutputType, @Nullable String pGroupName);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, Type pInputType,
    Type pOutputType);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, Type pInputType,
    Type pOutputType, @Nullable String pGroupName);

  <I, O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType);

  <I, O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType, @Nullable String pGroupName);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType, @Nullable String pGroupName);

  <I, O> O convert(I pInput, TypeReference<O> pOutputType);

  <I, O> O convert(I pInput, TypeReference<O> pOutputType, @Nullable String pGroupName);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, TypeReference<O> pOutputType);

  <I extends @Nullable Object, O extends @Nullable Object> O convertNullable(I pInput, TypeReference<O> pOutputType,
    @Nullable String pGroupName);

}

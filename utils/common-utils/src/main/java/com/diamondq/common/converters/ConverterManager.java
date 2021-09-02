package com.diamondq.common.converters;

import com.diamondq.common.TypeReference;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;

import java.lang.reflect.Type;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConverterManager {

  public void addConverter(Converter<?, ?> pConverter);

  public <O> Collection<Converter<?, O>> getConvertersByOutput(TypeReference<O> pOutputType);

  public <I> Collection<Converter<I, ?>> getConvertersByInput(TypeReference<I> pInputType);

  /**
   * Converts an output from an input type to an output type
   * 
   * @param pInput the input object
   * @param pOutputClass the requested output class
   * @return the object
   * @throws ExtendedIllegalArgumentException if there is no possible converter, then it will throw an exception with a
   *           code of UtilMessages.CONVERTERMANAGER_NO_MATCH
   */
  public <@NonNull I, @NonNull O> @NonNull O convert(I pInput, Class<O> pOutputClass);

  public <@NonNull I, @NonNull O> @NonNull O convert(I pInput, Class<O> pOutputClass, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass, @Nullable String pGroupName);

  public <@NonNull I, @NonNull O> @NonNull O convert(I pInput, Type pOutputType);

  public <@NonNull I, @NonNull O> @NonNull O convert(I pInput, Type pOutputType, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType, @Nullable String pGroupName);

  public <@NonNull I, @NonNull O> O convert(I pInput, Type pInputType, Type pOutputType);

  public <@NonNull I, @NonNull O> O convert(I pInput, Type pInputType, Type pOutputType, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType,
    @Nullable String pGroupName);

  public <@NonNull I, @NonNull O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType);

  public <@NonNull I, @NonNull O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType,
    @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType, @Nullable String pGroupName);

  public <@NonNull I, @NonNull O> O convert(I pInput, TypeReference<O> pOutputType);

  public <@NonNull I, @NonNull O> O convert(I pInput, TypeReference<O> pOutputType, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<O> pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<O> pOutputType,
    @Nullable String pGroupName);

}

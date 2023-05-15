package com.diamondq.common.converters;

import com.diamondq.common.TypeReference;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;

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
   *                                          code of UtilMessages.CONVERTERMANAGER_NO_MATCH
   */
  public <@NotNull I, @NotNull O> @NotNull O convert(I pInput, Class<O> pOutputClass);

  public <@NotNull I, @NotNull O> @NotNull O convert(I pInput, Class<O> pOutputClass, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass, @Nullable String pGroupName);

  public <@NotNull I, @NotNull O> @NotNull O convert(I pInput, Type pOutputType);

  public <@NotNull I, @NotNull O> @NotNull O convert(I pInput, Type pOutputType, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType, @Nullable String pGroupName);

  public <@NotNull I, @NotNull O> O convert(I pInput, Type pInputType, Type pOutputType);

  public <@NotNull I, @NotNull O> O convert(I pInput, Type pInputType, Type pOutputType, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType,
    @Nullable String pGroupName);

  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType);

  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType,
    @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType, @Nullable String pGroupName);

  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<O> pOutputType);

  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<O> pOutputType, @Nullable String pGroupName);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<O> pOutputType);

  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<O> pOutputType,
    @Nullable String pGroupName);

}

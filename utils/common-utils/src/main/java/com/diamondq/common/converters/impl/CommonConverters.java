package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.Converter;
import com.diamondq.common.converters.LambdaConverter;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

@Factory
public class CommonConverters {

  @Singleton
  public Converter<?, ?> getStringToPathConverter() {
    return new LambdaConverter<>(String.class, Path.class, Paths::get);
  }

  @Singleton
  public Converter<?, ?> getBooleanToStringConverter() {
    return new LambdaConverter<>(Boolean.class, String.class, Object::toString);
  }

  @Singleton
  public Converter<?, ?> getIntegerToLongConverter() {
    return new LambdaConverter<>(Integer.class, Long.class, Integer::longValue);
  }

  @Singleton
  public Converter<?, ?> getIntegerToStringConverter() {
    return new LambdaConverter<>(Integer.class, String.class, Object::toString);
  }

  @Singleton
  public Converter<?, ?> getLongToStringConverter() {
    return new LambdaConverter<>(Long.class, String.class, String::valueOf);
  }
}

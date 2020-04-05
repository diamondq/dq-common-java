package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.LambdaConverter;
import com.diamondq.common.converters.Converter;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;

@Factory
public class CommonConverters {

  @Singleton
  public Converter<String, Path> getStringToPathConverter() {
    return new LambdaConverter<>(String.class, Path.class, (path) -> Paths.get(path));
  }
}

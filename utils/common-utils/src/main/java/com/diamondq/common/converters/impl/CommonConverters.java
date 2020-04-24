package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.Converter;
import com.diamondq.common.converters.LambdaConverter;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;

@Factory
public class CommonConverters {

  @Singleton
  public Converter getStringToPathConverter() {
    return new LambdaConverter<>(String.class, Path.class, (path) -> Paths.get(path));
  }
}

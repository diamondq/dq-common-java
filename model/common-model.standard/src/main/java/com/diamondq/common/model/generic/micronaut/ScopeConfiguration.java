package com.diamondq.common.model.generic.micronaut;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.convert.format.MapFormat;
import io.micronaut.core.convert.format.MapFormat.MapTransformation;

@EachProperty("persistence.scopes")
public class ScopeConfiguration {

  public final String                        name;

  @MapFormat(transformation = MapTransformation.FLAT)
  public @Nullable List<Map<String, Object>> structures;

  @MapFormat(transformation = MapTransformation.FLAT)
  public @Nullable List<Map<String, Object>> resources;

  public ScopeConfiguration(@Parameter String pName) {
    name = pName;
  }
}

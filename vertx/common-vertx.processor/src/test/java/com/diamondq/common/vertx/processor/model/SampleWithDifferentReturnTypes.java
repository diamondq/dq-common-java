package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.context.ContextExtendedCompletionStage;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface SampleWithDifferentReturnTypes {

  @ValueString("void")
  public void VoidType();

  @ValueString("boolean")
  public boolean BooleanType();

  @ValueString("java.lang.Boolean")
  public Boolean Boolean();

  @ValueString("byte")
  public byte ByteType();

  @ValueString("java.lang.Byte")
  public Byte Byte();

  @ValueString("char")
  public char CharType();

  @ValueString("java.lang.Character")
  public Character Char();

  @ValueString("int")
  public int IntType();

  @ValueString("java.lang.Integer")
  public Integer Int();

  @ValueString("short")
  public short ShortType();

  @ValueString("java.lang.Short")
  public Short Short();

  @ValueString("long")
  public long LongType();

  @ValueString("java.lang.Long")
  public Long Long();

  @ValueString("double")
  public double DoubleType();

  @ValueString("java.lang.Double")
  public Double Double();

  @ValueString("float")
  public float FloatType();

  @ValueString("java.lang.Float")
  public Float Float();

  @ValueString("io.vertx.core.json.JsonObject")
  public JsonObject JsonObject();

  @ValueString("io.vertx.core.json.JsonArray")
  public JsonArray JsonArray();

  @ValueString("com.diamondq.common.context.ContextExtendedCompletionStage<java.lang.Boolean>")
  public ContextExtendedCompletionStage<Boolean> CBoolean();

}

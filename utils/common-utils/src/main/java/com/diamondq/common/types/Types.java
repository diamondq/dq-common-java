package com.diamondq.common.types;

import com.diamondq.common.TypeReference;

import java.util.List;
import java.util.Map;

public abstract class Types {

  private Types() {
  }

  /**
   * Constant for string argument.
   */
  public static final TypeReference<String>              STRING               = new TypeReference<String>() {
                                                                              };

  /**
   * Constant for int argument.
   */
  public static final TypeReference<Integer>             INT                  = new TypeReference<Integer>() {
                                                                              };

  /**
   * Constant for long argument.
   */
  public static final TypeReference<Long>                LONG                 = new TypeReference<Long>() {
                                                                              };

  /**
   * Constant for float argument.
   */
  public static final TypeReference<Float>               FLOAT                = new TypeReference<Float>() {
                                                                              };

  /**
   * Constant for double argument.
   */
  public static final TypeReference<Double>              DOUBLE               = new TypeReference<Double>() {
                                                                              };

  /**
   * Constant for void argument.
   */
  public static final TypeReference<Void>                VOID                 = new TypeReference<Void>() {
                                                                              };

  /**
   * Constant for byte argument.
   */
  public static final TypeReference<Byte>                BYTE                 = new TypeReference<Byte>() {
                                                                              };

  /**
   * Constant for boolean argument.
   */
  public static final TypeReference<Boolean>             BOOLEAN              = new TypeReference<Boolean>() {
                                                                              };

  /**
   * Constant char argument.
   */
  public static final TypeReference<Character>           CHAR                 = new TypeReference<Character>() {
                                                                              };

  /**
   * Constant short argument.
   */
  public static final TypeReference<Short>               SHORT                = new TypeReference<Short>() {
                                                                              };

  /**
   * Default Object argument.
   */
  public static final TypeReference<Object>              OBJECT               = new TypeReference<Object>() {
                                                                              };

  public static final TypeReference<Map<String, String>> MAP_STRING_TO_STRING =
    new TypeReference<Map<String, String>>() {
                                                                                };

  public static final TypeReference<List<String>>        LIST_STRING          = new TypeReference<List<String>>() {
                                                                              };

  public static final TypeReference<List<Long>>          LIST_LONG            = new TypeReference<List<Long>>() {
                                                                              };

}

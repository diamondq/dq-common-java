package com.diamondq.common.types;

import com.diamondq.common.TypeReference;
import com.diamondq.common.i18n.I18NString;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class Types {

  private Types() {
  }

  /**
   * Constant for string argument.
   */
  public static final TypeReference<String> STRING = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable String> NULLABLE_STRING = new TypeReference<@Nullable String>() {
  };

  /**
   * Constant for int argument.
   */
  public static final TypeReference<Integer> INT = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Integer> NULLABLE_INT = new TypeReference<@Nullable Integer>() {
  };

  /**
   * Constant for long argument.
   */
  public static final TypeReference<Long> LONG = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Long> NULLABLE_LONG = new TypeReference<@Nullable Long>() {
  };

  /**
   * Constant for float argument.
   */
  public static final TypeReference<Float> FLOAT = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Float> NULLABLE_FLOAT = new TypeReference<@Nullable Float>() {
  };

  /**
   * Constant for double argument.
   */
  public static final TypeReference<Double> DOUBLE = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Double> NULLABLE_DOUBLE = new TypeReference<@Nullable Double>() {
  };

  /**
   * Constant for void argument.
   */
  public static final TypeReference<@Nullable Void> VOID          = new TypeReference<@Nullable Void>() {
  };
  public static final TypeReference<@Nullable Void> NULLABLE_VOID = new TypeReference<@Nullable Void>() {
  };

  /**
   * Constant for byte argument.
   */
  public static final TypeReference<Byte> BYTE = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Byte> NULLABLE_BYTE = new TypeReference<@Nullable Byte>() {
  };

  /**
   * Constant for boolean argument.
   */
  public static final TypeReference<Boolean> BOOLEAN = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Boolean> NULLABLE_BOOLEAN = new TypeReference<@Nullable Boolean>() {
  };

  /**
   * Constant char argument.
   */
  public static final TypeReference<Character> CHAR = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Character> NULLABLE_CHAR = new TypeReference<@Nullable Character>() {
  };

  /**
   * Constant short argument.
   */
  public static final TypeReference<Short> SHORT = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Short> NULLABLE_SHORT = new TypeReference<@Nullable Short>() {
  };

  /**
   * Default Object argument.
   */
  public static final TypeReference<Object> OBJECT = new TypeReference<>() {
  };

  public static final TypeReference<Class<?>>         CLASS           = new TypeReference<>() {
  };
  public static final TypeReference<DataSource>       DATA_SOURCE     = new TypeReference<>() {
  };
  public static final TypeReference<@Nullable Object> NULLABLE_OBJECT = new TypeReference<@Nullable Object>() {
  };

  public static final TypeReference<Map<String, String>> MAP_OF_STRING_TO_STRING = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Map<String, String>> NULLABLE_MAP_OF_STRING_TO_STRING = new TypeReference<@Nullable Map<String, String>>() {
  };

  public static final TypeReference<Map<String, Long>> MAP_OF_STRING_TO_LONG = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Map<String, Long>> NULLABLE_MAP_OF_STRING_TO_LONG = new TypeReference<@Nullable Map<String, Long>>() {
  };

  public static final TypeReference<Map<String, @Nullable Object>> MAP_OF_STRING_TO_NULLABLE_OBJECT = new TypeReference<Map<String, @Nullable Object>>() {
  };
  public static final TypeReference<Map<String, Object>>           MAP_OF_STRING_TO_OBJECT          = new TypeReference<>() {
  };

  public static final TypeReference<List<String>> LIST_OF_STRING = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable List<String>> NULLABLE_LIST_OF_STRING = new TypeReference<@Nullable List<String>>() {
  };
  public static final TypeReference<List<Object>>           LIST_OF_OBJECT          = new TypeReference<>() {
  };

  public static final TypeReference<Set<String>> SET_OF_STRING = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Set<String>> NULLABLE_SET_OF_STRING = new TypeReference<@Nullable Set<String>>() {
  };

  public static final TypeReference<Collection<String>> COLLECTION_OF_STRING = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable Collection<String>> NULLABLE_COLLECTION_OF_STRING = new TypeReference<@Nullable Collection<String>>() {
  };

  public static final TypeReference<List<Long>> LIST_OF_LONG = new TypeReference<>() {
  };

  public static final TypeReference<@Nullable List<Long>> NULLABLE_LIST_OF_LONG = new TypeReference<@Nullable List<Long>>() {
  };

  public static final TypeReference<I18NString> I18NSTRING = new TypeReference<>() {
  };

  public static final TypeReference<Optional<?>> OPTIONAL_OF_WILD = new TypeReference<>() {
  };

  public static final TypeReference<BigDecimal> BIG_DECIMAL = new TypeReference<>() {
  };
  public static final TypeReference<BigInteger> BIG_INTEGER = new TypeReference<>() {
  };

  /* Date/Time */

  public static final TypeReference<Date>           DATE             = new TypeReference<>() {
  };
  public static final TypeReference<LocalDate>      LOCAL_DATE       = new TypeReference<>() {         // type
  };
  public static final TypeReference<LocalDateTime>  LOCAL_DATE_TIME  = new TypeReference<>() {         // type
  };
  public static final TypeReference<ZonedDateTime>  ZONED_DATE_TIME  = new TypeReference<>() {
  };
  public static final TypeReference<OffsetDateTime> OFFSET_DATE_TIME = new TypeReference<>() {
  };


  /* ARRAYS */

  public static final TypeReference<String[]>     ARRAY_OF_STRING      = new TypeReference<>() {
  };
  public static final TypeReference<Boolean[]>    ARRAY_OF_BOOLEAN     = new TypeReference<>() {         // type
  };
  public static final TypeReference<BigDecimal[]> ARRAY_OF_BIG_DECIMAL = new TypeReference<>() {         // type
  };
  public static final TypeReference<byte[]>       ARRAY_OF_BYTE        = new TypeReference<>() {
  };
}

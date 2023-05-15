package com.diamondq.common.storage.jdbc;

import com.diamondq.common.config.ConfigKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataSourceFactory {

  public static class DataSourceFactoryBuilder {

    private Map<Object, Object> mParams = Maps.newHashMap();

    public DataSource build() {
      String type = (String) mParams.remove("type");
      try {
        @SuppressWarnings(
          "unchecked") Class<? extends DataSource> sourceClass = (Class<? extends DataSource>) Class.forName(type);
        DataSource source = sourceClass.newInstance();

        param:
        for (Map.Entry<@NotNull Object, Object> pair : mParams.entrySet()) {
          String key = pair.getKey().toString();
          List<String> possibleNames = determinePossible(key);
          Set<Class<?>> possibleArgs = determinePossibleArgs(pair.getValue());
          for (String k : possibleNames) {
            for (Class<?> c : possibleArgs) {
              try {
                Method method = source.getClass().getMethod("set" + k, c);
                Object wrappedValue = wrapValue(pair.getValue(), c);
                method.invoke(source, wrappedValue);
                continue param;
              }
              catch (NoSuchMethodException | SecurityException | IllegalArgumentException |
                     InvocationTargetException ex2) {
                continue;
              }
            }
          }
        }

        return source;
      }
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    }

    private Object wrapValue(Object pValue, Class<?> pClass) {
      if ((Boolean.class == pClass) || (Boolean.TYPE == pClass)) {
        if (Boolean.class.isInstance(pValue)) return pValue;
        return Boolean.valueOf(pValue.toString());
      }
      if ((Short.class == pClass) || (Short.TYPE == pClass)) {
        if (Short.class.isInstance(pValue)) return pValue;
        return Short.valueOf(pValue.toString());
      }
      if ((Integer.class == pClass) || (Integer.TYPE == pClass)) {
        if (Integer.class.isInstance(pValue)) return pValue;
        return Integer.valueOf(pValue.toString());
      }
      if ((Long.class == pClass) || (Long.TYPE == pClass)) {
        if (Long.class.isInstance(pValue)) return pValue;
        return Long.valueOf(pValue.toString());
      }
      if ((Float.class == pClass) || (Float.TYPE == pClass)) {
        if (Float.class.isInstance(pValue)) return pValue;
        return Float.valueOf(pValue.toString());
      }
      if ((Double.class == pClass) || (Double.TYPE == pClass)) {
        if (Double.class.isInstance(pValue)) return pValue;
        return Double.valueOf(pValue.toString());
      }
      if (String.class == pClass) {
        if (String.class.isInstance(pValue)) return pValue;
        return pValue.toString();
      }
      return pValue;
    }

    private Set<Class<?>> determinePossibleArgs(Object pValue) {
      ImmutableSet.Builder<@NotNull Class<?>> builder = ImmutableSet.builder();
      builder.add(String.class);
      if (pValue instanceof Boolean) builder.add(Boolean.class).add(Boolean.TYPE);
      else if (pValue instanceof Short) builder.add(Short.class).add(Short.TYPE);
      else if (pValue instanceof Integer) builder.add(Integer.class).add(Integer.TYPE);
      else if (pValue instanceof Long) builder.add(Long.class).add(Long.TYPE);
      else if (pValue instanceof Float) builder.add(Float.class).add(Float.TYPE);
      else if (pValue instanceof Double) builder.add(Double.class).add(Double.TYPE);
      else builder.add(pValue.getClass());

      if (pValue instanceof String) {
        String str = (String) pValue;
        if (("true".equalsIgnoreCase(str)) || ("false".equalsIgnoreCase(str)))
          builder.add(Boolean.class).add(Boolean.TYPE);
        try {
          Short.valueOf(str);
          builder.add(Short.class).add(Short.TYPE);
        }
        catch (NumberFormatException ex) {
        }
        try {
          Integer.valueOf(str);
          builder.add(Integer.class).add(Integer.TYPE);
        }
        catch (NumberFormatException ex) {
        }
        try {
          Long.valueOf(str);
          builder.add(Long.class).add(Long.TYPE);
        }
        catch (NumberFormatException ex) {
        }
        try {
          Float.valueOf(str);
          builder.add(Float.class).add(Float.TYPE);
        }
        catch (NumberFormatException ex) {
        }
        try {
          Double.valueOf(str);
          builder.add(Double.class).add(Double.TYPE);
        }
        catch (NumberFormatException ex) {
        }
      }
      return builder.build();
    }

    private List<@NotNull String> determinePossible(String pKey) {

      /* The key may be either a-b or aB or just a, in which case, the possible values should be AB or A */

      /* Uppercase the first letter */

      StringBuilder sb = new StringBuilder();
      boolean isFirst = true;
      boolean capitializeNext = true;
      for (char c : pKey.toCharArray()) {
        if (isFirst == true) {
          isFirst = false;
          capitializeNext = false;
          sb.append(Character.toUpperCase(c));
        } else if (c == '-') {
          /* Skip it */
          capitializeNext = true;
        } else {
          if (capitializeNext == true) {
            sb.append(Character.toUpperCase(c));
            capitializeNext = false;
          } else sb.append(c);
        }
      }

      return ImmutableList.of(sb.toString());
    }

    @ConfigKey("*")
    public DataSourceFactoryBuilder putAnything(Object pKey, Object pValue) {
      mParams.put(pKey, pValue);
      return this;
    }

    public DataSourceFactoryBuilder anythings(Map<Object, Object> pValue) {
      mParams.putAll(pValue);
      return this;
    }
  }

  public static DataSourceFactoryBuilder builder() {
    return new DataSourceFactoryBuilder();
  }
}

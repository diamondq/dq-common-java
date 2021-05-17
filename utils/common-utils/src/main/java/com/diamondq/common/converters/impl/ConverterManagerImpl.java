package com.diamondq.common.converters.impl;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.converters.Converter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import com.googlecode.gentyref.GenericTypeReflector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class ConverterManagerImpl implements ConverterManager {

  private static class TypePair {
    public final Type inputType;

    public final Type outputType;

    public TypePair(Type pInputType, Type pOutputType) {
      inputType = pInputType;
      outputType = pOutputType;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return Objects.hash(inputType, outputType);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(@Nullable Object pObj) {
      if (this == pObj)
        return true;
      if (pObj == null)
        return false;
      if (getClass() != pObj.getClass())
        return false;
      TypePair pOther = (TypePair) pObj;
      if (Objects.equals(inputType, pOther.inputType) == false)
        return false;
      if (Objects.equals(outputType, pOther.outputType) == false)
        return false;
      return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return new StringBuilder().append(inputType.getTypeName()).append(" -> ").append(outputType.getTypeName())
        .toString();
    }
  }

  private final ConcurrentMap<TypePair, Converter<?, ?>> mConvertersByType = new ConcurrentHashMap<>();

  private final ConcurrentMap<TypePair, TypePair>        mShortcuts        = new ConcurrentHashMap<>();

  @Inject
  public ConverterManagerImpl(List<Converter<?, ?>> pConverters) {
    for (Converter<?, ?> pConverter : pConverters) {
      mConvertersByType.put(new TypePair(pConverter.getInputType(), pConverter.getOutputType()), pConverter);
    }
    mShortcuts.clear();
  }

  @Override
  public void addConverter(Converter<?, ?> pConverter) {
    mConvertersByType.put(new TypePair(pConverter.getInputType(), pConverter.getOutputType()), pConverter);
    mShortcuts.clear();
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#getConvertersByInput(java.lang.reflect.Type)
   */
  @Override
  public Collection<Converter<?, ?>> getConvertersByInput(Type pInputType) {
    List<Converter<?, ?>> result = new ArrayList<>();
    for (Map.Entry<TypePair, Converter<?, ?>> pair : mConvertersByType.entrySet()) {
      TypePair key = pair.getKey();
      if (key.inputType.equals(pInputType))
        result.add(pair.getValue());
    }
    return result;
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#getConvertersByOutput(java.lang.reflect.Type)
   */
  @Override
  public Collection<Converter<?, ?>> getConvertersByOutput(Type pOutputType) {
    List<Converter<?, ?>> result = new ArrayList<>();
    for (Map.Entry<TypePair, Converter<?, ?>> pair : mConvertersByType.entrySet()) {
      TypePair key = pair.getKey();
      if (key.outputType.equals(pOutputType))
        result.add(pair.getValue());
    }
    return result;
  }

  void calculateTypes(Type pType, LinkedHashSet<Type> pSet) {
    LinkedHashSet<Class<?>> rawTypes = new LinkedHashSet<>();
    recurseType(pType, pSet, rawTypes);

    /* Now add wildcard versions */

    LinkedHashSet<Class<?>> ignored = new LinkedHashSet<>();
    for (Class<?> rawClass : rawTypes) {
      Type wildRawType = Objects.requireNonNull(GenericTypeReflector.addWildcardParameters(rawClass));
      if (pSet.contains(wildRawType) == false)
        recurseType(wildRawType, pSet, ignored);
    }
  }

  private void recurseType(Type pType, LinkedHashSet<Type> pSet, LinkedHashSet<Class<?>> pRawTypes) {
    if (pType instanceof Class)
      pType = Objects.requireNonNull(GenericTypeReflector.addWildcardParameters((Class<?>) pType));
    pSet.add(pType);
    if (pType instanceof Class) {
      Class<?> clazz = (Class<?>) pType;
      @NonNull
      Class<?>[] interfaces = clazz.getInterfaces();
      for (Class<?> intf : interfaces) {
        Type exactIntf = GenericTypeReflector.getExactSuperType(pType, intf);
        if (exactIntf != null)
          recurseType(exactIntf, pSet, pRawTypes);
      }
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null) {
        Type exactSuperClass = GenericTypeReflector.getExactSuperType(pType, superClass);
        if (exactSuperClass != null)
          recurseType(exactSuperClass, pSet, pRawTypes);
      }
    }
    else if (pType instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) pType).getRawType();
      if (rawType instanceof Class) {
        Class<?> rawClass = (Class<?>) rawType;
        pRawTypes.add(rawClass);
        @NonNull
        Class<?>[] interfaces = rawClass.getInterfaces();
        for (Class<?> intf : interfaces) {
          Type exactSuperType = GenericTypeReflector.getExactSuperType(pType, intf);
          if (exactSuperType != null)
            recurseType(exactSuperType, pSet, pRawTypes);
        }
        Class<?> superClass = rawClass.getSuperclass();
        if (superClass != null) {
          Type exactSuperClass = GenericTypeReflector.getExactSuperType(pType, superClass);
          if (exactSuperClass != null)
            recurseType(exactSuperClass, pSet, pRawTypes);
        }

      }
      else
        throw new UnsupportedOperationException();
    }
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convertNullable(java.lang.Object, java.lang.Class)
   */
  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass) {
    if (pInput == null)
      return null;
    return convert(pInput, pInput.getClass(), pOutputClass);
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convert(java.lang.Object, java.lang.Class)
   */
  @Override
  public <@NonNull I, @NonNull O> O convert(I pInput, Class<O> pOutputClass) {
    assert pInput != null;
    return convert(pInput, pInput.getClass(), pOutputClass);
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convertNullable(java.lang.Object, java.lang.reflect.Type,
   *      java.lang.reflect.Type)
   */
  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType) {
    if (pInput == null)
      return null;
    return convert(pInput, pInputType, pOutputType);
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convert(java.lang.Object, java.lang.reflect.Type)
   */
  @Override
  public <@NonNull I, @NonNull O> O convert(I pInput, Type pOutputType) {
    return convert(pInput, pInput.getClass(), pOutputType);
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convertNullable(java.lang.Object, java.lang.reflect.Type)
   */
  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType) {
    if (pInput == null)
      return null;
    return convert(pInput, pInput.getClass(), pOutputType);
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convert(java.lang.Object, java.lang.reflect.Type,
   *      java.lang.reflect.Type)
   */
  @Override
  public <@NonNull I, @NonNull O> O convert(I pInput, Type pInputType, Type pOutputType) {
    assert pInput != null;
    assert pInputType != null;
    assert pOutputType != null;
    TypePair inputTypePair = new TypePair(pInputType, pOutputType);
    TypePair matchClass = mShortcuts.get(inputTypePair);
    if (matchClass == null) {

      /* Build the full set of possible classes */

      LinkedHashSet<Type> testClasses = new LinkedHashSet<>();
      calculateTypes(pInputType, testClasses);

      /* Now test each class in order, to find the first matching converter */

      for (Type testType2 : testClasses) {
        TypePair testPair = new TypePair(testType2, pOutputType);
        if (mConvertersByType.containsKey(testPair) == true) {
          matchClass = testPair;
          mShortcuts.put(inputTypePair, testPair);
          break;
        }
      }
      if (matchClass == null)
        throw new ExtendedIllegalArgumentException(UtilMessages.CONVERTERMANAGER_NO_MATCH, pInputType.getTypeName(),
          pOutputType.getTypeName());
    }
    @SuppressWarnings("unchecked")
    Converter<I, O> converter = (Converter<I, O>) mConvertersByType.get(matchClass);
    if (converter == null)
      throw new ExtendedIllegalArgumentException(UtilMessages.CONVERTERMANAGER_NO_MATCH, pInputType.getTypeName(),
        pOutputType.getTypeName());
    O result = converter.convert(pInput);
    if (pOutputType instanceof Class) {
      if (((Class<?>) pOutputType).isInstance(result) == false)
        throw new IllegalArgumentException();
    }
    else if (pOutputType instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) pOutputType).getRawType();
      if (rawType instanceof Class) {
        if (((Class<?>) rawType).isInstance(result) == false)
          throw new IllegalArgumentException();
      }
      else
        throw new UnsupportedOperationException();
    }
    else
      throw new UnsupportedOperationException();
    return result;
  }

}

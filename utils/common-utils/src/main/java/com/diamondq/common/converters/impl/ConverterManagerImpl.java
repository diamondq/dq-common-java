package com.diamondq.common.converters.impl;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.converters.Converter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class ConverterManagerImpl implements ConverterManager {

  private static class ClassPair {
    public final Class<?> inputClass;

    public final Class<?> outputClass;

    public ClassPair(Class<?> pInputClass, Class<?> pOutputClass) {
      inputClass = pInputClass;
      outputClass = pOutputClass;
    }

    @Override
    public int hashCode() {
      return Objects.hash(inputClass, outputClass);
    }

    @Override
    public boolean equals(@Nullable Object pObj) {
      if (this == pObj)
        return true;
      if (pObj == null)
        return false;
      if (getClass() != pObj.getClass())
        return false;
      ClassPair pOther = (ClassPair) pObj;
      if (Objects.equals(inputClass, pOther.inputClass) == false)
        return false;
      if (Objects.equals(outputClass, pOther.outputClass) == false)
        return false;
      return true;
    }
  }

  private final ConcurrentMap<ClassPair, Converter<?, ?>> mConvertersByClass = new ConcurrentHashMap<>();

  private final ConcurrentMap<ClassPair, ClassPair>       mShortcuts         = new ConcurrentHashMap<>();

  @Inject
  public void setConverters(List<Converter<?, ?>> pConverters) {
    for (Converter<?, ?> pConverter : pConverters) {
      mConvertersByClass.put(new ClassPair(pConverter.getInputClass(), pConverter.getOutputClass()), pConverter);
    }
    mShortcuts.clear();
  }

  private void recurseInterface(Class<?> pClass, Set<Class<?>> pSet) {
    pSet.add(pClass);
    @NonNull
    Class<?>[] interfaces = pClass.getInterfaces();
    if (interfaces.length > 0)
      for (Class<?> intf : interfaces) {
        recurseInterface(intf, pSet);
      }
  }

  /**
   * @see com.diamondq.common.converters.ConverterManager#convert(java.lang.Object, java.lang.Class)
   */
  @Override
  public <I, O> O convert(@NonNull I pInput, Class<O> pOutputClass) {
    assert pInput != null;
    assert pOutputClass != null;
    Class<?> rootClass = pInput.getClass();
    ClassPair inputClassPair = new ClassPair(rootClass, pOutputClass);
    ClassPair matchClass = mShortcuts.get(inputClassPair);
    if (matchClass == null) {

      /* Build the full set of possible classes */

      LinkedHashSet<Class<?>> testClasses = new LinkedHashSet<>();
      Class<?> testClass = rootClass;
      while (testClass != null) {
        recurseInterface(testClass, testClasses);
        testClass = testClass.getSuperclass();
      }

      /* Now test each class in order, to find the first matching converter */

      for (Class<?> testClass2 : testClasses) {
        ClassPair testPair = new ClassPair(testClass2, pOutputClass);
        if (mConvertersByClass.containsKey(testPair) == true) {
          matchClass = testPair;
          mShortcuts.put(inputClassPair, testPair);
          break;
        }
      }
      if (matchClass == null)
        throw new ExtendedIllegalArgumentException(UtilMessages.CONVERTERMANAGER_NO_MATCH, rootClass.getName(),
          pOutputClass.getName());
    }
    @SuppressWarnings("unchecked")
    Converter<I, O> converter = (Converter<I, O>) mConvertersByClass.get(matchClass);
    if (converter == null)
      throw new ExtendedIllegalArgumentException(UtilMessages.CONVERTERMANAGER_NO_MATCH, rootClass.getName(),
        pOutputClass.getName());
    return converter.convert(pInput);
  }

}

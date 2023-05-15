package com.diamondq.common.converters.impl;

import com.diamondq.common.TypeReference;
import com.diamondq.common.UtilMessages;
import com.diamondq.common.converters.Converter;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import com.googlecode.gentyref.GenericTypeReflector;
import com.googlecode.gentyref.TypeFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class ConverterManagerImpl implements ConverterManager {

  private static class TypePair {
    public final @Nullable String groupName;

    public final Type inputType;

    public final Type outputType;

    public TypePair(@Nullable String pGroupName, Type pInputType, Type pOutputType) {
      groupName = pGroupName;
      inputType = pInputType;
      outputType = pOutputType;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return Objects.hash(groupName, inputType, outputType);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(@Nullable Object pObj) {
      if (this == pObj) return true;
      if (pObj == null) return false;
      if (getClass() != pObj.getClass()) return false;
      TypePair pOther = (TypePair) pObj;
      if (!Objects.equals(groupName, pOther.groupName)) return false;
      if (!Objects.equals(inputType, pOther.inputType)) return false;
      //noinspection RedundantIfStatement
      if (!Objects.equals(outputType, pOther.outputType)) return false;
      return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      //noinspection StringBufferReplaceableByString
      return new StringBuilder().append(inputType.getTypeName())
        .append(" -> ")
        .append(outputType.getTypeName())
        .toString();
    }
  }

  private static final Converter<?, ?> sIdentityConverter = new IdentityConverter();

  private final ConcurrentMap<TypePair, Converter<?, ?>> mConvertersByType = new ConcurrentHashMap<>();

  private final ConcurrentMap<TypePair, Converter<?, ?>> mShortcuts = new ConcurrentHashMap<>();

  @Inject
  public ConverterManagerImpl(List<Converter<?, ?>> pConverters) {
    for (Converter<?, ?> pConverter : pConverters) {
      mConvertersByType.put(new TypePair(pConverter.getGroupName(),
        pConverter.getInputType(),
        pConverter.getOutputType()
      ), pConverter);
    }
    mShortcuts.clear();
  }

  @Override
  public void addConverter(Converter<?, ?> pConverter) {
    mConvertersByType.put(new TypePair(pConverter.getGroupName(),
      pConverter.getInputType(),
      pConverter.getOutputType()
    ), pConverter);
    mShortcuts.clear();
  }

  @Override
  public <I> Collection<Converter<I, ?>> getConvertersByInput(TypeReference<I> pInputType) {
    List<Converter<I, ?>> result = new ArrayList<>();
    Type testType = pInputType.getType();
    for (Map.Entry<TypePair, Converter<?, ?>> pair : mConvertersByType.entrySet()) {
      TypePair key = pair.getKey();
      if (key.inputType.equals(testType)) {
        @SuppressWarnings("unchecked") Converter<I, ?> c = (Converter<I, ?>) pair.getValue();
        result.add(c);
      }
    }
    return result;
  }

  @Override
  public <O> Collection<Converter<?, O>> getConvertersByOutput(TypeReference<O> pOutputType) {
    List<Converter<?, O>> result = new ArrayList<>();
    Type testType = pOutputType.getType();
    for (Map.Entry<TypePair, Converter<?, ?>> pair : mConvertersByType.entrySet()) {
      TypePair key = pair.getKey();
      if (key.outputType.equals(testType)) {
        @SuppressWarnings("unchecked") Converter<?, O> c = (Converter<?, O>) pair.getValue();
        result.add(c);
      }
    }
    return result;
  }

  void calculateTypes(Type pType, LinkedHashSet<Type> pSet) {

    /* If we start off with a Class, then see if it's a raw class, and if so, add the wildcard parameters */

    if (pType instanceof Class)
      pType = Objects.requireNonNull(GenericTypeReflector.addWildcardParameters((Class<?>) pType));

    /* If we have a ParameterizedType, see if any are TypeVariables */

    if (pType instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) pType;
      Type rawType = pt.getRawType();
      if (rawType instanceof Class) {

        @NotNull Type[] typeArgs = pt.getActualTypeArguments();
        int typeArgsLen = typeArgs.length;
        boolean changed = false;
        if (typeArgsLen > 0) for (int i = 0; i < typeArgsLen; i++) {
          if (typeArgs[i] instanceof TypeVariable) {
            TypeVariable<?> tv = (TypeVariable<?>) typeArgs[i];
            Type[] bounds = tv.getBounds();

            /*
             * NOTE: There's currently a limitation in the Gentyref code that doesn't have a public accessor to create
             * Wildcards that extend multiple bounds
             */

            if (bounds.length > 1) throw new UnsupportedOperationException();

            for (Type bound : bounds) {
              if (!bound.equals(Object.class)) {
                changed = true;
                typeArgs[i] = TypeFactory.wildcardExtends(bound);
              } else {
                changed = true;
                typeArgs[i] = TypeFactory.unboundWildcard();
              }
            }
          }
        }
        if (changed) pType = TypeFactory.parameterizedClass((Class<?>) rawType, typeArgs);
      }
    }

    LinkedHashSet<Class<?>> rawTypes = new LinkedHashSet<>();
    recurseType(pType, pSet, rawTypes);

    /* Now add wildcard versions */

    LinkedHashSet<Class<?>> ignored = new LinkedHashSet<>();
    for (Class<?> rawClass : rawTypes) {
      Type wildRawType = GenericTypeReflector.addWildcardParameters(rawClass);
      if (!pSet.contains(wildRawType)) recurseType(wildRawType, pSet, ignored);
    }
  }

  private void recurseType(Type pType, LinkedHashSet<Type> pSet, LinkedHashSet<Class<?>> pRawTypes) {
    pSet.add(pType);
    if (pType instanceof Class) {
      Class<?> clazz = (Class<?>) pType;
      @NotNull Class<?>[] interfaces = clazz.getInterfaces();
      for (Class<?> intf : interfaces) {
        Type exactIntf = GenericTypeReflector.getExactSuperType(pType, intf);
        if (exactIntf != null) recurseType(exactIntf, pSet, pRawTypes);
      }
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null) {
        Type exactSuperClass = GenericTypeReflector.getExactSuperType(pType, superClass);
        if (exactSuperClass != null) recurseType(exactSuperClass, pSet, pRawTypes);
      }
    } else if (pType instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) pType).getRawType();
      if (rawType instanceof Class) {
        Class<?> rawClass = (Class<?>) rawType;
        pRawTypes.add(rawClass);
        @NotNull Class<?>[] interfaces = rawClass.getInterfaces();
        for (Class<?> intf : interfaces) {
          Type exactSuperType = GenericTypeReflector.getExactSuperType(pType, intf);
          if (exactSuperType != null) recurseType(exactSuperType, pSet, pRawTypes);
        }
        Class<?> superClass = rawClass.getSuperclass();
        if (superClass != null) {
          Type exactSuperClass = GenericTypeReflector.getExactSuperType(pType, superClass);
          if (exactSuperClass != null) recurseType(exactSuperClass, pSet, pRawTypes);
        }

      } else throw new UnsupportedOperationException();
    }
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInput.getClass(), pOutputClass, null, true);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Class<O> pOutputClass, @Nullable String pGroupName) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInput.getClass(), pOutputClass, pGroupName, true);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, Class<O> pOutputClass) {
    assert pInput != null;
    return internalConvert(pInput, pInput.getClass(), pOutputClass, null, false);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, Class<O> pOutputClass, @Nullable String pGroupName) {
    assert pInput != null;
    return internalConvert(pInput, pInput.getClass(), pOutputClass, pGroupName, false);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInputType.getType(), pOutputType.getType(), null, true);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<I> pInputType,
    TypeReference<O> pOutputType, @Nullable String pGroupName) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInputType.getType(), pOutputType.getType(), pGroupName, true);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<O> pOutputType) {
    return internalConvert(pInput, pInput.getClass(), pOutputType.getType(), null, false);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<I> pInputType,
    TypeReference<@NotNull O> pOutputType, @Nullable String pGroupName) {
    return internalConvert(pInput, pInput.getClass(), pOutputType.getType(), pGroupName, false);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<O> pOutputType) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInput.getClass(), pOutputType.getType(), null, true);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, TypeReference<O> pOutputType,
    @Nullable String pGroupName) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInput.getClass(), pOutputType.getType(), pGroupName, true);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<O> pOutputType, @Nullable String pGroupName) {
    return internalConvert(pInput, pInput.getClass(), pOutputType.getType(), pGroupName, false);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, TypeReference<I> pInputType, TypeReference<O> pOutputType) {
    return internalConvert(pInput, pInputType.getType(), pOutputType.getType(), null, false);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, Type pOutputType) {
    return internalConvert(pInput, pInput.getClass(), pOutputType, null, false);
  }

  @Override
  public <@NotNull I, @NotNull O> @NotNull O convert(I pInput, Type pOutputType, @Nullable String pGroupName) {
    return internalConvert(pInput, pInput.getClass(), pOutputType, pGroupName, false);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, Type pInputType, Type pOutputType) {
    return internalConvert(pInput, pInputType, pOutputType, null, false);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInputType, pOutputType, null, true);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pInputType, Type pOutputType,
    @Nullable String pGroupName) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInputType, pOutputType, pGroupName, true);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInput.getClass(), pOutputType, null, true);
  }

  @Override
  public <@Nullable I, @Nullable O> O convertNullable(I pInput, Type pOutputType, @Nullable String pGroupName) {
    if (pInput == null) return null;
    return internalConvert(pInput, pInput.getClass(), pOutputType, pGroupName, true);
  }

  @Override
  public <@NotNull I, @NotNull O> O convert(I pInput, Type pInputType, Type pOutputType, @Nullable String pGroupName) {
    return internalConvert(pInput, pInputType, pOutputType, pGroupName, false);
  }

  private <@NotNull I, @Nullable O> O internalConvert(I pInput, Type pInputType, Type pOutputType,
    @Nullable String pGroupName, boolean pAllowNullOutput) {
    Objects.requireNonNull(pInput);

    /* Quick check for matching classes */

    if ((pOutputType instanceof Class) && (pInputType instanceof Class)) {
      Class<?> outputClass = (Class<?>) pOutputType;
      Class<?> inputClass = (Class<?>) pInputType;
      if (outputClass.isAssignableFrom(inputClass)) {
        @SuppressWarnings("unchecked") O output = (O) pInput;
        return output;
      }
    }

    /* Attempt to look for a shortcut */

    @NotNull TypePair inputTypePair = new TypePair(pGroupName, pInputType, pOutputType);
    Converter<?, ?> matchConverter = mShortcuts.get(inputTypePair);
    if (matchConverter == null) {

      /* Build the full set of possible classes */

      LinkedHashSet<Type> testInputClasses = new LinkedHashSet<>();
      calculateTypes(pInputType, testInputClasses);

      /* Now test each class in order, to find the first matching converter */

      for (Type testInputType : testInputClasses) {
        TypePair testPair = new TypePair(pGroupName, testInputType, pOutputType);
        if ((matchConverter = mConvertersByType.get(testPair)) != null) {
          mShortcuts.put(inputTypePair, matchConverter);
          break;
        }
      }
      if (matchConverter == null) {

        /* Attempt a second expansion of the output */

        LinkedHashSet<Type> testOutputClasses = new LinkedHashSet<>();
        calculateTypes(pOutputType, testOutputClasses);

        /* Now test each class in order, to find the first matching converter */

        OUTERTEST:
        for (Type testInputType : testInputClasses) {
          for (Type testOutputType : testOutputClasses) {
            TypePair testPair = new TypePair(pGroupName, testInputType, testOutputType);
            if ((matchConverter = mConvertersByType.get(testPair)) != null) {
              mShortcuts.put(inputTypePair, matchConverter);
              break OUTERTEST;
            }
          }
        }
        if (matchConverter == null) {

          /* Next see if we can basically assume these are the same */

          if (pOutputType instanceof ParameterizedType) {
            ParameterizedType outputPT = (ParameterizedType) pOutputType;
            Type outputBaseType = outputPT.getRawType();
            Type[] outputActualTypes = outputPT.getActualTypeArguments();
            SAME_INPUT_LOOP:
            for (Type testInputType : testInputClasses) {
              if (testInputType instanceof ParameterizedType) {
                ParameterizedType inputPT = (ParameterizedType) testInputType;

                Type inputBaseType = inputPT.getRawType();
                if ((inputBaseType instanceof Class) && (outputBaseType instanceof Class)) {
                  Class<?> outputBaseClass = (Class<?>) outputBaseType;
                  Class<?> inputBaseClass = (Class<?>) inputBaseType;
                  if (outputBaseClass.isAssignableFrom(inputBaseClass)) {

                    /* The base classes are equivalent. Check the param types */

                    Type[] inputActualTypes = inputPT.getActualTypeArguments();
                    if (inputActualTypes.length == outputActualTypes.length) {
                      int len = inputActualTypes.length;
                      for (int i = 0; i < len; i++) {

                        /* Are the types the same? */

                        if (!inputActualTypes[i].equals(outputActualTypes[i])) {

                          /* Is the input type a wildcard */

                          if (!(inputActualTypes[i] instanceof WildcardType)) {
                            continue SAME_INPUT_LOOP;
                          }
                        }
                      }

                      /* We've found a match */

                      matchConverter = sIdentityConverter;
                      mShortcuts.put(inputTypePair, matchConverter);
                      break SAME_INPUT_LOOP;
                    }
                  }
                }
              }
            }
          }

          if (matchConverter == null) {
            if (pGroupName != null)
              throw new ExtendedIllegalArgumentException(UtilMessages.CONVERTERMANAGER_NO_MATCH_WITH_GROUP,
                pInputType.getTypeName(),
                pOutputType.getTypeName(),
                pGroupName
              );
            else throw new ExtendedIllegalArgumentException(UtilMessages.CONVERTERMANAGER_NO_MATCH,
              pInputType.getTypeName(),
              pOutputType.getTypeName()
            );
          }
        }
      }
    }
    @SuppressWarnings("unchecked") Converter<I, O> converter = (Converter<I, O>) matchConverter;
    @Nullable O result = converter.convert(pInput);

    /* Verify the result matches */

    if (result == null) {
      if (pAllowNullOutput) return null;
      throw new NullPointerException(
        "Conversion of " + pInput + " (type " + pInputType + ") to type " + pOutputType + " resulted in null");
    }

    if (pOutputType instanceof Class) {
      if (!((Class<?>) pOutputType).isInstance(result)) throw new IllegalArgumentException();
    } else if (pOutputType instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) pOutputType).getRawType();
      if (rawType instanceof Class) {
        if (!((Class<?>) rawType).isInstance(result)) throw new IllegalArgumentException();
      } else throw new UnsupportedOperationException();
    } else throw new UnsupportedOperationException();
    return result;
  }

}

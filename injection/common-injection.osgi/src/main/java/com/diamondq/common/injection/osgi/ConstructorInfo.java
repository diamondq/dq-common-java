package com.diamondq.common.injection.osgi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ConstructorInfo {

  public static enum SpecialTypes {
    NA,
    BUNDLECONTEXT,
    COMPONENTCONTEXT,
    CONTEXTFACTORY
  }

  public static class ConstructionArg {

    public final Class<?> argumentClass;

    public final @Nullable String propertyFilterKey;

    public final @Nullable String propertyValueKey;

    public final @Nullable Object propertyValue;

    public final boolean propertyValueSet;

    public final boolean required;

    public final boolean collection;

    public final SpecialTypes specialType;

    public ConstructionArg(Class<?> pArgumentClass, @Nullable String pPropertyFilterKey,
      @Nullable String pPropertyValueKey, @Nullable Object pPropertyValue, boolean pPropertyValueSet, boolean pRequired,
      boolean pCollection, SpecialTypes pSpecialType) {
      super();
      argumentClass = pArgumentClass;
      propertyFilterKey = pPropertyFilterKey;
      propertyValueKey = pPropertyValueKey;
      propertyValue = pPropertyValue;
      propertyValueSet = pPropertyValueSet;
      required = pRequired;
      collection = pCollection;
      specialType = pSpecialType;
    }

  }

  public final Class<?> constructionClass;

  public final @Nullable Constructor<?> constructor;

  public final @Nullable Method method;

  public final @Nullable Method deleteMethod;

  public final @NotNull String[] filters;

  public final @NotNull Class<?>[] filterClasses;

  public final @NotNull String[] registrationClasses;

  public final @NotNull ConstructionArg[] constructionArgs;

  public ConstructorInfo(Class<?> pConstructionClass, @Nullable Constructor<?> pConstructor, @Nullable Method pMethod,
    @Nullable Method pDeleteMethod, @NotNull ConstructionArg[] pConstructionArgs, @NotNull String[] pFilters,
    @NotNull Class<?>[] pFilterClasses, @NotNull String[] pRegistrationClasses) {
    super();
    constructionClass = pConstructionClass;
    constructor = pConstructor;
    method = pMethod;
    deleteMethod = pDeleteMethod;
    constructionArgs = pConstructionArgs;
    filters = pFilters;
    filterClasses = pFilterClasses;
    registrationClasses = pRegistrationClasses;
  }

}

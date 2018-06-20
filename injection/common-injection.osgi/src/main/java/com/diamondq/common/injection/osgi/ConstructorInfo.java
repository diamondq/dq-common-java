package com.diamondq.common.injection.osgi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConstructorInfo {

	public static class ConstructionArg {

		public final Class<?>			argumentClass;

		public final @Nullable String	propertyFilterKey;

		public final @Nullable String	propertyValueKey;

		public final @Nullable Object	propertyValue;

		public final boolean			propertyValueSet;

		public final boolean			required;

		public ConstructionArg(Class<?> pArgumentClass, @Nullable String pPropertyFilterKey,
			@Nullable String pPropertyValueKey, @Nullable Object pPropertyValue, boolean pPropertyValueSet,
			boolean pRequired) {
			super();
			argumentClass = pArgumentClass;
			propertyFilterKey = pPropertyFilterKey;
			propertyValueKey = pPropertyValueKey;
			propertyValue = pPropertyValue;
			propertyValueSet = pPropertyValueSet;
			required = pRequired;
		}

	}

	public final Class<?>					constructionClass;

	public final @Nullable Constructor<?>	constructor;

	public final @Nullable Method			method;

	public final @NonNull String[]			filters;

	public final @NonNull Class<?>[]		filterClasses;

	public final @NonNull String[]			registrationClasses;

	public final @NonNull ConstructionArg[]	constructionArgs;

	public ConstructorInfo(Class<?> pConstructionClass, @Nullable Constructor<?> pConstructor, @Nullable Method pMethod,
		@NonNull ConstructionArg[] pConstructionArgs, @NonNull String[] pFilters, @NonNull Class<?>[] pFilterClasses,
		@NonNull String[] pRegistrationClasses) {
		super();
		constructionClass = pConstructionClass;
		constructor = pConstructor;
		method = pMethod;
		constructionArgs = pConstructionArgs;
		filters = pFilters;
		filterClasses = pFilterClasses;
		registrationClasses = pRegistrationClasses;
	}

}

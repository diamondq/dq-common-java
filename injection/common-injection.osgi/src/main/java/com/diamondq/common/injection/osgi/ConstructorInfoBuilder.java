package com.diamondq.common.injection.osgi;

import com.diamondq.common.injection.osgi.ConstructorInfo.ConstructionArg;
import com.diamondq.common.injection.osgi.i18n.Messages;
import com.diamondq.common.utils.misc.errors.ExtendedIllegalArgumentException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConstructorInfoBuilder {

	public static ConstructorInfoBuilder builder() {
		return new ConstructorInfoBuilder();
	}

	private List<ConstructionArg>	mConstructionArgs;

	private @Nullable Class<?>		mConstructionClass;

	private List<String>			mRegistrationClasses;

	private @Nullable String		mConstructionMethod;

	public static class ConstructorArgBuilder {

		private final ConstructorInfoBuilder	mBuilder;

		private @Nullable Class<?>				mClass;

		private @Nullable String				mFilter;

		private @Nullable String				mProperty;

		private @Nullable Boolean				mRequired;

		private @Nullable Object				mValue;

		private boolean							mValueSet	= false;

		public ConstructorArgBuilder(ConstructorInfoBuilder pConstructorInfoBuilder) {
			mBuilder = pConstructorInfoBuilder;
		}

		public ConstructorArgBuilder type(Class<?> pClass) {
			mClass = pClass;
			return this;
		}

		public ConstructorArgBuilder propFilter(String pFilter) {
			mFilter = pFilter;
			return this;
		}

		public ConstructorArgBuilder prop(String pValue) {
			mProperty = pValue;
			return this;
		}

		public ConstructorArgBuilder value(@Nullable Object pValue) {
			mValue = pValue;
			mValueSet = true;
			return this;
		}

		public ConstructorArgBuilder required() {
			mRequired = true;
			return this;
		}

		public ConstructorArgBuilder optional() {
			mRequired = false;
			return this;
		}

		public ConstructorInfoBuilder build() {
			Class<?> localClass = mClass;
			if (localClass == null)
				throw new ExtendedIllegalArgumentException(Messages.CONSTRUCTION_CLASS_REQUIRED);

			/* Make sure there is a way to find the argument */

			if ((mFilter == null) && (mProperty == null) && (mValueSet == false))
				throw new ExtendedIllegalArgumentException(Messages.ARG_VALUE_REQUIRED);

			Boolean requiredObj = mRequired;
			boolean required = (requiredObj == null ? true : requiredObj);

			if ((mValueSet == true) && (mValue == null) && (required == true))
				throw new ExtendedIllegalArgumentException(Messages.REQUIRED_VALUE_NULL);

			ConstructionArg arg = new ConstructionArg(localClass, mFilter, mProperty, mValue, mValueSet, required);
			mBuilder.mConstructionArgs.add(arg);
			return mBuilder;
		}

	}

	private ConstructorInfoBuilder() {
		mConstructionArgs = new ArrayList<>();
		mRegistrationClasses = new ArrayList<>();
	}

	public ConstructorArgBuilder cArg() {
		return new ConstructorArgBuilder(this);
	}

	public ConstructorInfoBuilder factoryMethod(String pMethod) {
		mConstructionMethod = pMethod;
		return this;
	}

	public ConstructorInfoBuilder constructorClass(Class<?> pClass) {
		mConstructionClass = pClass;
		return this;
	}

	public ConstructorInfoBuilder register(Class<?> pClass) {
		mRegistrationClasses.add(pClass.getName());
		return this;
	}

	public ConstructorInfo build() {
		Class<?> localConstructionClass = mConstructionClass;
		if (localConstructionClass == null)
			throw new ExtendedIllegalArgumentException(Messages.CONSTRUCTION_CLASS_REQUIRED);

		/* Calculate the filters and filterClasses */

		List<String> filterList = new ArrayList<>();
		List<Class<?>> filterClassList = new ArrayList<>();
		for (ConstructionArg arg : mConstructionArgs) {
			if (arg.propertyFilterKey != null) {
				filterList.add(arg.propertyFilterKey);
				filterClassList.add(arg.argumentClass);
			}
		}

		String localConstructionMethod = mConstructionMethod;
		@Nullable
		Constructor<?> constructor = null;
		@Nullable
		Method method = null;

		if (localConstructionMethod == null) {
			/* Figure out the constructor */

			Constructor<?>[] possibleConstructors = localConstructionClass.getConstructors();
			for (Constructor<?> possibleConstructor : possibleConstructors) {
				@NonNull
				Parameter[] parameters = possibleConstructor.getParameters();
				if (parameters.length != mConstructionArgs.size())
					continue;
				boolean match = true;
				for (int i = 0; i < parameters.length; i++) {
					Class<?> paramClass = parameters[i].getType();
					ConstructionArg arg = mConstructionArgs.get(i);
					if (paramClass.isAssignableFrom(arg.argumentClass) == false) {

//						if ((paramClass.isPrimitive()) && (paramClass.)
						/* Handle some basic conversions */
						/* TODO */

						match = false;
						break;
					}
				}
				if (match == false)
					continue;
				constructor = possibleConstructor;
			}
			if (constructor == null) {
				StringBuilder sb = new StringBuilder();
				sb.append('(');
				boolean first = true;
				for (ConstructionArg arg : mConstructionArgs) {
					if (first == true)
						first = false;
					else
						sb.append(", ");
					sb.append(arg.argumentClass.getName());
				}
				sb.append(')');
				throw new ExtendedIllegalArgumentException(Messages.NO_MATCHING_CONSTRUCTOR,
					localConstructionClass.getName(), sb.toString());
			}
		}
		else {
			/* Figure out the method */

			Method[] possibleMethods = localConstructionClass.getDeclaredMethods();
			for (Method possibleMethod : possibleMethods) {
				if (possibleMethod.getName().equals(localConstructionMethod) == false)
					continue;
				@NonNull
				Parameter[] parameters = possibleMethod.getParameters();
				if (parameters.length != mConstructionArgs.size())
					continue;
				boolean match = true;
				for (int i = 0; i < parameters.length; i++) {
					Class<?> paramClass = parameters[i].getType();
					ConstructionArg arg = mConstructionArgs.get(i);
					if (paramClass.isAssignableFrom(arg.argumentClass) == false) {
						match = false;
						break;
					}
				}
				if (match == false)
					continue;
				method = possibleMethod;
			}
			if (method == null)
				throw new ExtendedIllegalArgumentException(Messages.NO_MATCHING_METHOD, localConstructionMethod);
		}

		return new ConstructorInfo(localConstructionClass, constructor, method,
			mConstructionArgs.toArray(new @NonNull ConstructionArg[0]), filterList.toArray(new @NonNull String[0]),
			filterClassList.toArray(new @NonNull Class[0]), mRegistrationClasses.toArray(new @NonNull String[0]));
	}

}
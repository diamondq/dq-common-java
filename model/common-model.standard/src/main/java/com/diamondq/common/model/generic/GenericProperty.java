package com.diamondq.common.model.generic;

import com.diamondq.common.lambda.MemoizedSupplier;
import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.Script;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureRef;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.jdt.annotation.NonNull;

public class GenericProperty<@Nullable TYPE> implements Property<TYPE> {

	private final PropertyDefinition				mPropertyDefinition;

	private final boolean							mValueSet;

	private final TYPE								mValue;

	private final @Nullable MemoizedSupplier<TYPE>	mSupplier;

	public GenericProperty(PropertyDefinition pPropertyDefinition, boolean pValueSet, TYPE pValue,
		@Nullable Supplier<TYPE> pSupplier) {
		super();
		mPropertyDefinition = pPropertyDefinition;
		mValueSet = pValueSet;
		mValue = pValue;
		mSupplier = (pSupplier == null ? null : new MemoizedSupplier<TYPE>(pSupplier));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#getValue(com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public TYPE getValue(Structure pContainer) {
		if (mValueSet == true) {
			if (mSupplier != null)
				return mSupplier.getValue();
			else
				return mValue;
		}

		if (mPropertyDefinition.getKeywords().containsKey(CommonKeywordKeys.INHERIT_PARENT) == false) {
			StructureRef parentRef = pContainer.getParentRef();
			if (parentRef != null) {
				Structure parent = parentRef.resolve();
				if (parent != null) {
					Property<TYPE> parentProperty = parent.lookupPropertyByName(mPropertyDefinition.getName());
					if (parentProperty != null)
						if (parentProperty.getDefinition().isFinal() == false)
							if (parentProperty.getDefinition().getType() == mPropertyDefinition.getType())
								return parentProperty.getValue(parent);
				}
			}
		}

		/* Process defaults (if present) */

		String defaultValue = mPropertyDefinition.getDefaultValue();
		if (defaultValue == null) {

			Script defaultScript = mPropertyDefinition.getDefaultValueScript();
			if (defaultScript != null) {
				Object scriptResult = defaultScript.evaluate(this);
				if (scriptResult != null)
					defaultValue = scriptResult.toString();
			}
		}

		if (defaultValue != null) {

			/* Typecast it back to the defined type */

			switch (mPropertyDefinition.getType()) {
			case String: {
				@SuppressWarnings("unchecked")
				TYPE defaultResult = (TYPE) defaultValue;
				return defaultResult;
			}
			case Integer: {
				@SuppressWarnings("unchecked")
				TYPE defaultResult = (TYPE) (Integer) Integer.parseInt(defaultValue);
				return defaultResult;
			}
			case Long: {
				@SuppressWarnings("unchecked")
				TYPE defaultResult = (TYPE) (Long) Long.parseLong(defaultValue);
				return defaultResult;
			}
			case Decimal: {
				@SuppressWarnings("unchecked")
				TYPE defaultResult = (TYPE) new BigDecimal(defaultValue);
				return defaultResult;
			}
			case Boolean: {
				@SuppressWarnings("unchecked")
				TYPE defaultResult = (TYPE) (Boolean) Boolean.parseBoolean(defaultValue);
				return defaultResult;
			}
			case Timestamp:
			case Binary:
			case Image:
			case EmbeddedStructureList:
			case PropertyRef:
			case StructureRef:
			case StructureRefList: {
				return null;
			}
			}
		}

		return null;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#isValueSet()
	 */
	@Override
	public boolean isValueSet() {
		return mValueSet;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#setValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Property<TYPE> setValue(TYPE pValue) {

		/*
		 * Because the TYPE is a Generic, and can be lost in type-erasure or can be modeled as a <?>, we should see if
		 * we need to 'convert' the data type before passing it to the constructor
		 */

		if (pValue != null) {
			switch (mPropertyDefinition.getType()) {
			case Binary:
				break;
			case Boolean: {
				if (pValue instanceof Boolean)
					break;
				if (pValue instanceof String) {
					pValue = (TYPE) (Boolean) Boolean.parseBoolean((String) pValue);
					break;
				}
				throw new IllegalArgumentException("A Boolean Property must only be passed a Boolean or a String");
			}
			case Decimal: {
				if (pValue instanceof BigDecimal)
					break;
				if (pValue instanceof Short) {
					pValue = (TYPE) new BigDecimal((Short) pValue);
					break;
				}
				if (pValue instanceof Long) {
					pValue = (TYPE) BigDecimal.valueOf((Long) pValue);
					break;
				}
				if (pValue instanceof Integer) {
					pValue = (TYPE) new BigDecimal((Integer) pValue);
					break;
				}
				if (pValue instanceof Float) {
					pValue = (TYPE) new BigDecimal((Float) pValue);
					break;
				}
				if (pValue instanceof Double) {
					pValue = (TYPE) BigDecimal.valueOf((Double) pValue);
					break;
				}
				if (pValue instanceof String) {
					pValue = (TYPE) new BigDecimal((String) pValue);
					break;
				}
				throw new IllegalArgumentException(
					"A Decimal Property must be passed a BigDecimal, Short, Long, Integer, Float, Double or String");
			}
			case EmbeddedStructureList:
				break;
			case Image:
				break;
			case Integer: {
				if (pValue instanceof Integer)
					break;
				if (pValue instanceof Long) {
					if (((Long) pValue).longValue() > Integer.MAX_VALUE)
						throw new IllegalArgumentException(
							"An Integer Property can only be passed a Long that is less than a MAX Integer");
					pValue = (TYPE) (Integer) ((Long) pValue).intValue();
					break;
				}
				if (pValue instanceof String) {
					pValue = (TYPE) (Integer) Integer.parseInt((String) pValue);
					break;
				}
				throw new IllegalArgumentException("An Integer Property must be passed an Integer, Long or a String");
			}
			case Long: {
				if (pValue instanceof Long)
					break;
				if (pValue instanceof Integer) {
					pValue = (TYPE) (Long) ((Integer) pValue).longValue();
					break;
				}
				if (pValue instanceof String) {
					pValue = (TYPE) (Integer) Integer.parseInt((String) pValue);
					break;
				}
				throw new IllegalArgumentException("An Long Property must be passed an Integer, Long or a String");
			}
			case PropertyRef: {
				if (pValue instanceof PropertyRef)
					break;
				if (pValue instanceof String) {
					pValue = (TYPE) mPropertyDefinition.getScope().getToolkit()
						.createPropertyRefFromSerialized(mPropertyDefinition.getScope(), (String) pValue);
					break;
				}
				throw new IllegalArgumentException("A PropertyRef Property must be passed a PropertyRef or a String");
			}
			case String: {
				if (pValue instanceof String)
					break;
				pValue = (TYPE) pValue.toString();
				break;
			}
			case StructureRef: {
				if (pValue instanceof StructureRef)
					break;
				if (pValue instanceof String) {
					pValue = (TYPE) mPropertyDefinition.getScope().getToolkit()
						.createStructureRefFromSerialized(mPropertyDefinition.getScope(), (String) pValue);
					break;
				}
				throw new IllegalArgumentException("A StructureRef Property must be passed a String");
			}
			case StructureRefList: {
				if (pValue instanceof List)
					break;
				throw new IllegalArgumentException("A StructureRefList Property must be passed a List");
			}
			case Timestamp: {
				if (pValue instanceof Long)
					break;
				if (pValue instanceof Integer) {
					@SuppressWarnings("cast")
					long asLong = Long.valueOf((long) (Integer) pValue);
					pValue = (TYPE) (Long) asLong;
					break;
				}
				if (pValue instanceof String) {
					pValue = (TYPE) Long.valueOf((String) pValue);
					break;
				}
				if (pValue instanceof Date) {
					pValue = (TYPE) (Long) (((Date) pValue).getTime());
					break;
				}
				throw new IllegalArgumentException("A Timestamp Property must be passed a Long, Integer or String");
			}
			}
		}

		return new GenericProperty<TYPE>(mPropertyDefinition, true, pValue, null);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#clearValueSet()
	 */
	@Override
	public @NonNull Property<@Nullable TYPE> clearValueSet() {
		return new GenericProperty<TYPE>(mPropertyDefinition, false, null, null);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#setLazyLoadSupplier(java.util.function.Supplier)
	 */
	@Override
	public Property<TYPE> setLazyLoadSupplier(@Nullable Supplier<TYPE> pSupplier) {
		return new GenericProperty<TYPE>(mPropertyDefinition, (pSupplier == null ? mValueSet : true), mValue,
			pSupplier);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#getDefinition()
	 */
	@Override
	public PropertyDefinition getDefinition() {
		return mPropertyDefinition;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(mPropertyDefinition, mValue, mValueSet, mSupplier);
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
		GenericProperty<?> other = (GenericProperty<?>) pObj;
		return Objects.equals(mPropertyDefinition, other.mPropertyDefinition) && Objects.equals(mValue, other.mValue)
			&& Objects.equals(mValueSet, other.mValueSet) && Objects.equals(mSupplier, other.mSupplier);
	}
}

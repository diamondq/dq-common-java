package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Script;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureRef;

import java.math.BigDecimal;
import java.util.Objects;

public class GenericProperty<TYPE> implements Property<TYPE> {

	private final PropertyDefinition	mPropertyDefinition;

	private final boolean				mValueSet;

	private final TYPE					mValue;

	public GenericProperty(PropertyDefinition pPropertyDefinition, boolean pValueSet, TYPE pValue) {
		super();
		mPropertyDefinition = pPropertyDefinition;
		mValueSet = pValueSet;
		mValue = pValue;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#getValue(com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public TYPE getValue(Structure pContainer) {
		if (mValueSet == true)
			return mValue;

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
			case Binary:
			case Image:
			case EmbeddedStructureList:
			case PropertyRef:
			case StructureRef:
			case StructureRefList:
				return null;
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
	@Override
	public Property<TYPE> setValue(TYPE pValue) {
		return new GenericProperty<TYPE>(mPropertyDefinition, true, pValue);
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
		return Objects.hash(mPropertyDefinition, mValue, mValueSet);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object pObj) {
		if (this == pObj)
			return true;
		if (pObj == null)
			return false;
		if (getClass() != pObj.getClass())
			return false;
		GenericProperty<?> other = (GenericProperty<?>) pObj;
		return Objects.equals(mPropertyDefinition, other.mPropertyDefinition) && Objects.equals(mValue, other.mValue)
			&& Objects.equals(mValueSet, other.mValueSet);
	}
}

package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Structure;

import java.util.Objects;

public class GenericSDNameProperty implements Property<String> {

	private final PropertyDefinition mPropertyDefinition;

	public GenericSDNameProperty(PropertyDefinition pPropertyDefinition) {
		mPropertyDefinition = pPropertyDefinition;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#getValue(com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public String getValue(Structure pContainer) {
		return pContainer.getDefinition().getName();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#isValueSet()
	 */
	@Override
	public boolean isValueSet() {
		return true;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Property#setValue(java.lang.Object)
	 */
	@Override
	public Property<String> setValue(String pValue) {
		throw new IllegalArgumentException();
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
		return Objects.hash(mPropertyDefinition);
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
		GenericSDNameProperty other = (GenericSDNameProperty) pObj;
		return Objects.equals(mPropertyDefinition, other.mPropertyDefinition);
	}

}

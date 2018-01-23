package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.Structure;

import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

public class StandardSetValueMigration implements BiFunction<Structure, Structure, Structure> {

	private final String	mColumnName;

	private final Object	mNewValue;

	public StandardSetValueMigration(String pColumnName, Object pNewValue) {
		mColumnName = pColumnName;
		mNewValue = pNewValue;
	}

	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Structure apply(Structure pOld, Structure pNew) {
		Property<@Nullable Object> newProperty = pNew.lookupMandatoryPropertyByName(mColumnName);
		pNew = pNew.updateProperty(newProperty.setValue(mNewValue));

		return pNew;
	}

}

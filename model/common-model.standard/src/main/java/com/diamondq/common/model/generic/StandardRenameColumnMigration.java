package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.Structure;

import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

public class StandardRenameColumnMigration implements BiFunction<Structure, Structure, Structure> {

	private final String	mOldColumnName;

	private final String	mNewColumnName;

	public StandardRenameColumnMigration(String pOldColumnName, String pNewColumnName) {
		mOldColumnName = pOldColumnName;
		mNewColumnName = pNewColumnName;
	}

	/**
	 * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Structure apply(Structure pOld, Structure pNew) {
		Property<@Nullable Object> oldProperty = pOld.lookupPropertyByName(mOldColumnName);
		if (oldProperty == null)
			return pNew;
		Property<@Nullable Object> newProperty = pNew.lookupMandatoryPropertyByName(mNewColumnName);
		if (oldProperty.isValueSet() == true)
			pNew = pNew.updateProperty(newProperty.setValue(oldProperty.getValue(pOld)));
		else
			pNew = pNew.updateProperty(newProperty.clearValueSet());

		return pNew;
	}

}

package com.diamondq.common.model.interfaces;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Property<TYPE> {

	/* Value */

	/**
	 * Returns the value. If this property actually has the value set, then the value is returned. If not, then if the
	 * Structure has a parent Structure with a matching Property, then it's value is used. If not, then if a default
	 * value is available, then it's returned.
	 * 
	 * @param pContainer the containing structure.
	 * @return the value
	 */
	@Nullable
	public TYPE getValue(Structure pContainer);

	/**
	 * Returns whether the value is set
	 * 
	 * @return true if it is set or false otherwise
	 */
	public boolean isValueSet();

	@CheckReturnValue
	@Nonnull
	public Property<TYPE> setValue(TYPE pValue);

	/* Definition */

	/**
	 * Returns the PropertyDefinition for the given Property. NOTE: Since all Properties must be created with a
	 * PropertyDefinition, this will never return null.
	 * 
	 * @return the PropertyDefinition
	 */
	@Nonnull
	public PropertyDefinition getDefinition();

}
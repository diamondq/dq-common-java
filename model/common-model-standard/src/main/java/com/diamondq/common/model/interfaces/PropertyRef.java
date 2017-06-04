package com.diamondq.common.model.interfaces;

import javax.annotation.Nullable;

public interface PropertyRef<T> extends Ref<Property<T>> {

	/**
	 * Resolves this reference into the containing Structure
	 * 
	 * @return the object
	 */
	@Nullable
	public Structure resolveToStructure();

	/**
	 * Resolves this reference into a Structure and a Property
	 * 
	 * @return the StructureAndProperty
	 */
	public StructureAndProperty<T> resolveToBoth();

}

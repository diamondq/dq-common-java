package com.diamondq.common.model.interfaces;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface PropertyRef<T> extends Ref<Property<T>> {

	/**
	 * Resolves this reference into the containing Structure
	 * 
	 * @return the object
	 */
	public @Nullable Structure resolveToStructure();

	/**
	 * Resolves this reference into a Structure and a Property
	 * 
	 * @return the StructureAndProperty
	 */
	public @Nullable StructureAndProperty<T> resolveToBoth();

}

package com.diamondq.common.model.interfaces;

import java.util.Collection;
import java.util.Map;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Structure extends Resolvable<Structure, StructureRef> {

	/* Definition */

	@Nonnull
	public StructureDefinition getDefinition();

	/* Virtual Property: Local Name */

	/**
	 * Returns the primary key of this object, but scoped to within it's StructureDefinition and/or it's Container (ie.
	 * it's not a full reference; use {@link #getReference()} for that). This is a virtual property, as it's tied to the
	 * set of actual Properties who are marked as primary keys.
	 * 
	 * @return the unique name
	 */
	@Nullable
	public String getLocalName();

	/* Virtual Property: parent */

	/**
	 * Returns the reference to the parent. if present. This is a virtual property, as it's tied to an actual Property
	 * who has the {@value CommonKeywordKeys#INHERIT_PARENT}/{@value CommonKeywordValues#TRUE} keyword set. NOTE: A
	 * parent structure is used for inheritance of values, and does not represent containment.
	 * 
	 * @return the parent reference
	 */
	@Nullable
	public StructureRef getParentRef();

	/* Virtual Property: container */

	/**
	 * Returns the reference to the container, if present. This is a virtual property, as it's tied to an actual
	 * Property who has the {@value CommonKeywordKeys#CONTAINER}/{@value CommonKeywordValues#CONTAINER_PARENT} keyword
	 * set.
	 * 
	 * @return the container reference or null
	 */
	@Nullable
	public <T> PropertyRef<T> getContainerRef();

	/* Properties */

	@Nonnull
	public Map<String, Property<?>> getProperties();

	@CheckReturnValue
	@Nonnull
	public Structure updateProperty(@Nonnull Property<?> pValue);

	/**
	 * Returns a property with the given name
	 * 
	 * @param pName the name of the property
	 * @return the Property or null
	 */
	@Nullable
	public <T> Property<T> lookupPropertyByName(@Nonnull String pName);

	/**
	 * Returns back the set of Property's that match the given keyword
	 * 
	 * @param pKey the key
	 * @param pValue the value (or null if any value is valid)
	 * @param pType the type (or null if any type is valid)
	 * @return the collection of Properties
	 */
	@Nullable
	public <T> Collection<Property<T>> lookupPropertiesByKeyword(@Nonnull String pKey, @Nullable String pValue,
		@Nullable PropertyType pType);

}
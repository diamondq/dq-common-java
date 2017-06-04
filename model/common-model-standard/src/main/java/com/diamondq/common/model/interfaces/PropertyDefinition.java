package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A PropertyDefinition defines all metadata about a given property.
 */
public interface PropertyDefinition
	extends ResolvableWithContainer<PropertyDefinition, PropertyDefinitionRef, StructureDefinition> {

	/* Name */

	@Nullable
	public String getName();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setName(@Nonnull String pValue);

	/* Label */

	@Nullable
	public TranslatableString getLabel();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setLabel(@Nullable TranslatableString pValue);

	/* Primary Key */

	public boolean isPrimaryKey();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setPrimaryKey(boolean pValue);

	/* Type */

	@Nullable
	public PropertyType getType();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setType(@Nonnull PropertyType pValue);

	/* Validation Script */

	@Nullable
	public Script getValidationScript();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setValidationScript(@Nullable Script pValue);

	/* Default Value */

	@Nullable
	public String getDefaultValue();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setDefaultValue(@Nullable String pValue);

	/* Default Value Script */

	@Nullable
	public Script getDefaultValueScript();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setDefaultValueScript(@Nullable Script pValue);

	/* Reference Type */

	@Nonnull
	public Collection<StructureDefinitionRef> getReferenceTypes();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition addReferenceType(@Nonnull StructureDefinitionRef pValue);

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition removeReferenceType(@Nonnull StructureDefinitionRef pValue);

	/* Min Value */

	@Nullable
	public BigDecimal getMinValue();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setMinValue(@Nullable BigDecimal pValue);

	/* Max Value */

	@Nullable
	public BigDecimal getMaxValue();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setMaxValue(@Nullable BigDecimal pValue);

	/* Final */

	public boolean isFinal();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setFinal(boolean pValue);

	/* keywords */

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs).
	 * 
	 * @return the multimap
	 */
	@Nonnull
	public Multimap<String, String> getKeywords();

	/**
	 * Adds a new keyword to this StructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated PropertyDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public PropertyDefinition addKeyword(@Nonnull String pKey, @Nonnull String pValue);

	/**
	 * Removes a keyword from this StructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated PropertyDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public PropertyDefinition removeKeyword(@Nonnull String pKey, @Nonnull String pValue);

	/**
	 * Returns the order of this property with other primary keys. Only applies if this property is a primary key
	 * 
	 * @return the order (returns 0 if it's not a primary key)
	 */
	public int getPrimaryKeyOrder();

	/**
	 * Changes the primary order of this primary with other primary keys.
	 * 
	 * @param pOrder the new order
	 * @return the updated PropertyDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setPrimaryKeyOrder(int pOrder);

	@Nonnull
	public PropertyPattern getPropertyPattern();

	@CheckReturnValue
	@Nonnull
	public PropertyDefinition setPropertyPattern(@Nonnull PropertyPattern pValue);
}

package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;

import java.math.BigDecimal;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * A PropertyDefinition defines all metadata about a given property.
 */
public interface PropertyDefinition
	extends ResolvableWithContainer<PropertyDefinition, PropertyDefinitionRef, StructureDefinition> {

	/* Name */

	@Nullable
	public String getName();

	
	
	public PropertyDefinition setName( String pValue);

	/* Label */

	@Nullable
	public TranslatableString getLabel();

	
	
	public PropertyDefinition setLabel(@Nullable TranslatableString pValue);

	/* Primary Key */

	public boolean isPrimaryKey();

	
	
	public PropertyDefinition setPrimaryKey(boolean pValue);

	/* Type */

	@Nullable
	public PropertyType getType();

	
	
	public PropertyDefinition setType( PropertyType pValue);

	/* Validation Script */

	@Nullable
	public Script getValidationScript();

	
	
	public PropertyDefinition setValidationScript(@Nullable Script pValue);

	/* Default Value */

	@Nullable
	public String getDefaultValue();

	
	
	public PropertyDefinition setDefaultValue(@Nullable String pValue);

	/* Default Value Script */

	@Nullable
	public Script getDefaultValueScript();

	
	
	public PropertyDefinition setDefaultValueScript(@Nullable Script pValue);

	/* Reference Type */

	
	public Collection<StructureDefinitionRef> getReferenceTypes();

	
	
	public PropertyDefinition addReferenceType( StructureDefinitionRef pValue);

	
	
	public PropertyDefinition removeReferenceType( StructureDefinitionRef pValue);

	/* Min Value */

	@Nullable
	public BigDecimal getMinValue();

	
	
	public PropertyDefinition setMinValue(@Nullable BigDecimal pValue);

	/* Max Value */

	@Nullable
	public BigDecimal getMaxValue();

	
	
	public PropertyDefinition setMaxValue(@Nullable BigDecimal pValue);

	/* Final */

	public boolean isFinal();

	
	
	public PropertyDefinition setFinal(boolean pValue);

	/* Max Length */
	
	@Nullable
	public Integer getMaxLength();
	
	
	
	public PropertyDefinition setMaxLength(Integer pValue);
	
	/* keywords */

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs).
	 * 
	 * @return the multimap
	 */
	
	public Multimap<String, String> getKeywords();

	/**
	 * Adds a new keyword to this StructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated PropertyDefinition
	 */
	
	
	public PropertyDefinition addKeyword( String pKey,  String pValue);

	/**
	 * Removes a keyword from this StructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated PropertyDefinition
	 */
	
	
	public PropertyDefinition removeKeyword( String pKey,  String pValue);

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
	
	
	public PropertyDefinition setPrimaryKeyOrder(int pOrder);

	
	public PropertyPattern getPropertyPattern();

	
	
	public PropertyDefinition setPropertyPattern( PropertyPattern pValue);
	
	
	public Scope getScope();
}

package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * A PropertyDefinition defines all metadata about a given property.
 */
public interface PropertyDefinition
  extends ResolvableWithContainer<PropertyDefinition, PropertyDefinitionRef, StructureDefinition> {

  /* Name */

  public String getName();

  /* Label */

  public @Nullable TranslatableString getLabel();

  public PropertyDefinition setLabel(@Nullable TranslatableString pValue);

  /* Primary Key */

  public boolean isPrimaryKey();

  public PropertyDefinition setPrimaryKey(boolean pValue);

  /* Type */

  public PropertyType getType();

  /* Validation Script */

  public @Nullable Script getValidationScript();

  public PropertyDefinition setValidationScript(@Nullable Script pValue);

  /* Default Value */

  public @Nullable String getDefaultValue();

  public PropertyDefinition setDefaultValue(@Nullable String pValue);

  /* Default Value Script */

  public @Nullable Script getDefaultValueScript();

  public PropertyDefinition setDefaultValueScript(@Nullable Script pValue);

  /* Reference Type */

  public Collection<StructureDefinitionRef> getReferenceTypes();

  public PropertyDefinition addReferenceType(StructureDefinitionRef pValue);

  public PropertyDefinition removeReferenceType(StructureDefinitionRef pValue);

  /* Min Value */

  public @Nullable BigDecimal getMinValue();

  public PropertyDefinition setMinValue(@Nullable BigDecimal pValue);

  /* Max Value */

  public @Nullable BigDecimal getMaxValue();

  public PropertyDefinition setMaxValue(@Nullable BigDecimal pValue);

  /* Final */

  public boolean isFinal();

  public PropertyDefinition setFinal(boolean pValue);

  /* Max Length */

  public @Nullable Integer getMaxLength();

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
  public PropertyDefinition addKeyword(String pKey, String pValue);

  /**
   * Removes a keyword from this StructureDefinition
   *
   * @param pKey the key
   * @param pValue the value
   * @return the updated PropertyDefinition
   */
  public PropertyDefinition removeKeyword(String pKey, String pValue);

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

  public PropertyDefinition setPropertyPattern(PropertyPattern pValue);

  public @Nullable BigDecimal getAutoIncrementStart();

  public @Nullable BigDecimal getAutoIncrementBy();

  public PropertyDefinition setAutoIncrement(@Nullable BigDecimal pStart, @Nullable BigDecimal pIncrementBy);

  public Scope getScope();

  public byte[] saveToByteArray();
}

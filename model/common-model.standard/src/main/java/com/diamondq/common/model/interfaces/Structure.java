package com.diamondq.common.model.interfaces;

import java.util.Collection;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Structure extends Resolvable<Structure, StructureRef> {

  /* Definition */

  public StructureDefinition getDefinition();

  /* Virtual Property: Local Name */

  /**
   * Returns the primary key of this object, but scoped to within it's StructureDefinition and/or it's Container (ie.
   * it's not a full reference; use {@link #getReference()} for that). This is a virtual property, as it's tied to the
   * set of actual Properties who are marked as primary keys.
   * 
   * @return the unique name
   */
  public @Nullable String getLocalName();

  /* Virtual Property: parent */

  /**
   * Returns the reference to the parent. if present. This is a virtual property, as it's tied to an actual Property who
   * has the {@value CommonKeywordKeys#INHERIT_PARENT}/{@value CommonKeywordValues#TRUE} keyword set. NOTE: A parent
   * structure is used for inheritance of values, and does not represent containment.
   * 
   * @return the parent reference
   */
  public @Nullable StructureRef getParentRef();

  /* Virtual Property: container */

  /**
   * Returns the reference to the container, if present. This is a virtual property, as it's tied to an actual Property
   * who has the {@value CommonKeywordKeys#CONTAINER}/{@value CommonKeywordValues#CONTAINER_PARENT} keyword set.
   * 
   * @return the container reference or null
   */
  public <@Nullable T> @Nullable PropertyRef<T> getContainerRef();

  /* Properties */

  public Map<String, Property<?>> getProperties();

  public Structure updateProperty(Property<?> pValue);

  /**
   * Returns a property with the given name
   * 
   * @param pName the name of the property
   * @return the Property or null
   */
  public <@Nullable T> @Nullable Property<T> lookupPropertyByName(String pName);

  /**
   * Returns a property with the given name. If the property doesn't exist then an IllegalArgumentException is thrown
   * indicating that the property is mandatory.
   * 
   * @param pName the name of the property
   * @return the Property
   */
  public <@Nullable T> Property<T> lookupMandatoryPropertyByName(String pName);

  /**
   * Returns back the set of Property's that match the given keyword
   * 
   * @param pKey the key
   * @param pValue the value (or null if any value is valid)
   * @param pType the type (or null if any type is valid)
   * @return the collection of Properties
   */
  public <@Nullable T> Collection<Property<T>> lookupPropertiesByKeyword(String pKey, @Nullable String pValue,
    @Nullable PropertyType pType);

}

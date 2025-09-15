package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface Structure extends Resolvable<Structure, StructureRef> {

  /* Definition */

  StructureDefinition getDefinition();

  /* Virtual Property: Local Name */

  /**
   * Returns the primary key of this object, but scoped to within it's StructureDefinition and/or it's Container (i.e.,
   * it's not a full reference; use {@link #getReference()} for that). This is a virtual property, as it's tied to the
   * set of actual Properties that are marked as primary keys.
   *
   * @return the unique name
   */
  @Nullable
  String getLocalName();

  /* Virtual Property: parent */

  /**
   * Returns the reference to the parent, if present. <br/> This is a virtual property, as it's tied to an actual
   * Property <br/> who has the {@value CommonKeywordKeys#INHERIT_PARENT}/{@value CommonKeywordValues#TRUE} keyword set.
   * <br/> NOTE: A parent structure is used for inheritance of values and does not represent containment.
   *
   * @return the parent reference
   */
  @Nullable
  StructureRef getParentRef();

  /* Virtual Property: container */

  /**
   * Returns the reference to the container, if present. This is a virtual property, as it's tied to an actual Property
   * who has the {@value CommonKeywordKeys#CONTAINER}/{@value CommonKeywordValues#CONTAINER_PARENT} keyword set.
   *
   * @return the container reference or null
   */
  <T extends @Nullable Object> @Nullable PropertyRef<T> getContainerRef();

  /* Properties */

  Map<String, Property<?>> getProperties();

  Structure updateProperty(Property<?> pValue);

  /**
   * Returns a property with the given name
   *
   * @param pName the name of the property
   * @return property or null
   */
  <T extends @Nullable Object> @Nullable Property<T> lookupPropertyByName(String pName);

  /**
   * Returns a property with the given name.  <br/>If the property doesn't exist, then an IllegalArgumentException is
   * thrown indicating that the property is mandatory.
   *
   * @param pName the name of the property
   * @return the Property
   */
  <T extends @Nullable Object> Property<T> lookupMandatoryPropertyByName(String pName);

  /**
   * Returns the set of Property's that matches the given keyword
   *
   * @param pKey the key
   * @param pValue the value (or null if any value is valid)
   * @param pType the type (or null if any type is valid)
   * @return the collection of Properties
   */
  <T extends @Nullable Object> Collection<Property<T>> lookupPropertiesByKeyword(String pKey, @Nullable String pValue,
    @Nullable PropertyType pType);

}

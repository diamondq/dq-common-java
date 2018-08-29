package com.diamondq.common.model.interfaces;

/**
 * Represents a reference to a StructureDefinition
 */
public interface StructureDefinitionRef extends Ref<StructureDefinition> {

  /**
   * Returns true if this is a wildcard reference (ie. does not refer to a revision)
   *
   * @return true if it is a wildcard or false otherwise
   */
  public boolean isWildcardReference();

  /**
   * Returns a wildcard reference version of this reference (or the same reference if it's already a wildcard reference)
   *
   * @return the wildcard reference version of this reference.
   */
  public StructureDefinitionRef getWildcardReference();

}

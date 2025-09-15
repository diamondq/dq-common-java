package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

public interface PropertyRef<T extends @Nullable Object> extends Ref<Property<T>> {

  /**
   * Resolves this reference into the containing Structure
   *
   * @return the object
   */
  @Nullable
  Structure resolveToStructure();

  /**
   * Resolves this reference into a Structure and a Property
   *
   * @return structureAndProperty
   */
  @Nullable
  StructureAndProperty<T> resolveToBoth();

}

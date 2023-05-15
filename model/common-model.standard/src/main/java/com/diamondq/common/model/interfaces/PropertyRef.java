package com.diamondq.common.model.interfaces;

import org.jetbrains.annotations.Nullable;

public interface PropertyRef<@Nullable T> extends Ref<Property<T>> {

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

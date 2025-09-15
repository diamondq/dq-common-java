package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

public class StructureAndProperty<T extends @Nullable Object> {

  public final Structure structure;

  @Nullable public final Property<T> property;

  public StructureAndProperty(Structure pStructure, @Nullable Property<T> pProperty) {
    structure = pStructure;
    property = pProperty;
  }

}

package com.diamondq.common.model.interfaces;

import org.jetbrains.annotations.Nullable;

public class StructureAndProperty<@Nullable T> {

  public final Structure structure;

  @Nullable public final Property<T> property;

  public StructureAndProperty(Structure pStructure, @Nullable Property<T> pProperty) {
    structure = pStructure;
    property = pProperty;
  }

}

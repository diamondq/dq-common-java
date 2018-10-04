package com.diamondq.common.model.interfaces;

import com.diamondq.common.storage.kv.Query;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ModelQuery extends Query {
  public StructureDefinition getStructureDefinition();

  public @Nullable PropertyDefinition getParentPropertyDefinition();

}

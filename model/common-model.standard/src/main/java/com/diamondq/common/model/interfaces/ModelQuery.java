package com.diamondq.common.model.interfaces;

import com.diamondq.common.storage.kv.Query;
import org.jetbrains.annotations.Nullable;

public interface ModelQuery extends Query {
  public StructureDefinition getStructureDefinition();

  public @Nullable PropertyDefinition getParentPropertyDefinition();

}

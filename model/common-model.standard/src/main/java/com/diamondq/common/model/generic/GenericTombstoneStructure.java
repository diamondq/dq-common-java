package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.Tombstone;

import java.util.Map;

public class GenericTombstoneStructure extends GenericStructure implements Tombstone {

  public GenericTombstoneStructure(Scope pScope, StructureDefinition pDefinition, Map<String, Property<?>> pProps) {
    super(pScope, pDefinition, pProps);
  }

}

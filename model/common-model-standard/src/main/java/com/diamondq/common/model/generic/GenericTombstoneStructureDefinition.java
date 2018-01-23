package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Tombstone;

public class GenericTombstoneStructureDefinition extends GenericStructureDefinition implements Tombstone {

	public GenericTombstoneStructureDefinition(Scope pScope, String pName) {
		super(pScope, pName, -1, null, false, null, null, null);
	}

}

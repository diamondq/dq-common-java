package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;

public class GenericStructureDefinitionRef extends AbstractRef<StructureDefinition> implements StructureDefinitionRef {

	public GenericStructureDefinitionRef(Scope pScope, String pId) {
		super(pScope, pId, StructureDefinition.class);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Ref#resolve()
	 */
	@Override
	public StructureDefinition resolve() {
		return mScope.getToolkit().lookupStructureDefinitionByName(mScope, mId);
	}
}

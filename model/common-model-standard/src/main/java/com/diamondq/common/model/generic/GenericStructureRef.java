package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureRef;

public class GenericStructureRef extends AbstractRef<Structure> implements StructureRef {

	public GenericStructureRef(Scope pScope, String pName) {
		super(pScope, pName, Structure.class);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Ref#resolve()
	 */
	@Override
	public Structure resolve() {
		return mScope.getToolkit().lookupStructureBySerializedRef(mScope, mId);
	}

}

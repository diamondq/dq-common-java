package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;

public class GenericPropertyDefinitionRef extends AbstractRef<PropertyDefinition> implements PropertyDefinitionRef {

	public GenericPropertyDefinitionRef(Scope pScope, String pName) {
		super(pScope, pName, PropertyDefinition.class);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Ref#resolve()
	 */
	@Override
	public PropertyDefinition resolve() {
		int offset = mId.indexOf('#');
		StructureDefinition sd;
		sd = mScope.getToolkit().lookupStructureDefinitionByName(mScope, mId.substring(0, offset));
		return sd.lookupPropertyDefinitionByName(mId.substring(offset + 1));
	}

}

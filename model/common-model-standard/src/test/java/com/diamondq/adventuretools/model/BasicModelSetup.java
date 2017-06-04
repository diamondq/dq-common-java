package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;

public class BasicModelSetup {

	public static void setup(Toolkit toolkit, Scope scope) {

		/* Map Anchor */

		StructureDefinition mapAnchorDef =
			toolkit.createNewStructureDefinition(scope, "mapAnchor").setSingleInstance(false);

		mapAnchorDef = mapAnchorDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope).setName("name")
			.setPrimaryKey(true).setType(PropertyType.String));
		mapAnchorDef = mapAnchorDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope).setName("x")
			.setDefaultValue("0").setFinal(true).setType(PropertyType.Integer));
		mapAnchorDef = mapAnchorDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope).setName("y")
			.setDefaultValue("0").setFinal(true).setType(PropertyType.Integer));
		mapAnchorDef = mapAnchorDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope).setName("z")
			.setDefaultValue("0").setFinal(true).setType(PropertyType.Integer));

		toolkit.writeStructureDefinition(scope, mapAnchorDef);

		StructureDefinitionRef mapAnchorRef =
			toolkit.lookupStructureDefinitionByName(scope, "mapAnchor").getReference();

		/* Zone */

		StructureDefinition definition = toolkit.createNewStructureDefinition(scope, "zone").setSingleInstance(false);

		definition = definition.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope).setName("name")
			.setPrimaryKey(true).setType(PropertyType.String));
		definition = definition.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope).setName("mapAnchorId")
			.setType(PropertyType.StructureRef).addReferenceType(mapAnchorRef));
		definition = definition.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope).setName("dimensionId").setType(PropertyType.Integer));

		toolkit.writeStructureDefinition(scope, definition);
	}
}

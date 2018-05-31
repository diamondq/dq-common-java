package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;

import org.junit.Assert;

public class BasicModelSetup {

	public static void setup(Toolkit toolkit, Scope scope) {

		/* Map Anchor */

		StructureDefinition mapAnchorDef =
			toolkit.createNewStructureDefinition(scope, "mapAnchor").setSingleInstance(false);

		mapAnchorDef = mapAnchorDef.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope, "name", PropertyType.String).setPrimaryKey(true));
		mapAnchorDef = mapAnchorDef.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope, "x", PropertyType.Integer).setDefaultValue("0").setFinal(true));
		mapAnchorDef = mapAnchorDef.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope, "y", PropertyType.Integer).setDefaultValue("0").setFinal(true));
		mapAnchorDef = mapAnchorDef.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope, "z", PropertyType.Integer).setDefaultValue("0").setFinal(true));

		toolkit.writeStructureDefinition(scope, mapAnchorDef);

		StructureDefinition sd = toolkit.lookupStructureDefinitionByName(scope, "mapAnchor");
		Assert.assertNotNull(sd);
		StructureDefinitionRef mapAnchorRef = sd.getReference();

		/* Zone */

		StructureDefinition definition = toolkit.createNewStructureDefinition(scope, "zone").setSingleInstance(false);

		definition = definition.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope, "name", PropertyType.String).setPrimaryKey(true));
		definition = definition
			.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "mapAnchorId", PropertyType.StructureRef)
				.addReferenceType(mapAnchorRef));
		definition = definition
			.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "dimensionId", PropertyType.Integer));

		toolkit.writeStructureDefinition(scope, definition);
	}
}

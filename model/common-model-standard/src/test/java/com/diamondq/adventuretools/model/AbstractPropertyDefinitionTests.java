package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.Toolkit;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractPropertyDefinitionTests implements StandardTest {

	protected Toolkit	mToolkit;

	protected Scope		mScope;

	@Override
	public void setup(Toolkit pToolkit, Scope pScope) {
		mToolkit = pToolkit;
		mScope = pScope;
	}

	protected StructureDefinition checkAndCreate(String pStructureName) {

		/* Make sure it doesn't already exist */

		StructureDefinition def = mToolkit.lookupStructureDefinitionByName(mScope, pStructureName);
		Assert.assertNull(def);

		/* Create a new object */

		StructureDefinition newDef = mToolkit.createNewStructureDefinition(mScope, pStructureName);
		Assert.assertNotNull(newDef);

		return newDef;
	}

	@Test
	public void testValidName() {

		String name = "apdt-vn";

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		def = def.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope)
			.setName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-").setPrimaryKey(true)
			.setType(PropertyType.String));

		mToolkit.writeStructureDefinition(mScope, def);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidValidName() {

		String name = "apdt-ivn";

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		def = def.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName("abc/def")
			.setPrimaryKey(true).setType(PropertyType.String));

		mToolkit.writeStructureDefinition(mScope, def);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotSettingType() {

		String name = "apdt-nst";

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		def = def
			.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName("abc/def").setPrimaryKey(true));

		mToolkit.writeStructureDefinition(mScope, def);

	}
}

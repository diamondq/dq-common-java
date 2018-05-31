package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.Toolkit;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractPropertyDefinitionTests implements StandardTest {

	protected @Nullable Toolkit	mToolkit;

	protected @Nullable Scope	mScope;

	@Override
	public void setup(Toolkit pToolkit, Scope pScope) {
		mToolkit = pToolkit;
		mScope = pScope;
	}

	protected StructureDefinition checkAndCreate(String pStructureName) {

		Toolkit toolkit = mToolkit;
		Scope scope = mScope;
		Assert.assertNotNull(toolkit);
		Assert.assertNotNull(scope);

		/* Make sure it doesn't already exist */

		StructureDefinition def = toolkit.lookupStructureDefinitionByName(scope, pStructureName);
		Assert.assertNull(def);

		/* Create a new object */

		StructureDefinition newDef = toolkit.createNewStructureDefinition(scope, pStructureName);
		Assert.assertNotNull(newDef);

		return newDef;
	}

	@Test
	public void testValidName() {

		String name = "apdt-vn";

		Toolkit toolkit = mToolkit;
		Scope scope = mScope;
		Assert.assertNotNull(toolkit);
		Assert.assertNotNull(scope);

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		def =
			def.addPropertyDefinition(
				toolkit
					.createNewPropertyDefinition(scope,
						"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-", PropertyType.String)
					.setPrimaryKey(true));

		toolkit.writeStructureDefinition(scope, def);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidValidName() {

		String name = "apdt-ivn";

		Toolkit toolkit = mToolkit;
		Scope scope = mScope;
		Assert.assertNotNull(toolkit);
		Assert.assertNotNull(scope);

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		def = def.addPropertyDefinition(
			toolkit.createNewPropertyDefinition(scope, "abc/def", PropertyType.String).setPrimaryKey(true));

		toolkit.writeStructureDefinition(scope, def);
	}

}

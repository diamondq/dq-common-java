package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.TranslatableString;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractStructureDefinitionTests implements StandardTest {

	protected Toolkit	mToolkit;

	protected Scope		mScope;

	@Override
	public void setup(Toolkit pToolkit, Scope pScope) {
		mToolkit = pToolkit;
		mScope = pScope;
	}

	protected StructureDefinition checkAndCreate(String pName) {
		/* Make sure it doesn't already exist */

		StructureDefinition def = mToolkit.lookupStructureDefinitionByName(mScope, pName);
		Assert.assertNull(def);

		/* Create a new object */

		StructureDefinition newDef = mToolkit.createNewStructureDefinition(mScope, pName);
		Assert.assertNotNull(newDef);

		return newDef;
	}

	/**
	 * Make sure that a new StructureDefinition isn't automatically written upon creation
	 */
	@Test
	public void testUnwrittenNewStructureDefinition() {

		String name = "asdt_1";

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		/* Make sure it doesn't exist, since it hasn't been written */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNull(testDef);
	}

	/**
	 * Make sure that a written StructureDefinition can be read back in
	 */
	@Test
	public void testWrittenNewStructureDefinition() {

		String name = "asdt-2";

		StructureDefinition newDef = checkAndCreate(name);

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Make sure it doesn't exist, since it hasn't been written */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);
	}

	/**
	 * Test that the valid characters for a name are accepted
	 */
	@Test
	public void testValidName() {
		String validName = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-";
		StructureDefinition newDef = checkAndCreate(validName);
		mToolkit.writeStructureDefinition(mScope, newDef);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidName() {
		String validName = "def/abc";
		StructureDefinition newDef = checkAndCreate(validName);
		mToolkit.writeStructureDefinition(mScope, newDef);
	}

	/**
	 * Test that setting the label survives a write/read
	 */
	@Test
	public void testLabel() {

		String name = "asdt-ts";
		StructureDefinition newDef = checkAndCreate(name);

		/* Setup Label */

		TranslatableString ts = mToolkit.createNewTranslatableString(mScope, "dummy.key");
		Assert.assertNotNull(ts);
		Assert.assertEquals("Key does not match", "dummy.key", ts.getKey());

		newDef = newDef.setLabel(ts);

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Read */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);

		/* Test */

		TranslatableString testTS = testDef.getLabel();
		Assert.assertNotNull(testTS);
		Assert.assertEquals("Serialized key does not match", "dummy.key", testTS.getKey());
	}

	/**
	 * Test that setting the Single Instance surives a write/read
	 */
	@Test
	public void testSettingSingleInstance() {

		String name = "asdt-ssi";
		StructureDefinition newDef = checkAndCreate(name);

		/* Setup Single Instance */

		newDef = newDef.setSingleInstance(true);

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Read */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);

		/* Test */

		Assert.assertEquals("Serialized single instance does not match", true, testDef.isSingleInstance());
	}

	/**
	 * Test that the default value for Single Instance is false
	 */
	@Test
	public void testDefaultSingleInstance() {

		String name = "asdt-dsi";
		StructureDefinition newDef = checkAndCreate(name);

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Read */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);

		/* Test */

		Assert.assertEquals("Serialized single instance does not match", false, testDef.isSingleInstance());
	}

	/**
	 * Test that adding a PropertyDefinition survives a write/read
	 */
	@Test
	public void testAddingPropertyDefinition() {

		String name = "asdt-apd";
		String propName = name + "-name";
		StructureDefinition newDef = checkAndCreate(name);

		/* Setup PropertyDefinition */

		PropertyDefinition newPD = mToolkit.createNewPropertyDefinition(mScope);
		newPD = newPD.setName(propName).setType(PropertyType.String);

		newDef = newDef.addPropertyDefinition(newPD);

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Read */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);

		/* Test */

		Map<String, PropertyDefinition> testProperties = testDef.getPropertyDefinitions();
		Assert.assertNotNull(testProperties);
		PropertyDefinition testPD = testProperties.get(propName);
		Assert.assertNotNull(testPD);
		Assert.assertEquals("Serialized property does not match", propName, testPD.getName());

		/* Test All */

		Map<String, PropertyDefinition> testAllProperties = testDef.getAllProperties();
		Assert.assertNotNull(testAllProperties);
		PropertyDefinition testAllPD = testAllProperties.get(propName);
		Assert.assertNotNull(testAllPD);
		Assert.assertEquals("Serialized property name does not match", propName, testAllPD.getName());
	}

	/**
	 * Test that removing an existing PropertyDefinition survies a write/read
	 */
	@Test
	public void testRemovingPropertyDefinition() {

		String name = "asdt-rpd";
		String propName = name + "-name";
		StructureDefinition newDef = checkAndCreate(name);

		/* Setup PropertyDefinition */

		PropertyDefinition newPD = mToolkit.createNewPropertyDefinition(mScope);
		newPD = newPD.setName(propName).setType(PropertyType.String);

		newDef = newDef.addPropertyDefinition(newPD);

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Read */

		StructureDefinition testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);

		/* Test */

		Map<String, PropertyDefinition> testProperties = testDef.getPropertyDefinitions();
		Assert.assertNotNull(testProperties);
		PropertyDefinition testPD = testProperties.get(propName);
		Assert.assertNotNull(testPD);
		Assert.assertEquals("Serialized property does not match", propName, testPD.getName());

		/* Test All */

		Map<String, PropertyDefinition> testAllProperties = testDef.getAllProperties();
		Assert.assertNotNull(testAllProperties);
		PropertyDefinition testAllPD = testAllProperties.get(propName);
		Assert.assertNotNull(testAllPD);
		Assert.assertEquals("Serialized property name does not match", propName, testAllPD.getName());

		/* Remove */

		testDef = testDef.removePropertyDefinition(testPD);
		mToolkit.writeStructureDefinition(mScope, testDef);

		/* Re-Read */

		testDef = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(testDef);

		/* Test */

		testProperties = testDef.getPropertyDefinitions();
		Assert.assertNotNull(testProperties);
		testPD = testProperties.get(propName);
		Assert.assertNull(testPD);

		/* Test All */

		testAllProperties = testDef.getAllProperties();
		Assert.assertNotNull(testAllProperties);
		testAllPD = testAllProperties.get(propName);
		Assert.assertNull(testAllPD);

	}

	/**
	 * Test that adding a parent with a PropertyDefinition is visible in a child StructureDefinition.
	 */
	@Test
	public void testSingleParent() {

		String parentName = "asdt-sp-parent";
		String childName = "asdt-sp-child";
		String parentPropName = parentName + "-name";
		StructureDefinition parentDef = checkAndCreate(parentName);

		/* Setup PropertyDefinition on parent */

		PropertyDefinition newPD = mToolkit.createNewPropertyDefinition(mScope);
		newPD = newPD.setName(parentPropName).setType(PropertyType.String);

		parentDef = parentDef.addPropertyDefinition(newPD);

		StructureDefinitionRef parentDefRef = parentDef.getReference();

		/* Write */

		mToolkit.writeStructureDefinition(mScope, parentDef);

		/* Now build the 'empty' child */

		StructureDefinition childDef = checkAndCreate(childName);
		childDef = childDef.addParentDefinition(parentDefRef);

		mToolkit.writeStructureDefinition(mScope, childDef);

		/* Lookup the child */

		StructureDefinition testChildDef = mToolkit.lookupStructureDefinitionByName(mScope, childName);
		Assert.assertNotNull(testChildDef);

		/* Look for the property (which comes from the parent */

		PropertyDefinition childProp = testChildDef.lookupPropertyDefinitionByName(parentPropName);
		Assert.assertNotNull(childProp);
		Assert.assertEquals(parentPropName, childProp.getName());
	}

	@Test
	public void testSimpleReference() {

		String name = "asdt-sr";
		String propName = name + "-name";
		StructureDefinition newDef = checkAndCreate(name);

		/* Setup PropertyDefinition */

		PropertyDefinition newPD = mToolkit.createNewPropertyDefinition(mScope);
		newPD = newPD.setName(propName).setType(PropertyType.String);

		newDef = newDef.addPropertyDefinition(newPD);

		StructureDefinitionRef ref = newDef.getReference();

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		/* Test */

		StructureDefinition testDef = ref.resolve();
		Assert.assertNotNull(testDef);
		PropertyDefinition testPD = testDef.lookupPropertyDefinitionByName(propName);
		Assert.assertNotNull(testPD);
		Assert.assertEquals(propName, testPD.getName());
	}

}

package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.collect.Iterables;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractPropertyTests implements StandardTest {

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
	public void testSimpleValue() {

		String name = "apt-sv-1";

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(name);
		Assert.assertNotNull(def);

		def = def.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName("name").setPrimaryKey(true)
			.setType(PropertyType.String));
		def = def.addPropertyDefinition(
			mToolkit.createNewPropertyDefinition(mScope).setName("testValue").setType(PropertyType.String));

		mToolkit.writeStructureDefinition(mScope, def);

		/* Create a structure */

		def = mToolkit.lookupStructureDefinitionByName(mScope, name);
		Assert.assertNotNull(def);

		Structure structure = def.createNewStructure();
		Assert.assertNotNull(structure);

		Property<String> prop = structure.lookupPropertyByName("name");
		Assert.assertNotNull(prop);
		prop = prop.setValue("testStructure");
		Assert.assertNotNull(prop);
		structure = structure.updateProperty(prop);

		prop = structure.lookupPropertyByName("testValue");
		Assert.assertNotNull(prop);
		prop = prop.setValue("value1");
		Assert.assertNotNull(prop);

		structure = structure.updateProperty(prop);

		String structureRef = structure.getReference().getSerializedString();
		
		/* Write */

		mToolkit.writeStructure(mScope, structure);

		/* Read */

		Structure testStructure = mToolkit.createStructureRefFromSerialized(mScope, structureRef).resolve();
		Assert.assertNotNull(testStructure);

		Property<String> testProp = testStructure.lookupPropertyByName("testValue");
		Assert.assertNotNull(testProp);
		String testValue = testProp.getValue(testStructure);
		Assert.assertEquals("value1", testValue);
	}

	@Test
	public void testInheritedValue() {

		String parentDefName = "apt-iv-1";
		String childDefName = "apt-iv-2";

		/* Define a basic structure definition */

		StructureDefinition def = checkAndCreate(parentDefName);
		Assert.assertNotNull(def);

		def = def.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName("name").setPrimaryKey(true)
			.setType(PropertyType.String));
		def = def.addPropertyDefinition(
			mToolkit.createNewPropertyDefinition(mScope).setName("testValue").setType(PropertyType.String));

		mToolkit.writeStructureDefinition(mScope, def);

		/* Define a child structure */

		StructureDefinition childDef = checkAndCreate(childDefName);
		Assert.assertNotNull(childDef);
		childDef = childDef.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName("name")
			.setPrimaryKey(true).setType(PropertyType.String));
		childDef = childDef.addPropertyDefinition(
			mToolkit.createNewPropertyDefinition(mScope).setName("testValue").setType(PropertyType.String));
		childDef = childDef.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName("parent")
			.setType(PropertyType.StructureRef).addKeyword(CommonKeywordKeys.INHERIT_PARENT, CommonKeywordValues.TRUE));

		mToolkit.writeStructureDefinition(mScope, childDef);

		/* Create a structure */

		def = mToolkit.lookupStructureDefinitionByName(mScope, parentDefName);
		Assert.assertNotNull(def);

		Structure parentStructure = def.createNewStructure();
		Assert.assertNotNull(parentStructure);

		Property<String> prop = parentStructure.lookupPropertyByName("name");
		Assert.assertNotNull(prop);
		prop = prop.setValue("testStructure");
		Assert.assertNotNull(prop);
		parentStructure = parentStructure.updateProperty(prop);

		prop = parentStructure.lookupPropertyByName("testValue");
		Assert.assertNotNull(prop);
		prop = prop.setValue("value1");
		Assert.assertNotNull(prop);

		parentStructure = parentStructure.updateProperty(prop);
		StructureRef parentRef = parentStructure.getReference();
		
		/* Write */

		mToolkit.writeStructure(mScope, parentStructure);

		/* Create the child structure */

		parentStructure = parentRef.resolve();
		Assert.assertNotNull(parentStructure);

		childDef = mToolkit.lookupStructureDefinitionByName(mScope, childDefName);
		Assert.assertNotNull(childDef);

		Structure childStructure = childDef.createNewStructure();
		prop = childStructure.lookupPropertyByName("name");
		Assert.assertNotNull(prop);
		prop = prop.setValue("testChildStructure");
		childStructure = childStructure.updateProperty(prop);

		prop = childStructure.lookupPropertyByName("testValue");
		Assert.assertNotNull(prop);
		String value = prop.getValue(childStructure);
		Assert.assertNull(value);

		Collection<Property<StructureRef>> parentProps =
			childStructure.lookupPropertiesByKeyword(CommonKeywordKeys.INHERIT_PARENT, null, PropertyType.StructureRef);
		childStructure = childStructure
			.updateProperty(Iterables.getFirst(parentProps, null).setValue(parentStructure.getReference()));
		prop = childStructure.lookupPropertyByName("testValue");

		String childValue = prop.getValue(childStructure);
		Assert.assertEquals("value1", childValue);

		StructureRef childRef = childStructure.getReference();
		mToolkit.writeStructure(mScope, childStructure);

		/* Read */

		Structure testStructure = childRef.resolve();
		Assert.assertNotNull(testStructure);

		Property<String> testProp = testStructure.lookupPropertyByName("testValue");
		Assert.assertNotNull(testProp);
		String testValue = testProp.getValue(testStructure);
		Assert.assertEquals("value1", testValue);
	}

}

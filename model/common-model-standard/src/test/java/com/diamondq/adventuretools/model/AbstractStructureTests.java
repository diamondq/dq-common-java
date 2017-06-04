package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractStructureTests implements StandardTest {

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

	@Test
	public void testParentContainer() {

		String name = "asdt-pc";
		String propName = name + "-name";
		String parentName = name + "-parent";
		StructureDefinition newDef = checkAndCreate(name);

		/* Setup PropertyDefinition */

		newDef = newDef.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope).setName(propName)
			.setPrimaryKey(true).setType(PropertyType.String));

		newDef = newDef.addPropertyDefinition(
			mToolkit.createNewPropertyDefinition(mScope).setName(parentName).setType(PropertyType.PropertyRef)
				.addKeyword(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT));

		/* Write */

		mToolkit.writeStructureDefinition(mScope, newDef);

		StructureDefinition def = mToolkit.lookupStructureDefinitionByName(mScope, name);

		/* Setup instances */

		Structure parent = mToolkit.createNewStructure(mScope, def);
		parent = parent.updateProperty(parent.lookupPropertyByName(propName).setValue("INHERIT_PARENT"));
		PropertyRef<?> parentRef = mToolkit.createPropertyRef(mScope, null, parent);

		mToolkit.writeStructure(mScope, parent);

		Structure child = mToolkit.createNewStructure(mScope, def);
		child = child.updateProperty(child.lookupPropertyByName(propName).setValue("CHILD"));
		child = child.updateProperty(child.lookupPropertyByName(parentName).setValue(parentRef));

		String childRef = child.getReference().getSerializedString();
		mToolkit.writeStructure(mScope, child);

		/* Test */

		StructureRef childRefObj = mToolkit.createStructureRefFromSerialized(mScope, childRef);
		child = childRefObj.resolve();
		Assert.assertEquals("CHILD", child.lookupPropertyByName(propName).getValue(child));

		/* Verify the destruction of the parent destroys the child */

		parent = parentRef.resolveToStructure();
		mToolkit.deleteStructure(mScope, parent);

		childRefObj = mToolkit.createStructureRefFromSerialized(mScope, childRef);
		child = childRefObj.resolve();
		Assert.assertNull(child);
	}
}

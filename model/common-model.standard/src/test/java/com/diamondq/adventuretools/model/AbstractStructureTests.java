package com.diamondq.adventuretools.model;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractStructureTests implements StandardTest {

  protected @Nullable Toolkit mToolkit;

  protected @Nullable Scope   mScope;

  @Override
  public void setup(Toolkit pToolkit, Scope pScope) {
    mToolkit = pToolkit;
    mScope = pScope;
  }

  protected StructureDefinition checkAndCreate(String pName) {

    Toolkit toolkit = mToolkit;
    Scope scope = mScope;
    Assert.assertNotNull(toolkit);
    Assert.assertNotNull(scope);

    /* Make sure it doesn't already exist */

    StructureDefinition def = toolkit.lookupStructureDefinitionByName(scope, pName);
    Assert.assertNull(def);

    /* Create a new object */

    StructureDefinition newDef = toolkit.createNewStructureDefinition(scope, pName);
    Assert.assertNotNull(newDef);

    return newDef;
  }

  @Test
  public void testParentContainer() {

    String name = "asdt-pc";
    String propName = name + "-name";
    String parentName = name + "-parent";

    Toolkit toolkit = mToolkit;
    Scope scope = mScope;
    Assert.assertNotNull(toolkit);
    Assert.assertNotNull(scope);

    StructureDefinition newDef = checkAndCreate(name);

    /* Setup PropertyDefinition */

    newDef = newDef.addPropertyDefinition(
      toolkit.createNewPropertyDefinition(scope, propName, PropertyType.String).setPrimaryKey(true));

    newDef =
      newDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, parentName, PropertyType.PropertyRef)
        .addKeyword(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT));

    /* Write */

    toolkit.writeStructureDefinition(scope, newDef);

    StructureDefinition def = toolkit.lookupStructureDefinitionByName(scope, name);
    Assert.assertNotNull(def);

    /* Setup instances */

    Structure parent = toolkit.createNewStructure(scope, def);
    Property<@Nullable Object> parentProperty = parent.lookupPropertyByName(propName);
    Assert.assertNotNull(parentProperty);
    parent = parent.updateProperty(parentProperty.setValue("INHERIT_PARENT"));
    PropertyRef<?> parentRef = toolkit.createPropertyRef(scope, null, parent);

    toolkit.writeStructure(scope, parent);

    Structure child = toolkit.createNewStructure(scope, def);
    Property<@Nullable Object> childProperty = child.lookupPropertyByName(propName);
    Assert.assertNotNull(childProperty);
    child = child.updateProperty(childProperty.setValue("CHILD"));
    Property<@Nullable Object> childParentProperty = child.lookupPropertyByName(parentName);
    Assert.assertNotNull(childParentProperty);
    child = child.updateProperty(childParentProperty.setValue(parentRef));

    String childRef = child.getReference().getSerializedString();
    toolkit.writeStructure(scope, child);

    /* Test */

    StructureRef childRefObj = toolkit.createStructureRefFromSerialized(scope, childRef);
    child = childRefObj.resolve();
    Assert.assertNotNull(child);
    childProperty = child.lookupPropertyByName(propName);
    Assert.assertNotNull(childProperty);
    Assert.assertEquals("CHILD", childProperty.getValue(child));

    /* Verify the destruction of the parent destroys the child */

    parent = parentRef.resolveToStructure();
    Assert.assertNotNull(parent);
    toolkit.deleteStructure(scope, parent);

    childRefObj = toolkit.createStructureRefFromSerialized(scope, childRef);
    child = childRefObj.resolve();
    Assert.assertNull(child);
  }
}

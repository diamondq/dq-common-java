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
import org.jspecify.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public abstract class AbstractPropertyTests implements StandardTest {

  protected @Nullable Toolkit mToolkit;

  protected @Nullable Scope mScope;

  @Override
  public void setup(Toolkit pToolkit, Scope pScope) {
    mToolkit = pToolkit;
    mScope = pScope;
  }

  protected StructureDefinition checkAndCreate(String pStructureName) {

    Toolkit toolkit = mToolkit;
    Assert.assertNotNull(toolkit);

    Scope scope = mScope;
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
  public void testSimpleValue() {

    String name = "apt-sv-1";

    Toolkit toolkit = mToolkit;
    Scope scope = mScope;
    Assert.assertNotNull(toolkit);
    Assert.assertNotNull(scope);

    /* Define a basic structure definition */

    StructureDefinition def = checkAndCreate(name);
    Assert.assertNotNull(def);

    def = def.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "name", PropertyType.String)
      .setPrimaryKey(true));
    def = def.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "testValue", PropertyType.String));

    toolkit.writeStructureDefinition(scope, def);

    /* Create a structure */

    def = toolkit.lookupStructureDefinitionByName(scope, name);
    Assert.assertNotNull(def);

    Structure structure = def.createNewStructure();
    Assert.assertNotNull(structure);

    Property<@Nullable String> prop = structure.lookupPropertyByName("name");
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

    toolkit.writeStructure(scope, structure);

    /* Read */

    Structure testStructure = toolkit.createStructureRefFromSerialized(scope, structureRef).resolve();
    Assert.assertNotNull(testStructure);

    Property<@Nullable String> testProp = testStructure.lookupPropertyByName("testValue");
    Assert.assertNotNull(testProp);
    String testValue = testProp.getValue(testStructure);
    Assert.assertEquals("value1", testValue);
  }

  @Test
  public void testInheritedValue() {

    String parentDefName = "apt-iv-1";
    String childDefName = "apt-iv-2";

    Toolkit toolkit = mToolkit;
    Scope scope = mScope;
    Assert.assertNotNull(toolkit);
    Assert.assertNotNull(scope);

    /* Define a basic structure definition */

    StructureDefinition def = checkAndCreate(parentDefName);
    Assert.assertNotNull(def);

    def = def.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "name", PropertyType.String)
      .setPrimaryKey(true));
    def = def.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "testValue", PropertyType.String));

    toolkit.writeStructureDefinition(scope, def);

    /* Define a child structure */

    StructureDefinition childDef = checkAndCreate(childDefName);
    Assert.assertNotNull(childDef);
    childDef = childDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope, "name", PropertyType.String)
      .setPrimaryKey(true));
    childDef = childDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope,
      "testValue",
      PropertyType.String
    ));
    childDef = childDef.addPropertyDefinition(toolkit.createNewPropertyDefinition(scope,
      "parent",
      PropertyType.StructureRef
    ).addKeyword(CommonKeywordKeys.INHERIT_PARENT, CommonKeywordValues.TRUE));

    toolkit.writeStructureDefinition(scope, childDef);

    /* Create a structure */

    def = toolkit.lookupStructureDefinitionByName(scope, parentDefName);
    Assert.assertNotNull(def);

    Structure parentStructure = def.createNewStructure();
    Assert.assertNotNull(parentStructure);

    Property<@Nullable String> prop = parentStructure.lookupPropertyByName("name");
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

    toolkit.writeStructure(scope, parentStructure);

    /* Create the child structure */

    parentStructure = parentRef.resolve();
    Assert.assertNotNull(parentStructure);

    childDef = toolkit.lookupStructureDefinitionByName(scope, childDefName);
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

    Collection<Property<@Nullable StructureRef>> parentProps = childStructure.lookupPropertiesByKeyword(
      CommonKeywordKeys.INHERIT_PARENT,
      null,
      PropertyType.StructureRef
    );
    childStructure = childStructure.updateProperty(Iterables.get(parentProps, 0)
      .setValue(parentStructure.getReference()));
    prop = childStructure.lookupPropertyByName("testValue");
    Assert.assertNotNull(prop);

    String childValue = prop.getValue(childStructure);
    Assert.assertEquals("value1", childValue);

    StructureRef childRef = childStructure.getReference();
    toolkit.writeStructure(scope, childStructure);

    /* Read */

    Structure testStructure = childRef.resolve();
    Assert.assertNotNull(testStructure);

    Property<@Nullable String> testProp = testStructure.lookupPropertyByName("testValue");
    Assert.assertNotNull(testProp);
    String testValue = testProp.getValue(testStructure);
    Assert.assertEquals("value1", testValue);
  }

}

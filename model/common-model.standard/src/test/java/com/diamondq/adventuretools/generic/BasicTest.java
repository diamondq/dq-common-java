package com.diamondq.adventuretools.generic;

import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.ToolkitFactory;
import com.diamondq.common.model.persistence.MemoryPersistenceLayer;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class BasicTest {

  @Test
  public void test() {
    ToolkitFactory instance = ToolkitFactory.newInstance();
    GenericToolkit toolkit = (GenericToolkit) instance.newToolkit();
    Scope scope = toolkit.getOrCreateScope("Design");
    toolkit.setPersistenceLayer(scope, new MemoryPersistenceLayer());
    StructureDefinition definition = toolkit.createNewStructureDefinition(scope, "Test-Definition");
    toolkit.writeStructureDefinition(scope, definition);
    Collection<StructureDefinitionRef> allStructureDefinitionRefs = toolkit.getAllStructureDefinitionRefs(scope);
    Assert.assertTrue(allStructureDefinitionRefs.contains(definition.getReference()));
  }

}

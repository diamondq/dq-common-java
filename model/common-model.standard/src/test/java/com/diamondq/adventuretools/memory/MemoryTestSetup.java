package com.diamondq.adventuretools.memory;

import com.diamondq.adventuretools.model.BasicModelSetup;
import com.diamondq.adventuretools.model.StandardTest;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.ToolkitFactory;
import com.diamondq.common.model.persistence.MemoryPersistenceLayer;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.context.impl.ContextFactorySetup;

public interface MemoryTestSetup {

  default public void beforeSetup(StandardTest pStandardTest) {
    ContextFactory contextFactory = ContextFactorySetup.setup();
    ToolkitFactory instance = ToolkitFactory.newInstance();
    GenericToolkit toolkit = (GenericToolkit) instance.newToolkit();
    Scope scope = toolkit.getOrCreateScope("Design");
    toolkit.setPersistenceLayer(scope, new MemoryPersistenceLayer(contextFactory));

    BasicModelSetup.setup(toolkit, scope);

    pStandardTest.setup(toolkit, scope);
  }
}

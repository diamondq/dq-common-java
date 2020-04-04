package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.persistence.PropertiesFilePersistenceLayer;

import java.io.File;

public class OSGiPropertiesFilePersistenceLayer extends AbstractOSGiConstructor {

  public OSGiPropertiesFilePersistenceLayer() {
    super(ConstructorInfoBuilder.builder() //
      .constructorClass(PropertiesFilePersistenceLayer.class) //
      .register(PersistenceLayer.class) //
      .cArg().type(ContextFactory.class).injectContextFactory().required().build() //
      .cArg().type(File.class).prop(".structureDir").optional().build() //
      .cArg().type(Integer.TYPE).prop(".cacheStructuresSeconds").value(300).build() //
      .cArg().type(File.class).prop(".structureDefDir").optional().build() //
      .cArg().type(File.class).prop(".editorStructureDefDir").optional().build() //
    );
  }

}

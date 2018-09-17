package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.persistence.StorageKVPersistenceLayer;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.utils.context.ContextFactory;

public class OSGiStorageKVPersistenceLayer extends AbstractOSGiConstructor {

  public OSGiStorageKVPersistenceLayer() {
    super(ConstructorInfoBuilder.builder() //
      .constructorClass(StorageKVPersistenceLayer.class) //
      .register(PersistenceLayer.class) //
      .cArg().type(ContextFactory.class).injectContextFactory().required().build()//
      .cArg().type(IKVStore.class).propFilter(".kvstore").required().build() //
    );
  }

}

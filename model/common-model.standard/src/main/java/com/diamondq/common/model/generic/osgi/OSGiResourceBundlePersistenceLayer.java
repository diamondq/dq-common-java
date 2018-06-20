package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.persistence.ResourceBundlePersistenceLayer;

public class OSGiResourceBundlePersistenceLayer extends AbstractOSGiConstructor {

	public OSGiResourceBundlePersistenceLayer() {
		super(ConstructorInfoBuilder.builder() //
			.constructorClass(ResourceBundlePersistenceLayer.class) //
			.register(PersistenceLayer.class) //
			.cArg().type(String.class).prop(".resourceBaseName").required().build() //
		);
	}

}

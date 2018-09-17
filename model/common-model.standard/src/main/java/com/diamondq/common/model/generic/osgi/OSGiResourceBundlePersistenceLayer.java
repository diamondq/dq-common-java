package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.persistence.ResourceBundlePersistenceLayer;
import com.diamondq.common.utils.context.ContextFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSGiResourceBundlePersistenceLayer extends AbstractOSGiConstructor {
  private static final Logger sLogger = LoggerFactory.getLogger(OSGiResourceBundlePersistenceLayer.class);

  public OSGiResourceBundlePersistenceLayer() {
    super(ConstructorInfoBuilder.builder() //
      .constructorClass(OSGiResourceBundlePersistenceLayer.class) //
      .factoryMethod("create") //
      .register(PersistenceLayer.class) //
      .cArg().type(BundleContext.class).injectBundleContext().required().build() //
      .cArg().type(ContextFactory.class).injectContextFactory().required().build() //
      .cArg().type(String.class).prop(".resourceBaseName").required().build() //
    );
  }

  public PersistenceLayer create(BundleContext pContext, ContextFactory pContextFactory, String pResourceBaseName) {
    sLogger.trace("create({}, {}) from {}", pContext, pResourceBaseName, this);

    BundleWiring bundleWiring = pContext.getBundle().adapt(BundleWiring.class);
    if (bundleWiring == null)
      throw new IllegalStateException();
    ClassLoader classLoader = bundleWiring.getClassLoader();

    return new ResourceBundlePersistenceLayer(pContextFactory, pResourceBaseName, classLoader);
  }

}

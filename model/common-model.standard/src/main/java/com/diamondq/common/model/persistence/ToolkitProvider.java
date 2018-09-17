package com.diamondq.common.model.persistence;

import com.diamondq.common.config.Config;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.ToolkitFactory;
import com.diamondq.common.utils.context.ContextFactory;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;

@ApplicationScoped
public class ToolkitProvider {

  @Produces
  @ApplicationScoped
  public Toolkit createToolkit(Config pConfig, ContextFactory pContextFactory, BeanManager pManager) {
    ToolkitFactory factory = ToolkitFactory.newInstance();
    Toolkit toolkit = factory.newToolkit();
    GenericToolkit gt = (GenericToolkit) toolkit;

    @SuppressWarnings("unchecked")
    Collection<String> scopes = pConfig.bind("persistence.scopes", Collection.class);
    if (scopes != null) {
      for (String scopeName : scopes) {
        Scope scope = toolkit.getOrCreateScope(scopeName);

        /* Now attempt to get the list of structures */

        @SuppressWarnings("unchecked")
        List<PersistenceLayer> structureLayers = pConfig.bind("persistence.scope-" + scopeName + ".structures",
          List.class, Collections.singletonMap("scope", scope));
        if ((structureLayers == null) || (structureLayers.isEmpty() == true))
          throw new IllegalArgumentException(
            "The config key persistence.scope-" + scopeName + ".structures requires at least one definition");

        @SuppressWarnings("unchecked")
        List<PersistenceLayer> resourceLayers = pConfig.bind("persistence.scope-" + scopeName + ".resources",
          List.class, Collections.singletonMap("scope", scope));
        if ((resourceLayers == null) || (resourceLayers.isEmpty() == true))
          throw new IllegalArgumentException(
            "The config key persistence.scope-" + scopeName + ".resources requires at least one definition");

        gt.setPersistenceLayer(scope,
          new CombinedPersistenceLayer(pContextFactory, structureLayers,
            ImmutableList.<PersistenceLayer> builder().add(new MemoryPersistenceLayer(pContextFactory))
              .addAll(structureLayers).build(),
            Collections.singletonList(new MemoryPersistenceLayer(pContextFactory)), resourceLayers));
      }
    }
    return toolkit;
  }

}

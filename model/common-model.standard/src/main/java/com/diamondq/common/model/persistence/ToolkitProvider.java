package com.diamondq.common.model.persistence;

import com.diamondq.common.config.Config;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.ToolkitFactory;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.micronaut.context.annotation.Factory;

@ApplicationScoped
@Factory
public class ToolkitProvider {

  @Produces
  @ApplicationScoped
  public Toolkit createToolkit(Config pConfig, ContextFactory pContextFactory) {
    ToolkitFactory factory = ToolkitFactory.newInstance();
    Toolkit toolkit = factory.newToolkit();
    GenericToolkit gt = (GenericToolkit) toolkit;

    @SuppressWarnings("unchecked")
    Collection<String> scopes = pConfig.bind("persistence.scopes", Collection.class);
    if (scopes != null) {
      for (String scopeName : scopes) {
        Scope scope = toolkit.getOrCreateScope(scopeName);

        /* Now attempt to get the list of structures */

        Map<String, Object> context = new HashMap<>();
        context.put("scope", scope);
        context.put("contextFactory", pContextFactory);
        @SuppressWarnings("unchecked")
        List<PersistenceLayer> structureLayers =
          pConfig.bind("persistence.scope-" + scopeName + ".structures", List.class, context);
        if ((structureLayers == null) || (structureLayers.isEmpty() == true))
          throw new IllegalArgumentException(
            "The config key persistence.scope-" + scopeName + ".structures requires at least one definition");

        @SuppressWarnings("unchecked")
        List<PersistenceLayer> resourceLayers =
          pConfig.bind("persistence.scope-" + scopeName + ".resources", List.class, context);
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

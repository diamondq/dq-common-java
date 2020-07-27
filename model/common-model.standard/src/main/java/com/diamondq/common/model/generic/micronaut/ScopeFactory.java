package com.diamondq.common.model.generic.micronaut;

import com.diamondq.common.builders.IBuilder;
import com.diamondq.common.builders.IBuilderFactory;
import com.diamondq.common.builders.IBuilderWithMap;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.injection.InjectionContext;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.persistence.CombinedPersistenceLayer;
import com.diamondq.common.model.persistence.MemoryPersistenceLayer;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;

@Factory
public class ScopeFactory {

  @EachBean(ScopeConfiguration.class)
  public Scope createScope(Toolkit pToolkit, ScopeConfiguration pConfig, ContextFactory pContextFactory,
    InjectionContext pContext) {
    Scope scope = pToolkit.getOrCreateScope(pConfig.name);

    /* Now attempt to set up the layers for this scope */

    List<PersistenceLayer> structureLayers = new ArrayList<>();
    List<Map<String, Object>> list = pConfig.structures;
    if (list != null)
      for (Map<String, Object> confMap : list) {
        String type = (String) confMap.get("type");
        @SuppressWarnings("rawtypes")
        Optional<IBuilderFactory> factoryOpt = pContext.findBean(IBuilderFactory.class, type);
        if (factoryOpt.isPresent() == false)
          throw new IllegalStateException("There is no IBuilderFactory defined with the name " + type);
        @SuppressWarnings("unchecked")
        IBuilderFactory<PersistenceLayer> factory = factoryOpt.get();
        IBuilder<PersistenceLayer> builder = factory.create();
        PersistenceLayer pl;
        if (builder instanceof IBuilderWithMap) {
          @SuppressWarnings("unchecked")
          IBuilderWithMap<?, PersistenceLayer> mapBuilder = (IBuilderWithMap<?, PersistenceLayer>) builder;
          pl = mapBuilder.withMap(confMap, null).build();
        }
        else
          pl = builder.build();
        structureLayers.add(pl);
      }

    List<PersistenceLayer> resourceLayers = new ArrayList<>();
    list = pConfig.resources;
    if (list != null)
      for (Map<String, Object> confMap : list) {
        String type = (String) confMap.get("type");
        @SuppressWarnings("rawtypes")
        Optional<IBuilderFactory> factoryOpt = pContext.findBean(IBuilderFactory.class, type);
        if (factoryOpt.isPresent() == false)
          throw new IllegalStateException("There is no IBuilderFactory defined with the name " + type);
        @SuppressWarnings("unchecked")
        IBuilderFactory<PersistenceLayer> factory = factoryOpt.get();
        IBuilder<PersistenceLayer> builder = factory.create();
        PersistenceLayer pl;
        if (builder instanceof IBuilderWithMap) {
          @SuppressWarnings("unchecked")
          IBuilderWithMap<?, PersistenceLayer> mapBuilder = (IBuilderWithMap<?, PersistenceLayer>) builder;
          pl = mapBuilder.withMap(confMap, null).build();
        }
        else
          pl = builder.build();
        resourceLayers.add(pl);
      }
    if ((pToolkit instanceof GenericToolkit) == false)
      throw new IllegalArgumentException(
        "The provided Toolkit is not a GenericToolkit, but a " + pToolkit.getClass().getName());

    GenericToolkit gt = (GenericToolkit) pToolkit;
    gt.setPersistenceLayer(scope,
      new CombinedPersistenceLayer(pContextFactory, structureLayers,
        ImmutableList.<PersistenceLayer> builder().add(new MemoryPersistenceLayer(pContextFactory))
          .addAll(structureLayers).build(),
        Collections.singletonList(new MemoryPersistenceLayer(pContextFactory)), resourceLayers));

    return scope;
  }
}

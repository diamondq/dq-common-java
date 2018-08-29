package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.generic.UnknownScopeException;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.persistence.CombinedPersistenceLayer;
import com.diamondq.common.model.persistence.NewMemoryPersistenceLayer;
import com.diamondq.common.utils.misc.builders.IBuilder;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Octet;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedScope implements Scope {
  private static final Logger                                                    sLogger        =
    LoggerFactory.getLogger(WrappedScope.class);

  protected Scope                                                                mScope;

  protected Toolkit                                                              mToolkit;

  protected @Nullable String                                                     mName;

  protected final ConcurrentMap<PersistenceLayer, Map<String, Object>>           mLayers        =
    new ConcurrentHashMap<>();

  protected final ConcurrentMap<IBuilder<PersistenceLayer>, Map<String, Object>> mBuilderLayers =
    new ConcurrentHashMap<>();

  protected @Nullable Filter[]                                                   mFilters;

  protected volatile boolean                                                     mInitialized   = false;

  protected boolean[]                                                            mDefaultLayers;

  private static final @NonNull String[]                                         sFILTER_KEYS   =
    new @NonNull String[] {".structure_filter", ".structure_definition_filter", ".editor_structure_definition_filter",
        ".resource_filter"};

  @SuppressWarnings("null")
  public WrappedScope() {
    sLogger.trace("WrappedScope() from {}", this);
    mScope = null;
    mFilters = new Filter[sFILTER_KEYS.length];
    mDefaultLayers = new boolean[sFILTER_KEYS.length];
  }

  public Scope getScope() {
    return mScope;
  }

  public void setToolkit(Toolkit pToolkit) {
    sLogger.trace("setToolkit({}) from {}", pToolkit, this);
    mToolkit = pToolkit;
  }

  public void addPersistenceLayer(PersistenceLayer pLayer, Map<String, Object> pProps) {
    try {
      sLogger.trace("addPersistenceLayer({}, {}) from {}", pLayer, pProps, this);
      ImmutableMap<String, Object> props = ImmutableMap.copyOf(pProps);
      mLayers.put(pLayer, props);
      processLayers(false);
    }
    catch (RuntimeException ex) {
      sLogger.error("", ex);
      throw ex;
    }
  }

  public void removePersistenceLayer(PersistenceLayer pLayer) {
    try {
      sLogger.trace("removePersistenceLayer({}) from {}", pLayer, this);
      mLayers.remove(pLayer);
      processLayers(false);
    }
    catch (RuntimeException ex) {
      sLogger.error("", ex);
      throw ex;
    }
  }

  public void addBuilder(IBuilder<PersistenceLayer> pLayer, Map<String, Object> pProps) {
    try {
      sLogger.trace("addBuilder({}, {}) from {}", pLayer, pProps, this);
      ImmutableMap<String, Object> props = ImmutableMap.copyOf(pProps);
      mBuilderLayers.put(pLayer, props);
      processLayers(false);
    }
    catch (RuntimeException ex) {
      sLogger.error("", ex);
      throw ex;
    }
  }

  public void removeBuilder(IBuilder<PersistenceLayer> pLayer) {
    try {
      sLogger.trace("removeBuilder({}) from {}", pLayer, this);
      mBuilderLayers.remove(pLayer);
      processLayers(false);
    }
    catch (RuntimeException ex) {
      sLogger.error("", ex);
      throw ex;
    }
  }

  public void onActivate(ComponentContext pContext, Map<String, Object> pProps) {
    try {
      sLogger.trace("onActivate({}, {}) from {}", pContext, pProps, this);
      mName = PropertiesParsing.getNonNullString(pProps, "name", "default");
      mScope = mToolkit.getOrCreateScope(mName);

      /* Now see if any the layers match the criteria */

      for (int i = 0; i < sFILTER_KEYS.length; i++) {
        String filter = PropertiesParsing.getNullableString(pProps, sFILTER_KEYS[i]);
        if (filter != null) {
          try {
            mFilters[i] = pContext.getBundleContext().createFilter(filter);
          }
          catch (InvalidSyntaxException ex) {
            throw new RuntimeException(ex);
          }
        }
        else
          mFilters[i] = null;
      }

      mInitialized = true;
      processLayers(true);
    }
    catch (RuntimeException ex) {
      sLogger.error("", ex);
      throw ex;
    }
  }

  public @Nullable PersistenceLayer getPersistenceLayer() {
    if (mToolkit instanceof WrappedToolkit) {
      try {
        return ((WrappedToolkit) mToolkit).getPersistenceLayer(mScope);
      }
      catch (UnknownScopeException ex) {
        return null;
      }
    }
    else
      throw new UnsupportedOperationException();
  }

  /**
   * Adds the PersistenceLayer if the filter matches the properties
   * 
   * @param pLayer
   * @param pProps
   */
  private void processLayers(boolean pIsFirstProcess) {
    sLogger.trace("processLayers({}) from {}", pIsFirstProcess, this);

    if (mInitialized == false)
      return;

    synchronized (this) {

      /* Get the existing layers (if any) */

      PersistenceLayer existingLayer;
      Octet<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean> modificationState;
      if (mToolkit instanceof WrappedToolkit) {
        try {
          existingLayer = ((WrappedToolkit) mToolkit).getPersistenceLayer(mScope);
        }
        catch (UnknownScopeException ex) {
          existingLayer = null;
        }
        modificationState = ((WrappedToolkit) mToolkit).getModificationState();
      }
      else
        throw new UnsupportedOperationException();
      @SuppressWarnings("unchecked")
      List<PersistenceLayer>[] existingLayers = new List[sFILTER_KEYS.length];
      if (existingLayer == null) {
        for (int i = 0; i < sFILTER_KEYS.length; i++)
          existingLayers[i] = new ArrayList<>();
      }
      else {
        if ((existingLayer instanceof CombinedPersistenceLayer) == false)
          throw new UnsupportedOperationException();
        Quartet<List<PersistenceLayer>, List<PersistenceLayer>, List<PersistenceLayer>, List<PersistenceLayer>> existingLayerQuartet =
          ((CombinedPersistenceLayer) existingLayer).getPersistenceLayers();
        if (sFILTER_KEYS.length != 4)
          throw new UnsupportedOperationException();
        existingLayers[0] = existingLayerQuartet.getValue0();
        existingLayers[1] = existingLayerQuartet.getValue1();
        existingLayers[2] = existingLayerQuartet.getValue2();
        existingLayers[3] = existingLayerQuartet.getValue3();
      }

      @SuppressWarnings("unchecked")
      List<Pair<Integer, PersistenceLayer>>[] layers = new List[sFILTER_KEYS.length];
      for (int o = 0; o < sFILTER_KEYS.length; o++)
        layers[o] = Lists.newArrayList();

      for (Map.Entry<PersistenceLayer, Map<String, Object>> pair : mLayers.entrySet()) {
        for (int i = 0; i < sFILTER_KEYS.length; i++) {
          Filter filter = mFilters[i];
          if (filter == null)
            continue;

          Map<String, Object> props = pair.getValue();
          if (filter.matches(props) == true) {

            Object rankingObj = props.get(Constants.SERVICE_RANKING);
            int ranking;
            if (rankingObj == null)
              ranking = Integer.MAX_VALUE;
            else if (rankingObj instanceof Integer)
              ranking = ((Integer) rankingObj);
            else
              ranking = Integer.parseInt(rankingObj.toString());

            layers[i].add(Pair.with(ranking, pair.getKey()));
          }
        }
      }

      for (Map.Entry<IBuilder<PersistenceLayer>, Map<String, Object>> pair : mBuilderLayers.entrySet()) {
        for (int i = 0; i < sFILTER_KEYS.length; i++) {
          Filter filter = mFilters[i];
          if (filter == null)
            continue;

          Map<String, Object> props = pair.getValue();
          if (filter.matches(props) == true) {

            Object rankingObj = props.get(Constants.SERVICE_RANKING);
            int ranking;
            if (rankingObj == null)
              ranking = Integer.MAX_VALUE;
            else if (rankingObj instanceof Integer)
              ranking = ((Integer) rankingObj);
            else
              ranking = Integer.parseInt(rankingObj.toString());

            layers[i].add(Pair.with(ranking, pair.getKey().build()));
          }
        }
      }

      /* Add default values */

      for (int i = 0; i < sFILTER_KEYS.length; i++) {
        if (layers[i].isEmpty() == true) {
          if (pIsFirstProcess == true) {
            if (mFilters[i] != null)
              sLogger.info("Filter {} didn't resolve. Using a Memory PersistenceLayer for now", mFilters[i]);
            layers[i].add(Pair.with(Integer.MAX_VALUE, new NewMemoryPersistenceLayer()));
            mDefaultLayers[i] = true;
          }
          else {
            if (mDefaultLayers[i] == true) {
              for (PersistenceLayer pl : existingLayers[i])
                layers[i].add(Pair.with(Integer.MAX_VALUE, pl));
            }
            else {
              if (mFilters[i] != null)
                sLogger.info("Filter {} didn't resolve. Using a Memory PersistenceLayer for now", mFilters[i]);
              layers[i].add(Pair.with(Integer.MAX_VALUE, new NewMemoryPersistenceLayer()));
              mDefaultLayers[i] = true;
            }
          }
        }
        else
          mDefaultLayers[i] = false;
      }

      /* Now sort the lists */

      @SuppressWarnings("unchecked")
      ImmutableList.Builder<PersistenceLayer>[] sortedLayers = new ImmutableList.Builder[sFILTER_KEYS.length];
      for (int i = 0; i < sFILTER_KEYS.length; i++) {
        final int o = i;
        Collections.sort(layers[o], (a, b) -> {
          return b.getValue0() - a.getValue0();
        });
        sortedLayers[o] = ImmutableList.builder();
        layers[o].forEach((p) -> {
          sortedLayers[o].add(p.getValue1());
        });
      }

      @SuppressWarnings("unchecked")
      @NonNull
      List<PersistenceLayer>[] finalLayers = new @NonNull List[sFILTER_KEYS.length];
      for (int i = 0; i < sFILTER_KEYS.length; i++)
        finalLayers[i] = sortedLayers[i].build();

      /* Now check if there are any failures due to modifications */

      if (sFILTER_KEYS.length != 4)
        throw new UnsupportedOperationException();
      for (int i = 0; i < 4; i++) {
        boolean isDifferent = false;
        if (finalLayers[i].size() != existingLayers[i].size())
          isDifferent = true;
        else {
          for (int o = 0; o < finalLayers[i].size(); o++) {
            if (finalLayers[i].get(o) != existingLayers[i].get(o)) {
              isDifferent = true;
              break;
            }
          }
        }
        if (isDifferent == true) {
          switch (i) {
          case 0:
            if (modificationState.getValue0() == true)
              throw new UnsupportedOperationException(
                "The structure persistence layer has updated something before the scope was updated. Not yet supported.");
            if (modificationState.getValue4() == true)
              throw new UnsupportedOperationException(
                "The structure persistence layer has deleted something before the scope was updated. Not yet supported.");
            break;
          case 1:
            if (modificationState.getValue1() == true)
              throw new UnsupportedOperationException(
                "The structure definition persistence layer has updated something before the scope was updated. Not yet supported.");
            if (modificationState.getValue5() == true)
              throw new UnsupportedOperationException(
                "The structure definition persistence layer has deleted something before the scope was updated. Not yet supported.");
            break;
          case 2:
            if (modificationState.getValue2() == true)
              throw new UnsupportedOperationException(
                "The editor structure persistence layer has updated something before the scope was updated. Not yet supported.");
            if (modificationState.getValue6() == true)
              throw new UnsupportedOperationException(
                "The editor structure persistence layer has deleted something before the scope was updated. Not yet supported.");
            break;
          case 3:
            if (modificationState.getValue3() == true)
              throw new UnsupportedOperationException(
                "The resource persistence layer has updated something before the scope was updated. Not yet supported.");
            if (modificationState.getValue7() == true)
              throw new UnsupportedOperationException(
                "The resource persistence layer has deleted something before the scope was updated. Not yet supported.");
            break;
          }
        }
      }

      CombinedPersistenceLayer combinedPersistenceLayer =
        new CombinedPersistenceLayer(finalLayers[0], finalLayers[1], finalLayers[2], finalLayers[3]);
      if (mToolkit instanceof GenericToolkit)
        ((GenericToolkit) mToolkit).setPersistenceLayer(mScope, combinedPersistenceLayer);
      else if (mToolkit instanceof WrappedToolkit) {
        ((WrappedToolkit) mToolkit).setPersistenceLayer(mScope, combinedPersistenceLayer);
        ((WrappedToolkit) mToolkit).clearModificationState();
      }
      else
        return;
    }

  }

  public void onDeactivate() {
    try {
      sLogger.trace("onDeactivate() from {}", this);
      String name = mName;
      if (name != null) {
        mName = null;
        mToolkit.removeScope(name);
      }
    }
    catch (RuntimeException ex) {
      sLogger.error("", ex);
      throw ex;
    }
  }

  /**
   * @see com.diamondq.common.model.interfaces.Scope#getName()
   */
  @Override
  public String getName() {
    return mScope.getName();
  }

  /**
   * @see com.diamondq.common.model.interfaces.Scope#getToolkit()
   */
  @Override
  public Toolkit getToolkit() {
    return mScope.getToolkit();
  }
}

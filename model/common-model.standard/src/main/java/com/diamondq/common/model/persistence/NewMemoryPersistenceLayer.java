package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NewMemoryPersistenceLayer extends AbstractDocumentPersistenceLayer<Map<String, Object>, String> {

  /**
   * The builder (generally used for the Config system)
   */
  public static class MemoryPersistenceLayerBuilder {

    /**
     * Builds the layer
     *
     * @return the layer
     */
    public NewMemoryPersistenceLayer build() {
      return new NewMemoryPersistenceLayer();
    }
  }

  private static class DataWrapper {
    public final Map<String, Object> data;

    public DataWrapper(Map<String, Object> pData) {
      data = pData;
    }

  }

  protected final ConcurrentMap<String, Object> mDataCache;

  /**
   * Default constructor
   */
  public NewMemoryPersistenceLayer() {
    super(true, true, -1, false, true, -1, false, false, -1, true, true, -1);
    mDataCache = Maps.newConcurrentMap();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#constructOptimisticObj(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected @Nullable String constructOptimisticObj(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    @Nullable Structure pStructure) {
    return constructOptimisticStringObj(pToolkit, pScope, pDefName, pKey, pStructure);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#setStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType, java.lang.Object)
   */
  @Override
  protected <@NonNull R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey, PropertyType pType, R pValue) {
    pConfig.put(pKey, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#getStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  protected <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey, PropertyType pType) {
    @SuppressWarnings("unchecked")
    R result = (R) pConfig.get(pKey);
    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#hasStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String)
   */
  @Override
  protected boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey) {
    return pConfig.containsKey(pKey);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#removeStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  protected boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey, PropertyType pType) {
    if (pConfig.remove(pKey) != null)
      return true;
    return false;
  }

  private @Nullable ConcurrentMap<String, Object> resolveToParent(ConcurrentMap<String, Object> pTop,
    @NonNull String[] pParts, boolean pCreateIfMissing) {
    ConcurrentMap<String, Object> map = pTop;
    if (pParts.length > 1)
      for (int i = 0; i < (pParts.length - 1); i++) {
        Object result = map.get(pParts[i]);
        if (result == null) {
          if (pCreateIfMissing == false) {
            map = null;
            break;
          }
          ConcurrentMap<String, Object> newMap = Maps.newConcurrentMap();
          if ((result = map.putIfAbsent(pParts[i], newMap)) == null)
            result = newMap;
        }
        else if ((result instanceof ConcurrentMap) == false)
          throw new IllegalArgumentException("Parent key writing to real object");
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, Object> castedMap = (ConcurrentMap<String, Object>) result;
        map = castedMap;
      }

    return map;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#loadStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, boolean)
   */
  @Override
  protected @Nullable Map<String, Object> loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName,
    String pKey, boolean pCreateIfMissing) {
    @NonNull
    String[] parts = pKey.split("/");
    ConcurrentMap<String, Object> map = resolveToParent(mDataCache, parts, false);
    Map<String, Object> data;
    if (map == null)
      data = null;
    else {
      DataWrapper wrapper = (DataWrapper) map.get(parts[parts.length - 1] + ".map");
      if (wrapper == null)
        data = null;
      else
        data = wrapper.data;
    }

    if ((data == null) && (pCreateIfMissing == true))
      data = Maps.newHashMap();
    return data;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#saveStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, java.lang.Object, boolean,
   *      java.lang.Object)
   */
  @Override
  protected boolean saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Map<String, Object> pConfig, boolean pMustMatchOptimisticObj, @Nullable String pOptimisticObj) {
    @NonNull
    String[] parts = pKey.split("/");
    ConcurrentMap<String, Object> map = resolveToParent(mDataCache, parts, true);
    if (map == null)
      throw new IllegalStateException("The map shouldn't be null");
    map.put(parts[parts.length - 1] + ".map", new DataWrapper(pConfig));

    // TODO: Support the optimistic code
    if (pMustMatchOptimisticObj == true)
      throw new UnsupportedOperationException();
    return true;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure) {
    @NonNull
    String[] parts = pKey.split("/");
    ConcurrentMap<String, Object> map = resolveToParent(mDataCache, parts, false);
    if (map != null)
      map.remove(parts[parts.length - 1] + ".map");
    // TODO: Support optimistic checks
    return true;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#isStructureConfigChanged(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object)
   */
  @Override
  protected boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig) {
    return false;
  }

  @Override
  protected void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope,
    @Nullable Map<String, Object> pConfig, StructureDefinition pStructureDefinition, String pStructureDefName,
    @Nullable String pKey, @Nullable PropertyDefinition pPropDef, Builder<StructureRef> pStructureRefListBuilder) {
    List<String> partList = pKey == null ? Lists.newArrayList() : Lists.newArrayList(pKey.split("/"));
    if (pPropDef != null)
      partList.add(pPropDef.getName());
    @SuppressWarnings("null")
    @NonNull
    String[] parts = partList.toArray(new String[0]);
    ConcurrentMap<String, Object> parent = resolveToParent(mDataCache, parts, false);
    if (parent == null)
      return;
    @SuppressWarnings("unchecked")
    ConcurrentMap<String, Object> child =
      (parts.length == 0 ? parent : (ConcurrentMap<String, Object>) parent.get(parts[parts.length - 1]));
    if (child == null)
      return;
    String[] listTypeDirs =
      Maps.filterValues(child, (v) -> ((v instanceof DataWrapper) == false)).keySet().toArray(new String[0]);

    /* Setup some variables */

    StringBuilder refBuilder = new StringBuilder();
    if (pKey != null)
      refBuilder.append(pKey).append('/');
    if (pPropDef != null)
      refBuilder.append(pPropDef.getName()).append('/');
    int preTypeOffset = refBuilder.length();
    for (String typeName : listTypeDirs) {

      /* If the PropertyDefinition has type restrictions, then make sure that this directory/type is valid */

      if (pPropDef != null) {
        Collection<StructureDefinitionRef> referenceTypes = pPropDef.getReferenceTypes();
        if (referenceTypes.isEmpty() == false) {
          boolean match = false;
          for (StructureDefinitionRef sdr : referenceTypes) {
            StructureDefinition sd = sdr.resolve();
            if (sd == null)
              continue;
            String testName = sd.getName();
            if (typeName.equals(testName)) {
              match = true;
              break;
            }
          }
          if (match == false)
            continue;
        }
      }
      else if (pKey == null) {
        /*
         * Special case where there is no parent key. In this case, the StructureDefinition is the restriction
         */

        if (typeName.equals(pStructureDefName) == false)
          continue;
      }

      refBuilder.setLength(preTypeOffset);
      refBuilder.append(typeName).append('/');
      int preNameOffset = refBuilder.length();

      @SuppressWarnings("unchecked")
      ConcurrentMap<String, Object> listTypeDir = (ConcurrentMap<String, Object>) child.get(typeName);
      for (String name : Maps.filterValues(listTypeDir, (v) -> v instanceof DataWrapper).keySet()) {
        name = name.substring(0, name.length() - 4);
        refBuilder.setLength(preNameOffset);
        refBuilder.append(name);
        pStructureRefListBuilder
          .add(pScope.getToolkit().createStructureRefFromSerialized(pScope, refBuilder.toString()));
      }
    }
    return;
  }

  public ConcurrentMap<String, Object> getDataCache() {
    return mDataCache;
  }

  public @Nullable Cache<String, Structure> getStructureCache() {
    return mStructureCache;
  }

  public @Nullable Cache<String, StructureDefinition> getStructureDefinitionCache() {
    return mStructureDefinitionCache;
  }

  public @Nullable Cache<String, List<EditorStructureDefinition>> getEditorStructureDefinitionCache() {
    return mEditorStructureDefinitionCacheByRef;
  }

  public @Nullable Cache<String, String> getResourceCache() {
    return mResourceCache;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#persistContainerProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
   *      com.diamondq.common.model.interfaces.Property)
   */
  @Override
  protected boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure, Property<?> pProp) {
    return false;
  }

  public static MemoryPersistenceLayerBuilder builder() {
    return new MemoryPersistenceLayerBuilder();
  }

}

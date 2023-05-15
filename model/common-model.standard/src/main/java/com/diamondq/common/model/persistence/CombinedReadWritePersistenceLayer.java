package com.diamondq.common.model.persistence;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.model.generic.AbstractPersistenceLayer;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Tombstone;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This persistence layer will always write to the 'write' layer, but will first read from the 'write' layer and if it's
 * not present, then it will read from the 'read' layer. This allows you to define a 'read-only' starting point, but
 * persist updates/changes to a different layer.
 */
public class CombinedReadWritePersistenceLayer extends AbstractPersistenceLayer {

  /**
   * The builder (generally used for the Config system)
   */
  public static class CombinedReadWritePersistenceLayerBuilder {

    private @Nullable ContextFactory mContextFactory;

    private @Nullable Scope mScope;

    private @Nullable PersistenceLayer mStructureReadLayer;

    private @Nullable PersistenceLayer mStructureWriteLayer;

    private @Nullable PersistenceLayer mStructureDefinitionReadLayer;

    private @Nullable PersistenceLayer mStructureDefinitionWriteLayer;

    private @Nullable PersistenceLayer mEditorStructureDefinitionReadLayer;

    private @Nullable PersistenceLayer mEditorStructureDefinitionWriteLayer;

    private @Nullable PersistenceLayer mResourceReadLayer;

    private @Nullable PersistenceLayer mResourceWriteLayer;

    public CombinedReadWritePersistenceLayerBuilder contextFactory(ContextFactory pContextFactory) {
      mContextFactory = pContextFactory;
      return this;
    }

    /**
     * Sets the scope
     *
     * @param pScope the scope
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder scope(Scope pScope) {
      mScope = pScope;
      return this;
    }

    /**
     * Sets the structure read layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder structureReadLayer(PersistenceLayer pValue) {
      mStructureReadLayer = pValue;
      return this;
    }

    /**
     * Sets the structure write layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder structureWriteLayer(PersistenceLayer pValue) {
      mStructureWriteLayer = pValue;
      return this;
    }

    /**
     * Sets the structure definition read layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder structureDefinitionReadLayer(PersistenceLayer pValue) {
      mStructureDefinitionReadLayer = pValue;
      return this;
    }

    /**
     * Sets the structure definition write layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder structureDefinitionWriteLayer(PersistenceLayer pValue) {
      mStructureDefinitionWriteLayer = pValue;
      return this;
    }

    /**
     * Sets the editor structure definition read layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder editorStructureDefinitionReadLayer(PersistenceLayer pValue) {
      mEditorStructureDefinitionReadLayer = pValue;
      return this;
    }

    /**
     * Sets the editor structure definition write layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder editorStructureDefinitionWriteLayer(PersistenceLayer pValue) {
      mEditorStructureDefinitionWriteLayer = pValue;
      return this;
    }

    /**
     * Sets the resource read layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder resourceReadLayer(PersistenceLayer pValue) {
      mResourceReadLayer = pValue;
      return this;
    }

    /**
     * Sets the resource write layer
     *
     * @param pValue the layer
     * @return the builder
     */
    public CombinedReadWritePersistenceLayerBuilder resourceWriteLayer(PersistenceLayer pValue) {
      mResourceWriteLayer = pValue;
      return this;
    }

    /**
     * Builds the layer
     *
     * @return the layer
     */
    public CombinedReadWritePersistenceLayer build() {
      ContextFactory contextFactory = mContextFactory;
      if (contextFactory == null) throw new IllegalArgumentException("The mandatory field contextFactory was not set");
      Scope scope = mScope;
      if (scope == null) throw new IllegalArgumentException("The mandatory field scope was not set");
      PersistenceLayer structureWriteLayer = mStructureWriteLayer;
      if (structureWriteLayer == null) structureWriteLayer = new NewMemoryPersistenceLayer(contextFactory);
      PersistenceLayer structureDefinitionWriteLayer = mStructureDefinitionWriteLayer;
      if (structureDefinitionWriteLayer == null)
        structureDefinitionWriteLayer = new NewMemoryPersistenceLayer(contextFactory);
      PersistenceLayer editorStructureDefinitionWriteLayer = mEditorStructureDefinitionWriteLayer;
      if (editorStructureDefinitionWriteLayer == null)
        editorStructureDefinitionWriteLayer = new NewMemoryPersistenceLayer(contextFactory);
      PersistenceLayer resourceWriteLayer = mResourceWriteLayer;
      if (resourceWriteLayer == null) resourceWriteLayer = new NewMemoryPersistenceLayer(contextFactory);
      return new CombinedReadWritePersistenceLayer(contextFactory,
        mStructureReadLayer,
        structureWriteLayer,
        mStructureDefinitionReadLayer,
        structureDefinitionWriteLayer,
        mEditorStructureDefinitionReadLayer,
        editorStructureDefinitionWriteLayer,
        mResourceReadLayer,
        resourceWriteLayer
      );
    }
  }

  private final @Nullable PersistenceLayer mStructureReadLayer;

  private final PersistenceLayer mStructureWriteLayer;

  private final @Nullable PersistenceLayer mStructureDefinitionReadLayer;

  private final PersistenceLayer mStructureDefinitionWriteLayer;

  @SuppressWarnings("unused")
  private final @Nullable PersistenceLayer mEditorStructureDefinitionReadLayer;

  private final PersistenceLayer mEditorStructureDefinitionWriteLayer;

  private final @Nullable PersistenceLayer mResourceReadLayer;

  private final PersistenceLayer mResourceWriteLayer;

  public CombinedReadWritePersistenceLayer(ContextFactory pContextFactory,
    @Nullable PersistenceLayer pStructureReadLayer, PersistenceLayer pStructureWriteLayer,
    @Nullable PersistenceLayer pStructureDefinitionReadLayer, PersistenceLayer pStructureDefinitionWriteLayer,
    @Nullable PersistenceLayer pEditorStructureDefinitionReadLayer,
    PersistenceLayer pEditorStructureDefinitionWriteLayer, @Nullable PersistenceLayer pResourceReadLayer,
    PersistenceLayer pResourceWriteLayer) {
    super(pContextFactory);
    ContextFactory.staticReportTrace(CombinedReadWritePersistenceLayer.class,
      this,
      pStructureReadLayer,
      pStructureWriteLayer,
      pStructureDefinitionReadLayer,
      pStructureDefinitionWriteLayer,
      pEditorStructureDefinitionReadLayer,
      pEditorStructureDefinitionWriteLayer,
      pResourceReadLayer,
      pResourceWriteLayer
    );
    mStructureReadLayer = pStructureReadLayer;
    mStructureWriteLayer = pStructureWriteLayer;
    mStructureDefinitionReadLayer = pStructureDefinitionReadLayer;
    mStructureDefinitionWriteLayer = pStructureDefinitionWriteLayer;
    mEditorStructureDefinitionReadLayer = pEditorStructureDefinitionReadLayer;
    mEditorStructureDefinitionWriteLayer = pEditorStructureDefinitionWriteLayer;
    mResourceReadLayer = pResourceReadLayer;
    mResourceWriteLayer = pResourceWriteLayer;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public StructureDefinition writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    StructureDefinition result = mStructureDefinitionWriteLayer.writeStructureDefinition(pToolkit, pScope, pValue);
    enableStructureDefinition(pToolkit, pScope, pValue);
    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#enableStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void enableStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    PersistenceLayer readLayer = mStructureDefinitionReadLayer;
    if (readLayer != null) readLayer.enableStructureDefinition(pToolkit, pScope, pValue);
    mStructureWriteLayer.enableStructureDefinition(pToolkit, pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    PersistenceLayer readLayer = mStructureDefinitionReadLayer;
    if (readLayer == null) mStructureDefinitionWriteLayer.deleteStructureDefinition(pToolkit, pScope, pValue);
    else {
      StructureDefinition fromRead = readLayer.lookupStructureDefinitionByName(pToolkit, pScope, pValue.getName());
      if (fromRead != null) {

        /* We need to write a tombstone */

        StructureDefinition tombstone = mStructureDefinitionWriteLayer.createNewTombstoneStructureDefinition(pToolkit,
          pScope,
          pValue.getName()
        );
        mStructureDefinitionWriteLayer.writeStructureDefinition(pToolkit, pScope, tombstone);
      } else mStructureDefinitionWriteLayer.deleteStructureDefinition(pToolkit, pScope, pValue);
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope) {
    PersistenceLayer readLayer = mStructureDefinitionReadLayer;
    if (readLayer == null) return mStructureDefinitionWriteLayer.getAllStructureDefinitionRefs(pToolkit, pScope);
    Map<String, StructureDefinitionRef> results = Maps.newHashMap();
    readLayer.getAllStructureDefinitionRefs(pToolkit, pScope).forEach((sd) -> {
      results.put(sd.getSerializedString(), sd);
    });
    mStructureDefinitionWriteLayer.getAllStructureDefinitionRefs(pToolkit, pScope).forEach((sd) -> {
      if (sd instanceof Tombstone) results.remove(sd.getSerializedString());
      else results.put(sd.getSerializedString(), sd);
    });
    return ImmutableSet.copyOf(results.values());
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public @Nullable StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName) {
    PersistenceLayer readLayer = mStructureDefinitionReadLayer;
    if (readLayer == null)
      return mStructureDefinitionWriteLayer.lookupStructureDefinitionByName(pToolkit, pScope, pName);
    StructureDefinition result = mStructureDefinitionWriteLayer.lookupStructureDefinitionByName(pToolkit,
      pScope,
      pName
    );
    if (result == null) result = readLayer.lookupStructureDefinitionByName(pToolkit, pScope, pName);
    if ((result != null) && (result instanceof Tombstone)) result = null;
    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.Integer)
   */
  @Override
  public @Nullable StructureDefinition lookupStructureDefinitionByNameAndRevision(Toolkit pToolkit, Scope pScope,
    String pName, @Nullable Integer pRevision) {
    PersistenceLayer readLayer = mStructureDefinitionReadLayer;
    if (readLayer == null) return mStructureDefinitionWriteLayer.lookupStructureDefinitionByNameAndRevision(pToolkit,
      pScope,
      pName,
      pRevision
    );
    StructureDefinition result = mStructureDefinitionWriteLayer.lookupStructureDefinitionByNameAndRevision(pToolkit,
      pScope,
      pName,
      pRevision
    );
    if (result == null)
      result = readLayer.lookupStructureDefinitionByNameAndRevision(pToolkit, pScope, pName, pRevision);
    if ((result != null) && (result instanceof Tombstone)) result = null;
    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupLatestStructureDefinitionRevision(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public @Nullable Integer lookupLatestStructureDefinitionRevision(Toolkit pToolkit, Scope pScope, String pDefName) {
    PersistenceLayer readLayer = mStructureDefinitionReadLayer;
    if (readLayer == null)
      return mStructureDefinitionWriteLayer.lookupLatestStructureDefinitionRevision(pToolkit, pScope, pDefName);
    Integer result = mStructureDefinitionWriteLayer.lookupLatestStructureDefinitionRevision(pToolkit, pScope, pDefName);
    if (result == null) result = readLayer.lookupLatestStructureDefinitionRevision(pToolkit, pScope, pDefName);
    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
    mStructureWriteLayer.writeStructure(pToolkit, pScope, pStructure);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
   *   com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public boolean writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure,
    @Nullable Structure pOldStructure) {
    Structure retrievedStructure = lookupStructureBySerializedRef(pToolkit,
      pScope,
      pStructure.getReference().getSerializedString()
    );
    if (pOldStructure == null) {
      if (retrievedStructure == null)
        return mStructureWriteLayer.writeStructure(pToolkit, pScope, pStructure, pOldStructure);
      return false;
    } else {
      if (retrievedStructure == null) return false;
      if (pOldStructure.equals(retrievedStructure) == false) return false;
      return mStructureWriteLayer.writeStructure(pToolkit, pScope, pStructure, pOldStructure);
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public @Nullable Structure lookupStructureBySerializedRef(Toolkit pGenericToolkit, Scope pScope,
    String pSerializedRef) {
    PersistenceLayer readLayer = mStructureReadLayer;
    if (readLayer == null)
      return mStructureWriteLayer.lookupStructureBySerializedRef(pGenericToolkit, pScope, pSerializedRef);
    Structure result = mStructureWriteLayer.lookupStructureBySerializedRef(pGenericToolkit, pScope, pSerializedRef);
    if ((result != null) && (result instanceof Tombstone)) result = null;
    if (result == null) result = readLayer.lookupStructureBySerializedRef(pGenericToolkit, pScope, pSerializedRef);
    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public boolean deleteStructure(Toolkit pToolkit, Scope pScope, Structure pValue) {
    PersistenceLayer readLayer = mStructureReadLayer;
    if (readLayer == null) return mStructureWriteLayer.deleteStructure(pToolkit, pScope, pValue);

    /* Does the structure exist in the read layer */

    Structure old = readLayer.lookupStructureBySerializedRef(pToolkit,
      pScope,
      pValue.getReference().getSerializedString()
    );
    if (old != null) {
      Structure tombstone = mStructureWriteLayer.createNewTombstoneStructure(pToolkit, pScope, pValue);
      mStructureWriteLayer.writeStructure(pToolkit, pScope, tombstone);
      return true;
    } else return mStructureWriteLayer.deleteStructure(pToolkit, pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  public void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pEditorStructureDefinition) {
    mEditorStructureDefinitionWriteLayer.writeEditorStructureDefinition(pToolkit, pScope, pEditorStructureDefinition);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  public void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupEditorStructureDefinitionByRef(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Toolkit pToolkit, Scope pScope,
    StructureDefinitionRef pRef) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef,
   *   java.lang.String, com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope, StructureDefinitionRef pRef,
    @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
    PersistenceLayer readLayer = mStructureReadLayer;
    if (readLayer == null)
      return mStructureWriteLayer.getAllStructuresByDefinition(pToolkit, pScope, pRef, pParentKey, pParentPropertyDef);
    Map<String, Structure> results = Maps.newHashMap();
    mStructureWriteLayer.getAllStructuresByDefinition(pToolkit, pScope, pRef, pParentKey, pParentPropertyDef)
      .forEach((s) -> {
        results.put(s.getReference().getSerializedString(), s);
      });
    readLayer.getAllStructuresByDefinition(pToolkit, pScope, pRef, pParentKey, pParentPropertyDef).forEach((s) -> {
      if (s instanceof Tombstone) results.remove(s.getReference().getSerializedString());
      else results.put(s.getReference().getSerializedString(), s);
    });
    return ImmutableList.copyOf(results.values());
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
    return mResourceWriteLayer.isResourceStringWritingSupported(pToolkit, pScope);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
   */
  @Override
  public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue) {
    mResourceWriteLayer.writeResourceString(pToolkit, pScope, pLocale, pKey, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  public void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
    PersistenceLayer readLayer = mResourceReadLayer;
    if (readLayer == null) {
      mResourceWriteLayer.deleteResourceString(pToolkit, pScope, pLocale, pKey);
      return;
    }
    String readValue = readLayer.lookupResourceString(pToolkit, pScope, pLocale, pKey);
    if (readValue != null) {
      String tombstone = mResourceWriteLayer.createNewTombstoneResourceString(pToolkit, pScope);
      mResourceWriteLayer.writeResourceString(pToolkit, pScope, pLocale, pKey, tombstone);
    } else mResourceWriteLayer.deleteResourceString(pToolkit, pScope, pLocale, pKey);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
    PersistenceLayer readLayer = mResourceReadLayer;
    if (readLayer == null) return mResourceWriteLayer.getResourceStringLocales(pToolkit, pScope);
    ImmutableSet.Builder<Locale> builder = ImmutableSet.builder();
    builder.addAll(readLayer.getResourceStringLocales(pToolkit, pScope));
    builder.addAll(mResourceWriteLayer.getResourceStringLocales(pToolkit, pScope));
    return builder.build();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale)
   */
  @Override
  public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
    PersistenceLayer readLayer = mResourceReadLayer;
    if (readLayer == null) return mResourceWriteLayer.getResourceStringsByLocale(pToolkit, pScope, pLocale);
    Map<String, String> results = readLayer.getResourceStringsByLocale(pToolkit, pScope, pLocale);
    mResourceWriteLayer.getResourceStringsByLocale(pToolkit, pScope, pLocale).forEach((k, v) -> {
      if (mResourceWriteLayer.isTombstoneResourceString(pToolkit, pScope, v) == true) results.remove(k);
      else results.put(k, v);
    });
    return ImmutableMap.copyOf(results);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractPersistenceLayer#lookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  public @Nullable String lookupResourceString(Toolkit pToolkit, Scope pScope, @Nullable Locale pLocale, String pKey) {
    PersistenceLayer readLayer = mResourceReadLayer;
    if (readLayer == null) return mResourceWriteLayer.lookupResourceString(pToolkit, pScope, pLocale, pKey);
    String value = mResourceWriteLayer.lookupResourceString(pToolkit, pScope, pLocale, pKey);
    if (value != null) {
      if (mResourceWriteLayer.isTombstoneResourceString(pToolkit, pScope, value) == true) return null;
    } else value = readLayer.lookupResourceString(pToolkit, pScope, pLocale, pKey);
    return value;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractPersistenceLayer#internalLookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected @Nullable String internalLookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
    throw new UnsupportedOperationException();
  }

  public static CombinedReadWritePersistenceLayerBuilder builder() {
    return new CombinedReadWritePersistenceLayerBuilder();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#inferStructureDefinitions(com.diamondq.common.model.generic.GenericToolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean inferStructureDefinitions(GenericToolkit pGenericToolkit, Scope pScope) {
    boolean inferred = false;
    PersistenceLayer layer = mStructureReadLayer;
    if (layer != null) if (layer.inferStructureDefinitions(pGenericToolkit, pScope) == true) inferred = true;
    if (mStructureWriteLayer.inferStructureDefinitions(pGenericToolkit, pScope) == true) inferred = true;
    return inferred;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#clearStructures(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void clearStructures(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDef) {
    mStructureWriteLayer.clearStructures(pToolkit, pScope, pStructureDef);
  }
}

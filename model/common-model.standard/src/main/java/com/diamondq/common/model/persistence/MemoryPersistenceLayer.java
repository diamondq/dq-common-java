package com.diamondq.common.model.persistence;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.model.generic.AbstractCachingPersistenceLayer;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A Persistence Layer that stores all the information in memory
 */
public class MemoryPersistenceLayer extends AbstractCachingPersistenceLayer {

  /**
   * The builder (generally used for the Config system)
   */
  public static class MemoryPersistenceLayerBuilder {

    private @Nullable ContextFactory mContextFactory;

    public MemoryPersistenceLayerBuilder contextFactory(ContextFactory pContextFactory) {
      mContextFactory = pContextFactory;
      return this;
    }

    /**
     * Builds the layer
     *
     * @return the layer
     */
    public MemoryPersistenceLayer build() {
      ContextFactory contextFactory = mContextFactory;
      if (contextFactory == null) throw new IllegalArgumentException("The contextFactory is not set");
      return new MemoryPersistenceLayer(contextFactory);
    }
  }

  /**
   * Default constructor
   *
   * @param pContextFactory the context factory
   */
  public MemoryPersistenceLayer(ContextFactory pContextFactory) {
    super(pContextFactory, true, -1, true, -1, true, -1, true, -1);
    ContextFactory.staticReportTrace(MemoryPersistenceLayer.class, this);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef,
   *   java.lang.String, com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope, StructureDefinitionRef pRef,
    @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
    Cache<String, Structure> structureCache = mStructureCache;
    if (structureCache == null)
      throw new IllegalStateException("The structureCache is mandatory for the MemoryPersistenceLayer");
    if ((pParentKey != null) || (pParentPropertyDef != null))
      throw new UnsupportedOperationException("MemoryPersistenceLayer doesn't current support searching all by parent");
    if (pRef.isWildcardReference() == true) return Collections2.filter(structureCache.asMap().values(),
      (s) -> (s != null) && s.getDefinition().getWildcardReference().equals(pRef)
    );
    else return Collections2.filter(structureCache.asMap().values(),
      (s) -> (s != null) && s.getDefinition().getReference().equals(pRef)
    );
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
    return true;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
    Cache<String, String> resourceCache = mResourceCache;
    if (resourceCache == null)
      throw new IllegalStateException("The resourceCache is mandatory for the MemoryPersistenceLayer");
    Builder<Locale> builder = ImmutableSet.<Locale>builder();
    for (String k : resourceCache.asMap().keySet()) {
      int offset = k.indexOf(':');
      builder.add(Locale.forLanguageTag(k.substring(0, offset)));
    }
    // mmansell: The following code causes the 4.7.0 Eclipse compiler to freak out. Rewritten as the above
    // return ImmutableSet.<Locale> builder().addAll(
    // Collections2.<@NotNull String, @NotNull Locale> transform(resourceCache.asMap().keySet(), (String k) -> {
    // int offset = k.indexOf(':');
    // return Locale.forLanguageTag(k.substring(0, offset));
    // })).build();
    return builder.build();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale)
   */
  @Override
  public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
    String prefix = pLocale.toLanguageTag() + ":";
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    Cache<String, String> resourceCache = mResourceCache;
    if (resourceCache == null)
      throw new IllegalStateException("The resourceCache is mandatory for the MemoryPersistenceLayer");
    for (Map.Entry<String, String> pair : resourceCache.asMap().entrySet()) {
      String key = pair.getKey();
      if (key.startsWith(prefix) == true) builder.put(key.substring(prefix.length()), pair.getValue());
    }
    return builder.build();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.Structure, boolean, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure, boolean pMustMatchOldStructure, @Nullable Structure pOldStructure) {

    if (pMustMatchOldStructure == true) {
      Cache<String, Structure> structureCache = mStructureCache;
      if (structureCache == null)
        throw new IllegalStateException("The structureCache is mandatory for the MemoryPersistenceLayer");

      /* Check to see if the structure matches */

      Structure oldStructure = structureCache.getIfPresent(pKey);
      if (oldStructure == null) {
        if (pOldStructure != null) return false;
      } else {
        if (oldStructure.equals(pOldStructure) == false) return false;
      }
    }

    return true;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure) {

    Cache<String, Structure> structureCache = mStructureCache;
    if (structureCache == null)
      throw new IllegalStateException("The structureCache is mandatory for the MemoryPersistenceLayer");

    /* Check to see if the structure matches */

    Structure oldStructure = structureCache.getIfPresent(pKey);
    if (oldStructure == null) return false;
    if (oldStructure.equals(pStructure) == false) return false;

    /* Handle the recursive deleting of all children */

    Set<String> set = ImmutableSet.copyOf(Sets.filter(structureCache.asMap().keySet(),
      (k) -> (k != null) && k.startsWith(pKey)
    ));
    set.forEach((k) -> structureCache.invalidate(k));

    return true;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
   */
  @Override
  protected @Nullable Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName,
    String pKey) {
    return null;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected StructureDefinition internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pValue) {
    return pValue;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, int)
   */
  @Override
  protected @Nullable StructureDefinition internalLookupStructureDefinitionByNameAndRevision(Toolkit pToolkit,
    Scope pScope, String pName, int pRevision) {
    return null;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
   */
  @Override
  protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
    Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache) {
    return Collections.emptyList();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  protected void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue) {
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupEditorStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  protected @Nullable List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
    Scope pScope, StructureDefinitionRef pRef) {
    return null;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  protected void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue) {
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected @Nullable String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
    String pKey) {
    return null;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
   */
  @Override
  protected void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
    String pValue) {
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
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

  public static MemoryPersistenceLayerBuilder builder() {
    return new MemoryPersistenceLayerBuilder();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#inferStructureDefinitions(com.diamondq.common.model.generic.GenericToolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean inferStructureDefinitions(GenericToolkit pGenericToolkit, Scope pScope) {
    return false;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#clearStructures(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void clearStructures(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDef) {
    Cache<String, Structure> structureCache = mStructureCache;
    if (structureCache != null) structureCache.invalidateAll();
  }
}

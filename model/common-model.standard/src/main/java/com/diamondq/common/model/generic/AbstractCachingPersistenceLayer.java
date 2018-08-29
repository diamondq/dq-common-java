package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.base.Predicates;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This abstract class is used to provide caching capability to a given PersistenceLayer
 */
public abstract class AbstractCachingPersistenceLayer extends AbstractPersistenceLayer {

  protected final @Nullable Cache<String, Structure>                       mStructureCache;

  protected final @Nullable Cache<String, StructureDefinition>             mStructureDefinitionCache;

  protected final @Nullable Cache<String, Integer>                         mStructureDefinitionRevisionCache;

  protected final @Nullable Cache<String, List<EditorStructureDefinition>> mEditorStructureDefinitionCacheByRef;

  protected final @Nullable Cache<String, String>                          mResourceCache;

  /**
   * The main constructor
   *
   * @param pCacheStructures true if the class should cache structures
   * @param pCacheStructuresSeconds the number of seconds to cache (-1 is forever)
   * @param pCacheStructureDefinitions true if the class should cache structure definitions
   * @param pCacheStructureDefinitionsSeconds the number of seconds to cache (-1 is forever)
   * @param pCacheEditorStructureDefinitions true if the class should cache editor structure definitions
   * @param pCacheEditorStructureDefinitionsSeconds the number of seconds to cache (-1 is forever)
   * @param pCacheResources true if the class should cache resources
   * @param pCacheResourcesSeconds the number of seconds to cache (-1 is forever)
   */
  public AbstractCachingPersistenceLayer(boolean pCacheStructures, int pCacheStructuresSeconds,
    boolean pCacheStructureDefinitions, int pCacheStructureDefinitionsSeconds, boolean pCacheEditorStructureDefinitions,
    int pCacheEditorStructureDefinitionsSeconds, boolean pCacheResources, int pCacheResourcesSeconds) {
    if (pCacheStructures == true) {
      CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
      if (pCacheStructuresSeconds > 0)
        builder = builder.expireAfterWrite(pCacheStructuresSeconds, TimeUnit.SECONDS);
      mStructureCache = builder.build();
    }
    else
      mStructureCache = null;
    if (pCacheStructureDefinitions == true) {
      CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
      if (pCacheStructureDefinitionsSeconds > 0)
        builder = builder.expireAfterWrite(pCacheStructureDefinitionsSeconds, TimeUnit.SECONDS);
      mStructureDefinitionCache = builder.build();

      builder = CacheBuilder.newBuilder();
      if (pCacheStructureDefinitionsSeconds > 0)
        builder = builder.expireAfterWrite(pCacheStructureDefinitionsSeconds, TimeUnit.SECONDS);
      mStructureDefinitionRevisionCache = builder.build();
    }
    else {
      mStructureDefinitionCache = null;
      mStructureDefinitionRevisionCache = null;
    }
    if (pCacheEditorStructureDefinitions == true) {
      CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
      if (pCacheEditorStructureDefinitionsSeconds > 0)
        builder = builder.expireAfterWrite(pCacheEditorStructureDefinitionsSeconds, TimeUnit.SECONDS);
      mEditorStructureDefinitionCacheByRef = builder.build();
    }
    else
      mEditorStructureDefinitionCacheByRef = null;
    if (pCacheResources == true) {
      CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
      if (pCacheResourcesSeconds > 0)
        builder = builder.expireAfterWrite(pCacheResourcesSeconds, TimeUnit.SECONDS);
      mResourceCache = builder.build();
    }
    else
      mResourceCache = null;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
    String key = pToolkit.createStructureRefStr(pScope, pStructure);
    if (mStructureCache != null)
      mStructureCache.put(key, pStructure);

    /* Now write the data to disk */

    int lastOffset = key.lastIndexOf('/');
    if (lastOffset == -1)
      throw new IllegalArgumentException("The Structure reference is not the right format: " + key);
    int nextLastOffset = key.lastIndexOf('/', lastOffset - 1);
    String typeName;
    if (lastOffset == -1)
      typeName = key.substring(0, lastOffset);
    else
      typeName = key.substring(nextLastOffset + 1, lastOffset);

    internalWriteStructure(pToolkit, pScope, typeName, key, pStructure, false, null);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public boolean writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure,
    @Nullable Structure pOldStructure) {
    String key = pToolkit.createStructureRefStr(pScope, pStructure);

    /* Now write the data to disk */

    int lastOffset = key.lastIndexOf('/');
    if (lastOffset == -1)
      throw new IllegalArgumentException("The Structure reference is not the right format: " + key);
    int nextLastOffset = key.lastIndexOf('/', lastOffset - 1);
    String typeName;
    if (lastOffset == -1)
      typeName = key.substring(0, lastOffset);
    else
      typeName = key.substring(nextLastOffset + 1, lastOffset);

    if (internalWriteStructure(pToolkit, pScope, typeName, key, pStructure, true, pOldStructure) == false)
      return false;

    if (mStructureCache != null)
      mStructureCache.put(key, pStructure);

    return true;
  }

  protected abstract boolean internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure, boolean pMustMatchOldStructure, @Nullable Structure pOldStructure);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public boolean deleteStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
    String key = pToolkit.createStructureRefStr(pScope, pStructure);

    /* Now write the data to disk */

    int lastOffset = key.lastIndexOf('/');
    if (lastOffset == -1)
      throw new IllegalArgumentException("The Structure reference is not the right format: " + key);
    int nextLastOffset = key.lastIndexOf('/', lastOffset - 1);
    String typeName;
    if (lastOffset == -1)
      typeName = key.substring(0, lastOffset);
    else
      typeName = key.substring(nextLastOffset + 1, lastOffset);

    /* Now write the data to disk */

    if (internalDeleteStructure(pToolkit, pScope, typeName, key, pStructure) == false)
      return false;

    if (mStructureCache != null)
      mStructureCache.invalidate(key);

    return true;
  }

  protected abstract boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure);

  protected void invalidateStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
    String key = pToolkit.createStructureRefStr(pScope, pStructure);
    if (mStructureCache != null)
      mStructureCache.invalidate(key);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Nullable
  @Override
  public Structure lookupStructureBySerializedRef(Toolkit pToolkit, Scope pScope, String pSerializedRef) {
    Cache<String, Structure> structureCache = mStructureCache;
    Structure result = (structureCache == null ? null : structureCache.getIfPresent(pSerializedRef));
    if (result != null)
      return result;

    int lastOffset = pSerializedRef.lastIndexOf('/');
    if (lastOffset == -1)
      throw new IllegalArgumentException("The Structure reference is not the right format: " + pSerializedRef);
    int nextLastOffset = pSerializedRef.lastIndexOf('/', lastOffset - 1);
    String typeName;
    if (lastOffset == -1)
      typeName = pSerializedRef.substring(0, lastOffset);
    else
      typeName = pSerializedRef.substring(nextLastOffset + 1, lastOffset);

    result = internalLookupStructureByName(pToolkit, pScope, typeName, pSerializedRef);

    if ((result != null) && (mStructureCache != null))
      mStructureCache.put(pSerializedRef, result);

    return result;
  }

  protected abstract @Nullable Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName,
    String pKey);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    Cache<String, StructureDefinition> structureDefinitionCache = mStructureDefinitionCache;
    if (structureDefinitionCache != null) {
      int revision = pValue.getRevision();
      String name = pValue.getName();
      String key = new StringBuffer(name).append('-').append(revision).toString();
      structureDefinitionCache.put(key, pValue);
      Cache<String, Integer> structureDefinitionRevisionCache = mStructureDefinitionRevisionCache;
      if (structureDefinitionRevisionCache != null) {
        Integer oldRevision = structureDefinitionRevisionCache.getIfPresent(name);
        if ((oldRevision == null) || (oldRevision < revision))
          structureDefinitionRevisionCache.put(name, revision);
      }
    }

    internalWriteStructureDefinition(pToolkit, pScope, pValue);
  }

  protected abstract void internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public @Nullable StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName) {
    Cache<String, Integer> structureDefinitionRevisionCache = mStructureDefinitionRevisionCache;
    Integer revision =
      (structureDefinitionRevisionCache == null ? null : structureDefinitionRevisionCache.getIfPresent(pName));
    if (revision != null)
      return lookupStructureDefinitionByNameAndRevision(pToolkit, pScope, pName, revision);

    StructureDefinition result = internalLookupStructureDefinitionByNameAndRevision(pToolkit, pScope, pName, null);
    if ((result != null) && (structureDefinitionRevisionCache != null))
      structureDefinitionRevisionCache.put(pName, result.getRevision());

    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.Integer)
   */
  @Override
  public @Nullable StructureDefinition lookupStructureDefinitionByNameAndRevision(Toolkit pToolkit, Scope pScope,
    String pName, @Nullable Integer pRevision) {
    Cache<String, StructureDefinition> structureDefinitionCache = mStructureDefinitionCache;
    if (pRevision == null)
      pRevision = lookupLatestStructureDefinitionRevision(pToolkit, pScope, pName);
    if (pRevision != null) {
      String cacheKey = new StringBuilder(pName).append('-').append(pRevision).toString();
      StructureDefinition result =
        (structureDefinitionCache == null ? null : structureDefinitionCache.getIfPresent(cacheKey));
      if (result != null)
        return result;
    }

    StructureDefinition result = internalLookupStructureDefinitionByNameAndRevision(pToolkit, pScope, pName, pRevision);
    if ((result != null) && (structureDefinitionCache != null)) {
      String cacheKey = new StringBuilder(pName).append('-').append(result.getRevision()).toString();
      structureDefinitionCache.put(cacheKey, result);
    }

    return result;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupLatestStructureDefinitionRevision(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public @Nullable Integer lookupLatestStructureDefinitionRevision(Toolkit pToolkit, Scope pScope, String pDefName) {
    Cache<String, Integer> structureDefinitionRevisionCache = mStructureDefinitionRevisionCache;
    Integer revision =
      (structureDefinitionRevisionCache == null ? null : structureDefinitionRevisionCache.getIfPresent(pDefName));
    if (revision != null)
      return revision;

    StructureDefinition result = internalLookupStructureDefinitionByNameAndRevision(pToolkit, pScope, pDefName, null);
    if (result == null)
      return null;

    if (structureDefinitionRevisionCache != null)
      structureDefinitionRevisionCache.put(pDefName, result.getRevision());

    return result.getRevision();
  }

  protected abstract @Nullable StructureDefinition internalLookupStructureDefinitionByNameAndRevision(Toolkit pToolkit,
    Scope pScope, String pName, @Nullable Integer pRevision);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    Cache<String, StructureDefinition> structureDefinitionCache = mStructureDefinitionCache;
    if (structureDefinitionCache != null) {
      String key = new StringBuilder(pValue.getName()).append('-').append(pValue.getRevision()).toString();
      structureDefinitionCache.invalidate(key);
    }

    internalDeleteStructureDefinition(pToolkit, pScope, pValue);
  }

  protected abstract void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope) {

    Cache<String, StructureDefinition> structureDefinitionCache = mStructureDefinitionCache;

    if (structureDefinitionCache != null) {
      Collection<StructureDefinitionRef> missing =
        internalGetAllMissingStructureDefinitionRefs(pToolkit, pScope, mStructureDefinitionCache);
      for (StructureDefinitionRef ref : missing) {
        StructureDefinition sd = ref.resolve();
        if (sd != null)
          structureDefinitionCache.put(sd.getName(), sd);
      }

      return Collections2.transform(structureDefinitionCache.asMap().values(),
        (sd) -> sd == null ? null : sd.getReference());
    }
    else
      return internalGetAllMissingStructureDefinitionRefs(pToolkit, pScope, null);
  }

  protected abstract Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
    Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  public void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
    Cache<String, List<EditorStructureDefinition>> editorStructureDefinitionCacheByRef =
      mEditorStructureDefinitionCacheByRef;
    if (editorStructureDefinitionCacheByRef != null) {
      String key = pValue.getStructureDefinitionRef().getSerializedString();
      List<EditorStructureDefinition> list = editorStructureDefinitionCacheByRef.getIfPresent(key);
      if (list == null)
        list = ImmutableList.of();
      ImmutableList<EditorStructureDefinition> updatedList = ImmutableList.<EditorStructureDefinition> builder()
        .addAll(Collections2.filter(list, Predicates.not((a) -> (a != null) && a.getName().equals(pValue.getName()))))
        .add(pValue).build();
      editorStructureDefinitionCacheByRef.put(key, updatedList);
    }
    internalWriteEditorStructureDefinition(pToolkit, pScope, pValue);
  }

  protected abstract void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupEditorStructureDefinitionByRef(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Toolkit pToolkit, Scope pScope,
    StructureDefinitionRef pRef) {
    String key = pRef.getSerializedString();
    Cache<String, List<EditorStructureDefinition>> editorStructureDefinitionCacheByRef =
      mEditorStructureDefinitionCacheByRef;
    @Nullable
    List<EditorStructureDefinition> result =
      (editorStructureDefinitionCacheByRef != null ? editorStructureDefinitionCacheByRef.getIfPresent(key) : null);
    if (result != null)
      return result;

    result = internalLookupEditorStructureDefinitionByName(pToolkit, pScope, pRef);
    if ((result != null) && (editorStructureDefinitionCacheByRef != null))
      editorStructureDefinitionCacheByRef.put(key, result);

    if (result == null)
      result = Collections.emptyList();
    return result;
  }

  protected abstract @Nullable List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(
    Toolkit pToolkit, Scope pScope, StructureDefinitionRef pRef);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  public void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
    Cache<String, List<EditorStructureDefinition>> editorStructureDefinitionCacheByRef =
      mEditorStructureDefinitionCacheByRef;
    if (editorStructureDefinitionCacheByRef != null) {
      String key = pValue.getStructureDefinitionRef().getSerializedString();
      List<EditorStructureDefinition> list = editorStructureDefinitionCacheByRef.getIfPresent(key);
      if (list != null) {
        ImmutableList<EditorStructureDefinition> updatedList = ImmutableList.<EditorStructureDefinition> builder()
          .addAll(Collections2.filter(list, Predicates.not((a) -> (a != null) && a.getName().equals(pValue.getName()))))
          .build();
        if (updatedList.isEmpty() == true)
          editorStructureDefinitionCacheByRef.invalidate(key);
        else
          editorStructureDefinitionCacheByRef.put(key, updatedList);
      }
    }
    internalDeleteEditorStructureDefinition(pToolkit, pScope, pValue);
  }

  protected abstract void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue);

  /**
   * @see com.diamondq.common.model.generic.AbstractPersistenceLayer#internalLookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected @Nullable String internalLookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
    Cache<String, String> resourceCache = mResourceCache;
    if (resourceCache != null) {
      StringBuilder sb = new StringBuilder(pLocale.toString());
      sb.append(':').append(pKey);
      String key = sb.toString();
      String result = resourceCache.getIfPresent(key);
      if (result != null)
        return result;

      result = internal2LookupResourceString(pToolkit, pScope, pLocale, pKey);
      if (result != null)
        resourceCache.put(key, result);

      return result;
    }
    return internal2LookupResourceString(pToolkit, pScope, pLocale, pKey);
  }

  protected abstract @Nullable String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
    String pKey);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
   */
  @Override
  public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue) {
    if (isResourceStringWritingSupported(pToolkit, pScope) == false)
      throw new UnsupportedOperationException();
    Cache<String, String> resourceCache = mResourceCache;
    if (resourceCache != null) {
      StringBuilder sb = new StringBuilder(pLocale.toLanguageTag());
      sb.append(':').append(pKey);
      String key = sb.toString();
      resourceCache.put(key, pValue);
    }

    internalWriteResourceString(pToolkit, pScope, pLocale, pKey, pValue);
  }

  protected abstract void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
    String pValue);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#deleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  public void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
    if (isResourceStringWritingSupported(pToolkit, pScope) == false)
      throw new UnsupportedOperationException();
    Cache<String, String> resourceCache = mResourceCache;
    if (resourceCache != null) {
      StringBuilder sb = new StringBuilder(pLocale.toString());
      sb.append(':').append(pKey);
      String key = sb.toString();
      resourceCache.invalidate(key);
    }

    internalDeleteResourceString(pToolkit, pScope, pLocale, pKey);
  }

  protected abstract void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey);
}

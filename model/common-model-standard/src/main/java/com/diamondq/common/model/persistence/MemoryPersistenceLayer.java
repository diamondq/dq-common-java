package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractCachingPersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Persistence Layer that stores all the information in memory
 */
public class MemoryPersistenceLayer extends AbstractCachingPersistenceLayer {

	/**
	 * Default constructor
	 * 
	 * @param pScope the scope
	 */
	public MemoryPersistenceLayer(Scope pScope) {
		super(pScope, true, true, true, true);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		Cache<String, Structure> structureCache = mStructureCache;
		if (structureCache == null)
			throw new IllegalStateException("The structureCache is mandatory for the MemoryPersistenceLayer");
		return Collections2.filter(structureCache.asMap().values(),
			(s) -> s.getDefinition().getReference().equals(pRef));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
		return true;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
		Cache<String, String> resourceCache = mResourceCache;
		if (resourceCache == null)
			throw new IllegalStateException("The resourceCache is mandatory for the MemoryPersistenceLayer");
		return ImmutableSet.<Locale> builder().addAll(
			Collections2.<@NonNull String, @NonNull Locale> transform(resourceCache.asMap().keySet(), (String k) -> {
				int offset = k.indexOf(':');
				return Locale.forLanguageTag(k.substring(0, offset));
			})).build();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
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
			if ((key != null) && (key.startsWith(prefix) == true))
				builder.put(key.substring(prefix.length()), pair.getValue());
		}
		return builder.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure) {
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pKey, Structure pStructure) {

		/* Handle the recursive deleting */
		Cache<String, Structure> structureCache = mStructureCache;
		if (structureCache == null)
			throw new IllegalStateException("The structureCache is mandatory for the MemoryPersistenceLayer");
		Set<String> set = ImmutableSet.copyOf(Sets.filter(structureCache.asMap().keySet(), (k) -> k.startsWith(pKey)));
		set.forEach((k) -> structureCache.invalidate(k));

	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
	 */
	@Override
	protected @Nullable Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName,
		String pKey) {
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	protected @Nullable StructureDefinition internalLookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope,
		String pName) {
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
	 */
	@Override
	protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
		Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache) {
		return Collections.emptyList();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	protected void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pValue) {
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupEditorStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	protected @Nullable List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
		Scope pScope, StructureDefinitionRef pRef) {
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	protected void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pValue) {
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected @Nullable String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
		String pKey) {
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	protected void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
		String pValue) {
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
	}

}

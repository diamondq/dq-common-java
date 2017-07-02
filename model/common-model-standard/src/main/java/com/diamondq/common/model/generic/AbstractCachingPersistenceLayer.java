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
import java.util.List;
import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This abstract class is used to provide caching capability to a given PersistenceLayer
 */
public abstract class AbstractCachingPersistenceLayer extends AbstractPersistenceLayer {

	@Nullable
	protected final Cache<String, Structure>						mStructureCache;

	@Nullable
	protected final Cache<String, StructureDefinition>				mStructureDefinitionCache;

	@Nullable
	protected final Cache<String, List<EditorStructureDefinition>>	mEditorStructureDefinitionCacheByRef;

	@Nullable
	protected final Cache<String, String>							mResourceCache;

	/**
	 * The main constructor
	 * 
	 * @param pScope the scope
	 * @param pCacheStructures true if the class should cache structures
	 * @param pCacheStructureDefinitions true if the class should cache structure definitions
	 * @param pCacheEditorStructureDefinitions true if the class should cache editor structure definitions
	 * @param pCacheResources true if the class should cache resources
	 */
	public AbstractCachingPersistenceLayer(Scope pScope, boolean pCacheStructures, boolean pCacheStructureDefinitions,
		boolean pCacheEditorStructureDefinitions, boolean pCacheResources) {
		super(pScope);
		mStructureCache = (pCacheStructures == true ? CacheBuilder.newBuilder().build() : null);
		mStructureDefinitionCache = (pCacheStructureDefinitions == true ? CacheBuilder.newBuilder().build() : null);
		mEditorStructureDefinitionCacheByRef =
			(pCacheEditorStructureDefinitions == true ? CacheBuilder.newBuilder().build() : null);
		mResourceCache = (pCacheResources == true ? CacheBuilder.newBuilder().build() : null);
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

		internalWriteStructure(pToolkit, pScope, typeName, key, pStructure);
	}

	protected abstract void internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void deleteStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
		String key = pToolkit.createStructureRefStr(pScope, pStructure);
		if (mStructureCache != null)
			mStructureCache.invalidate(key);

		/* Now write the data to disk */

		internalDeleteStructure(pToolkit, pScope, key, pStructure);
	}

	protected abstract void internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pKey, Structure pStructure);

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
		Structure result = (mStructureCache == null ? null : mStructureCache.getIfPresent(pSerializedRef));
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

	protected abstract Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope,
		String pDefName, String pKey);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		if (mStructureDefinitionCache != null) {
			String key = pValue.getName();
			mStructureDefinitionCache.put(key, pValue);
		}

		internalWriteStructureDefinition(pToolkit, pScope, pValue);
	}

	protected abstract void internalWriteStructureDefinition( Toolkit pToolkit,  Scope pScope,
		 StructureDefinition pValue);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName) {
		StructureDefinition result =
			(mStructureDefinitionCache == null ? null : mStructureDefinitionCache.getIfPresent(pName));
		if (result != null)
			return result;

		result = internalLookupStructureDefinitionByName(pToolkit, pScope, pName);
		if ((result != null) && (mStructureDefinitionCache != null))
			mStructureDefinitionCache.put(pName, result);

		return result;
	}

	@Nullable
	protected abstract StructureDefinition internalLookupStructureDefinitionByName( Toolkit pToolkit,
		 Scope pScope,  String pName);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		if (mStructureDefinitionCache != null) {
			String key = pValue.getName();
			mStructureDefinitionCache.invalidate(key);
		}

		internalDeleteStructureDefinition(pToolkit, pScope, pValue);
	}

	protected abstract void internalDeleteStructureDefinition( Toolkit pToolkit,  Scope pScope,
		 StructureDefinition pValue);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope) {

		if (mStructureDefinitionCache != null) {
			Collection<StructureDefinitionRef> missing =
				internalGetAllMissingStructureDefinitionRefs(pToolkit, pScope, mStructureDefinitionCache);
			for (StructureDefinitionRef ref : missing) {
				StructureDefinition sd = ref.resolve();
				if (sd != null)
					mStructureDefinitionCache.put(sd.getName(), sd);
			}

			return Collections2.transform(mStructureDefinitionCache.asMap().values(),
				(sd) -> sd == null ? null : sd.getReference());
		}
		else
			return internalGetAllMissingStructureDefinitionRefs(pToolkit, pScope, null);
	}

	
	protected abstract Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(
		 Toolkit pToolkit,  Scope pScope,
		@Nullable Cache<String, StructureDefinition> pStructureDefinitionCache);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
		if (mEditorStructureDefinitionCacheByRef != null) {
			String key = pValue.getStructureDefinitionRef().getSerializedString();
			List<EditorStructureDefinition> list = mEditorStructureDefinitionCacheByRef.getIfPresent(key);
			if (list == null)
				list = ImmutableList.of();
			ImmutableList<EditorStructureDefinition> updatedList = ImmutableList.<EditorStructureDefinition> builder()
				.addAll(Collections2.filter(list,
					Predicates.not((a) -> a == null ? false : a.getName().equals(pValue.getName()))))
				.add(pValue).build();
			mEditorStructureDefinitionCacheByRef.put(key, updatedList);
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
		List<EditorStructureDefinition> result = (mEditorStructureDefinitionCacheByRef != null
			? mEditorStructureDefinitionCacheByRef.getIfPresent(key) : null);
		if (result != null)
			return result;

		result = internalLookupEditorStructureDefinitionByName(pToolkit, pScope, pRef);
		if ((result != null) && (mEditorStructureDefinitionCacheByRef != null))
			mEditorStructureDefinitionCacheByRef.put(key, result);

		return result;
	}

	protected abstract List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
		Scope pScope, StructureDefinitionRef pRef);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
		if (mEditorStructureDefinitionCacheByRef != null) {
			String key = pValue.getStructureDefinitionRef().getSerializedString();
			List<EditorStructureDefinition> list = mEditorStructureDefinitionCacheByRef.getIfPresent(key);
			if (list != null) {
				ImmutableList<EditorStructureDefinition> updatedList =
					ImmutableList.<EditorStructureDefinition> builder().addAll(Collections2.filter(list,
						Predicates.not((a) -> a == null ? false : a.getName().equals(pValue.getName())))).build();
				if (updatedList.isEmpty() == true)
					mEditorStructureDefinitionCacheByRef.invalidate(key);
				else
					mEditorStructureDefinitionCacheByRef.put(key, updatedList);
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
	protected String internalLookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		if (mResourceCache != null) {
			StringBuilder sb = new StringBuilder(pLocale.toString());
			sb.append(':').append(pKey);
			String key = sb.toString();
			String result = mResourceCache.getIfPresent(key);
			if (result != null)
				return result;

			result = internal2LookupResourceString(pToolkit, pScope, pLocale, pKey);
			if (result != null)
				mResourceCache.put(key, result);

			return result;
		}
		return internal2LookupResourceString(pToolkit, pScope, pLocale, pKey);
	}

	protected abstract String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
		String pKey);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue) {
		if (isResourceStringWritingSupported(pToolkit, pScope) == false)
			throw new UnsupportedOperationException();
		if (mResourceCache != null) {
			StringBuilder sb = new StringBuilder(pLocale.toLanguageTag());
			sb.append(':').append(pKey);
			String key = sb.toString();
			mResourceCache.put(key, pValue);
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
		if (mResourceCache != null) {
			StringBuilder sb = new StringBuilder(pLocale.toString());
			sb.append(':').append(pKey);
			String key = sb.toString();
			mResourceCache.invalidate(key);
		}

		internalDeleteResourceString(pToolkit, pScope, pLocale, pKey);
	}

	protected abstract void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey);
}

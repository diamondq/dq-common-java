package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractCachingPersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ResourceBundlePersistenceLayer extends AbstractCachingPersistenceLayer {

	/**
	 * The builder (generally used for the Config system)
	 */
	public static class ResourceBundlePersistenceLayerBuilder {

		private @Nullable String mResourceBaseName;

		/**
		 * Sets the resource base name
		 *
		 * @param pValue the base name
		 * @return the builder
		 */
		public ResourceBundlePersistenceLayerBuilder resourceBaseName(String pValue) {
			mResourceBaseName = pValue;
			return this;
		}

		/**
		 * Builds the layer
		 *
		 * @return the layer
		 */
		public ResourceBundlePersistenceLayer build() {
			String resourceBaseName = mResourceBaseName;
			if (resourceBaseName == null)
				throw new IllegalArgumentException("The mandatory field resourceBaseName was not set");
			return new ResourceBundlePersistenceLayer(resourceBaseName);
		}
	}

	protected final String mBaseName;

	public ResourceBundlePersistenceLayer(String pResourceBaseName) {
		super(false, -1, false, -1, false, -1, true, -1);
		mBaseName = pResourceBaseName;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected @Nullable String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
		String pKey) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(mBaseName, pLocale);
			return bundle.getString(pKey);
		}
		catch (MissingResourceException ex) {
			return null;
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
		return false;
	}

	@Override
	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
		return Collections.emptyList();
	}

	@Override
	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
		return Collections.emptyMap();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef,
	 *      java.lang.String, com.diamondq.common.model.interfaces.PropertyDefinition)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef, @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure, boolean, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected boolean internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure, boolean pMustMatchOldStructure, @Nullable Structure pOldStructure) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
	 */
	@Override
	protected @Nullable Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName,
		String pKey) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.Integer)
	 */
	@Override
	protected @Nullable StructureDefinition internalLookupStructureDefinitionByNameAndRevision(Toolkit pToolkit,
		Scope pScope, String pName, @Nullable Integer pRevision) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
	 */
	@Override
	protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
		Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	protected void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupEditorStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	protected @Nullable List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
		Scope pScope, StructureDefinitionRef pRef) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	protected void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	protected void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
		String pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		throw new UnsupportedOperationException();
	}

	public static ResourceBundlePersistenceLayerBuilder builder() {
		return new ResourceBundlePersistenceLayerBuilder();
	}

}

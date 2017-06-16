package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractCachingPersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundlePersistenceLayer extends AbstractCachingPersistenceLayer {

	protected final String mBaseName;

	public ResourceBundlePersistenceLayer(Scope pScope, String pResourceBaseName) {
		super(pScope, false, false, false, true);
		mBaseName = pResourceBaseName;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pKey, Structure pStructure) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
	 */
	@Override
	protected Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName, String pKey) {
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
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	protected StructureDefinition internalLookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope,
		String pName) {
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
		Scope pScope, Cache<String, StructureDefinition> pStructureDefinitionCache) {
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
	protected List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
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
}

package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractPersistenceLayer;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CombinedPersistenceLayer extends AbstractPersistenceLayer {

	private final PersistenceLayer	mStructurePersistenceLayer;

	private final PersistenceLayer	mStructureDefinitionPersistenceLayer;

	private final PersistenceLayer	mEditorStructureDefinitionPersistenceLayer;

	private final PersistenceLayer	mResourcePersistenceLayer;

	public CombinedPersistenceLayer(Scope pScope, PersistenceLayer pStructurePersistenceLayer,
		PersistenceLayer pStructureDefinitionPersistenceLayer,
		PersistenceLayer pEditorStructureDefinitionPersistenceLayer, PersistenceLayer pResourcePersistenceLayer) {
		super(pScope);
		mStructurePersistenceLayer = pStructurePersistenceLayer;
		mStructureDefinitionPersistenceLayer = pStructureDefinitionPersistenceLayer;
		mEditorStructureDefinitionPersistenceLayer = pEditorStructureDefinitionPersistenceLayer;
		mResourcePersistenceLayer = pResourcePersistenceLayer;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		mStructureDefinitionPersistenceLayer.writeStructureDefinition(pToolkit, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		mStructureDefinitionPersistenceLayer.deleteStructureDefinition(pToolkit, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope) {
		return mStructureDefinitionPersistenceLayer.getAllStructureDefinitionRefs(pToolkit, pScope);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName) {
		return mStructureDefinitionPersistenceLayer.lookupStructureDefinitionByName(pToolkit, pScope, pName);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
		mStructurePersistenceLayer.writeStructure(pToolkit, pScope, pStructure);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public Structure lookupStructureBySerializedRef(Toolkit pGenericToolkit, Scope pScope, String pSerializedRef) {
		return mStructurePersistenceLayer.lookupStructureBySerializedRef(pGenericToolkit, pScope, pSerializedRef);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void deleteStructure(Toolkit pToolkit, Scope pScope, Structure pValue) {
		mStructurePersistenceLayer.deleteStructure(pToolkit, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pEditorStructureDefinition) {
		mEditorStructureDefinitionPersistenceLayer.writeEditorStructureDefinition(pToolkit, pScope,
			pEditorStructureDefinition);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
		mEditorStructureDefinitionPersistenceLayer.deleteEditorStructureDefinition(pToolkit, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupEditorStructureDefinitionByRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		return mEditorStructureDefinitionPersistenceLayer.lookupEditorStructureDefinitionByRef(pToolkit, pScope, pRef);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		return mStructurePersistenceLayer.getAllStructuresByDefinition(pToolkit, pScope, pRef);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
		return mResourcePersistenceLayer.isResourceStringWritingSupported(pToolkit, pScope);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue) {
		mResourcePersistenceLayer.writeResourceString(pToolkit, pScope, pLocale, pKey, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	public void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		mResourcePersistenceLayer.deleteResourceString(pToolkit, pScope, pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
		return mResourcePersistenceLayer.getResourceStringLocales(pToolkit, pScope);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
	 */
	@Override
	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
		return mResourcePersistenceLayer.getResourceStringsByLocale(pToolkit, pScope, pLocale);
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractPersistenceLayer#lookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	public String lookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		return mResourcePersistenceLayer.lookupResourceString(pToolkit, pScope, pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractPersistenceLayer#internalLookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected String internalLookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		throw new UnsupportedOperationException();
	}

}

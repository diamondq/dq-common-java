package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractPersistenceLayer;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CombinedPersistenceLayer extends AbstractPersistenceLayer {

	private final List<PersistenceLayer>	mStructurePersistenceLayer;

	private final boolean					mStructurePersistenceLayerIsSingleton;

	private final List<PersistenceLayer>	mStructureDefinitionPersistenceLayer;

	private final boolean					mStructureDefinitionPersistenceLayerIsSingleton;

	private final List<PersistenceLayer>	mEditorStructureDefinitionPersistenceLayer;

	private final boolean					mEditorStructureDefinitionPersistenceLayerIsSingleton;

	private final List<PersistenceLayer>	mResourcePersistenceLayer;

	private final boolean					mResourcePersistenceLayerIsSingleton;

	public CombinedPersistenceLayer(Scope pScope, List<PersistenceLayer> pStructurePersistenceLayer,
		List<PersistenceLayer> pStructureDefinitionPersistenceLayer,
		List<PersistenceLayer> pEditorStructureDefinitionPersistenceLayer,
		List<PersistenceLayer> pResourcePersistenceLayer) {
		super(pScope);
		mStructurePersistenceLayer = ImmutableList.copyOf(pStructurePersistenceLayer);
		mStructurePersistenceLayerIsSingleton = (mStructurePersistenceLayer.size() == 1);
		mStructureDefinitionPersistenceLayer = ImmutableList.copyOf(pStructureDefinitionPersistenceLayer);
		mStructureDefinitionPersistenceLayerIsSingleton = (mStructureDefinitionPersistenceLayer.size() == 1);
		mEditorStructureDefinitionPersistenceLayer = ImmutableList.copyOf(pEditorStructureDefinitionPersistenceLayer);
		mEditorStructureDefinitionPersistenceLayerIsSingleton =
			(mEditorStructureDefinitionPersistenceLayer.size() == 1);
		mResourcePersistenceLayer = ImmutableList.copyOf(pResourcePersistenceLayer);
		mResourcePersistenceLayerIsSingleton = (mResourcePersistenceLayer.size() == 1);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		mStructureDefinitionPersistenceLayer.forEach((l) -> l.writeStructureDefinition(pToolkit, pScope, pValue));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		mStructureDefinitionPersistenceLayer.forEach((l) -> l.deleteStructureDefinition(pToolkit, pScope, pValue));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope) {
		if (mStructureDefinitionPersistenceLayerIsSingleton == true)
			return mStructureDefinitionPersistenceLayer.get(0).getAllStructureDefinitionRefs(pToolkit, pScope);

		ImmutableSet.Builder<StructureDefinitionRef> results = ImmutableSet.builder();
		mStructureDefinitionPersistenceLayer
			.forEach((l) -> results.addAll(l.getAllStructureDefinitionRefs(pToolkit, pScope)));
		return results.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName) {
		if (mStructureDefinitionPersistenceLayerIsSingleton == true)
			return mStructureDefinitionPersistenceLayer.get(0).lookupStructureDefinitionByName(pToolkit, pScope, pName);

		for (PersistenceLayer l : mStructureDefinitionPersistenceLayer) {
			StructureDefinition sd = l.lookupStructureDefinitionByName(pToolkit, pScope, pName);
			if (sd != null)
				return sd;
		}
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure) {
		mStructurePersistenceLayer.forEach((l) -> l.writeStructure(pToolkit, pScope, pStructure));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public Structure lookupStructureBySerializedRef(Toolkit pGenericToolkit, Scope pScope, String pSerializedRef) {
		if (mStructurePersistenceLayerIsSingleton == true)
			return mStructurePersistenceLayer.get(0).lookupStructureBySerializedRef(pGenericToolkit, pScope,
				pSerializedRef);

		for (PersistenceLayer l : mStructurePersistenceLayer) {
			Structure s = l.lookupStructureBySerializedRef(pGenericToolkit, pScope, pSerializedRef);
			if (s != null)
				return s;
		}
		return null;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void deleteStructure(Toolkit pToolkit, Scope pScope, Structure pValue) {
		mStructurePersistenceLayer.forEach((l) -> l.deleteStructure(pToolkit, pScope, pValue));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pEditorStructureDefinition) {
		mEditorStructureDefinitionPersistenceLayer
			.forEach((l) -> l.writeEditorStructureDefinition(pToolkit, pScope, pEditorStructureDefinition));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue) {
		mEditorStructureDefinitionPersistenceLayer
			.forEach((l) -> l.deleteEditorStructureDefinition(pToolkit, pScope, pValue));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupEditorStructureDefinitionByRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		if (mEditorStructureDefinitionPersistenceLayerIsSingleton == true)
			return mEditorStructureDefinitionPersistenceLayer.get(0).lookupEditorStructureDefinitionByRef(pToolkit,
				pScope, pRef);

		ImmutableList.Builder<EditorStructureDefinition> results = ImmutableList.builder();
		mEditorStructureDefinitionPersistenceLayer
			.forEach((l) -> results.addAll(l.lookupEditorStructureDefinitionByRef(pToolkit, pScope, pRef)));
		return results.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		if (mStructurePersistenceLayerIsSingleton == true)
			return mStructurePersistenceLayer.get(0).getAllStructuresByDefinition(pToolkit, pScope, pRef);

		ImmutableSet.Builder<Structure> results = ImmutableSet.builder();
		mStructurePersistenceLayer
			.forEach((l) -> results.addAll(l.getAllStructuresByDefinition(pToolkit, pScope, pRef)));
		return results.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
		for (PersistenceLayer l : mResourcePersistenceLayer)
			if (l.isResourceStringWritingSupported(pToolkit, pScope) == true)
				return true;
		return false;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#writeResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue) {
		for (PersistenceLayer l : mResourcePersistenceLayer)
			if (l.isResourceStringWritingSupported(pToolkit, pScope) == true)
				l.writeResourceString(pToolkit, pScope, pLocale, pKey, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#deleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	public void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		for (PersistenceLayer l : mResourcePersistenceLayer)
			if (l.isResourceStringWritingSupported(pToolkit, pScope) == true)
				l.deleteResourceString(pToolkit, pScope, pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
		if (mResourcePersistenceLayerIsSingleton == true)
			return mResourcePersistenceLayer.get(0).getResourceStringLocales(pToolkit, pScope);

		ImmutableSet.Builder<Locale> results = ImmutableSet.builder();
		mResourcePersistenceLayer.forEach((l) -> results.addAll(l.getResourceStringLocales(pToolkit, pScope)));
		return results.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
	 */
	@Override
	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
		if (mResourcePersistenceLayerIsSingleton == true)
			return mResourcePersistenceLayer.get(0).getResourceStringsByLocale(pToolkit, pScope, pLocale);

		ImmutableMap.Builder<String, String> results = ImmutableMap.builder();
		mResourcePersistenceLayer
			.forEach((l) -> results.putAll(l.getResourceStringsByLocale(pToolkit, pScope, pLocale)));
		return results.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractPersistenceLayer#lookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	public String lookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		if (mResourcePersistenceLayerIsSingleton == true)
			return mResourcePersistenceLayer.get(0).lookupResourceString(pToolkit, pScope, pLocale, pKey);

		for (PersistenceLayer l : mResourcePersistenceLayer) {
			String result = l.lookupResourceString(pToolkit, pScope, pLocale, pKey);
			if (result != null)
				return result;
		}
		return null;
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
package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorGroupDefinition;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class GenericToolkit implements Toolkit {

	private final ConcurrentMap<Scope, PersistenceLayer>	mPersistence	= Maps.newConcurrentMap();

	private final ConcurrentMap<String, Scope>				mScopes			= Maps.newConcurrentMap();

	public GenericToolkit() {
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getAllScopes()
	 */
	@Override
	public Collection<Scope> getAllScopes() {
		return mScopes.values();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getScope(java.lang.String)
	 */
	@Override
	public Scope getScope(String pValue) {
		return mScopes.get(pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getOrCreateScope(java.lang.String)
	 */
	@Override
	public Scope getOrCreateScope(String pName) {
		Scope scope = mScopes.get(pName);
		if (scope == null) {
			Scope newScope = new GenericScope(this, pName);
			if ((scope = mScopes.putIfAbsent(pName, newScope)) == null)
				scope = newScope;
		}
		return scope;
	}

	/**
	 * Adds the persistence layer for a given scope
	 * 
	 * @param pScope the scope
	 * @param pLayer the persistence layer
	 */
	public void addPersistenceLayer(Scope pScope, PersistenceLayer pLayer) {
		mPersistence.put(pScope, pLayer);
	}

	/**
	 * Internal helper method that either returns a PersistenceLayer or throws an exception explaining that the scope is
	 * unknown.
	 * 
	 * @param pScope the scope
	 * @return the persistence layer, never null.
	 */
	private PersistenceLayer getPersistenceLayer(Scope pScope) {
		PersistenceLayer layer = mPersistence.get(pScope);
		if (layer == null)
			throw new UnknownScopeException(pScope);
		return layer;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Scope pScope) {
		return getPersistenceLayer(pScope).getAllStructureDefinitionRefs(this, pScope);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureDefinition createNewStructureDefinition(Scope pScope, String pName) {
		return getPersistenceLayer(pScope).createNewStructureDefinition(this, pScope, pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void writeStructureDefinition(Scope pScope, StructureDefinition pValue) {
		if (pValue instanceof GenericStructureDefinition)
			((GenericStructureDefinition) pValue).validate();
		getPersistenceLayer(pScope).writeStructureDefinition(this, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureDefinitionRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public StructureDefinitionRef createStructureDefinitionRef(Scope pScope, StructureDefinition pResolvable) {
		return getPersistenceLayer(pScope).createStructureDefinitionRef(this, pScope, pResolvable);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createPropertyDefinitionRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public PropertyDefinitionRef createPropertyDefinitionRef(Scope pScope, PropertyDefinition pResolvable,
		StructureDefinition pContaining) {
		return getPersistenceLayer(pScope).createPropertyDefinitionRef(this, pScope, pResolvable, pContaining);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public StructureRef createStructureRef(Scope pScope, Structure pResolvable) {
		return getPersistenceLayer(pScope).createStructureRef(this, pScope, pResolvable);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRefStr(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public String createStructureRefStr(Scope pScope, Structure pResolvable) {
		return getPersistenceLayer(pScope).createStructureRefStr(this, pScope, pResolvable);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createPropertyRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Property, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public <T> PropertyRef<T> createPropertyRef(Scope pScope, Property<T> pResolvable, Structure pContaining) {
		return getPersistenceLayer(pScope).createPropertyRef(this, pScope, pResolvable, pContaining);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureDefinition lookupStructureDefinitionByName(Scope pScope, String pName) {
		return getPersistenceLayer(pScope).lookupStructureDefinitionByName(this, pScope, pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewPropertyDefinition(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public PropertyDefinition createNewPropertyDefinition(Scope pScope) {
		return getPersistenceLayer(pScope).createNewPropertyDefinition(this, pScope);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#collapsePrimaryKeys(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.List)
	 */
	@Override
	public String collapsePrimaryKeys(Scope pScope, List<Object> pNames) {
		return getPersistenceLayer(pScope).collapsePrimaryKeys(this, pScope, pNames);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public Structure lookupStructureBySerializedRef(Scope pScope, String pSerializedRef) {
		return getPersistenceLayer(pScope).lookupStructureBySerializedRef(this, pScope, pSerializedRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRefFromParts(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure, java.lang.String,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, java.util.List)
	 */
	@Override
	public StructureRef createStructureRefFromParts(Scope pScope, Structure pStructure, String pPropName,
		StructureDefinition pDef, List<Object> pPrimaryKeys) {
		return getPersistenceLayer(pScope).createStructureRefFromParts(this, pScope, pStructure, pPropName, pDef,
			pPrimaryKeys);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructuresByQuery(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, com.diamondq.common.model.interfaces.QueryBuilder,
	 *      java.util.Map)
	 */
	@Override
	public List<Structure> lookupStructuresByQuery(Scope pScope, StructureDefinition pStructureDefinition,
		QueryBuilder pBuilder, Map<String, Object> pParamValues) {
		return getPersistenceLayer(pScope).lookupStructuresByQuery(this, pScope, pStructureDefinition, pBuilder,
			pParamValues);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public Structure createNewStructure(Scope pScope, StructureDefinition pStructureDefinition) {
		return getPersistenceLayer(pScope).createNewStructure(this, pScope, pStructureDefinition);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void writeStructure(Scope pScope, Structure pStructure) {
		getPersistenceLayer(pScope).writeStructure(this, pScope, pStructure);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewProperty(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition, boolean, java.lang.Object)
	 */
	@Override
	public <TYPE> Property<TYPE> createNewProperty(Scope pScope, PropertyDefinition pPropertyDefinition,
		boolean pIsValueSet, TYPE pValue) {
		return getPersistenceLayer(pScope).createNewProperty(this, pScope, pPropertyDefinition, pIsValueSet, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewTranslatableString(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public TranslatableString createNewTranslatableString(Scope pScope, String pKey) {
		return getPersistenceLayer(pScope).createNewTranslatableString(this, pScope, pKey);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewEditorGroupDefinition(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public EditorGroupDefinition createNewEditorGroupDefinition(Scope pScope) {
		return getPersistenceLayer(pScope).createNewEditorGroupDefinition(this, pScope);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewEditorPropertyDefinition(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public EditorPropertyDefinition createNewEditorPropertyDefinition(Scope pScope) {
		return getPersistenceLayer(pScope).createNewEditorPropertyDefinition(this, pScope);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewEditorStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public EditorStructureDefinition createNewEditorStructureDefinition(Scope pScope, String pName,
		StructureDefinitionRef pRef) {
		return getPersistenceLayer(pScope).createNewEditorStructureDefinition(this, pScope, pName, pRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupEditorStructureDefinitionByRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Scope pScope,
		StructureDefinitionRef pRef) {
		return getPersistenceLayer(pScope).lookupEditorStructureDefinitionByRef(this, pScope, pRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void deleteEditorStructureDefinition(Scope pScope, EditorStructureDefinition pValue) {
		getPersistenceLayer(pScope).deleteEditorStructureDefinition(this, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void deleteStructure(Scope pScope, Structure pValue) {
		getPersistenceLayer(pScope).deleteStructure(this, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void deleteStructureDefinition(Scope pScope, StructureDefinition pValue) {
		getPersistenceLayer(pScope).deleteStructureDefinition(this, pScope, pValue);
	}

	@Override
	public void writeEditorStructureDefinition(Scope pScope, EditorStructureDefinition pEditorStructureDefinition) {
		getPersistenceLayer(pScope).writeEditorStructureDefinition(this, pScope, pEditorStructureDefinition);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureRef createStructureRefFromSerialized(Scope pScope, String pValue) {
		return getPersistenceLayer(pScope).createStructureRefFromSerialized(this, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createPropertyRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public <T> PropertyRef<T> createPropertyRefFromSerialized(Scope pScope, String pValue) {
		return getPersistenceLayer(pScope).createPropertyRefFromSerialized(this, pScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Scope pScope, StructureDefinitionRef pRef) {
		return getPersistenceLayer(pScope).getAllStructuresByDefinition(this, pScope, pRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupResourceString(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale, java.lang.String)
	 */
	@Override
	public String lookupResourceString(Scope pScope, Locale pLocale, String pKey) {
		return getPersistenceLayer(pScope).lookupResourceString(this, pScope, pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#setGlobalDefaultLocale(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale)
	 */
	@Override
	public void setGlobalDefaultLocale(Scope pScope, Locale pLocale) {
		if (pScope != null)
			getPersistenceLayer(pScope).setGlobalDefaultLocale(this, pScope, pLocale);
		else {
			for (Scope scope : getAllScopes())
				getPersistenceLayer(scope).setGlobalDefaultLocale(this, pScope, pLocale);
		}
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#setThreadLocale(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale)
	 */
	@Override
	public void setThreadLocale(Scope pScope, Locale pLocale) {
		if (pScope != null)
			getPersistenceLayer(pScope).setThreadLocale(this, pScope, pLocale);
		else {
			for (Scope scope : getAllScopes())
				getPersistenceLayer(scope).setThreadLocale(this, pScope, pLocale);
		}
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Scope pScope) {
		return getPersistenceLayer(pScope).isResourceStringWritingSupported(this, pScope);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeResourceString(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeResourceString(Scope pScope, Locale pLocale, String pKey, String pValue) {
		getPersistenceLayer(pScope).writeResourceString(this, pScope, pLocale, pKey, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteResourceString(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale, java.lang.String)
	 */
	@Override
	public void deleteResourceString(Scope pScope, Locale pLocale, String pKey) {
		getPersistenceLayer(pScope).deleteResourceString(this, pScope, pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getResourceStringLocales(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<Locale> getResourceStringLocales(Scope pScope) {
		return getPersistenceLayer(pScope).getResourceStringLocales(this, pScope);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale)
	 */
	@Override
	public Map<String, String> getResourceStringsByLocale(Scope pScope, Locale pLocale) {
		return getPersistenceLayer(pScope).getResourceStringsByLocale(this, pScope, pLocale);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewQueryBuilder(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public QueryBuilder createNewQueryBuilder(Scope pScope) {
		return getPersistenceLayer(pScope).createNewQueryBuilder(this, pScope);
	}
}

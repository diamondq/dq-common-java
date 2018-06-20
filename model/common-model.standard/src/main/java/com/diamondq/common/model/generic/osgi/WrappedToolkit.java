package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.EditorGroupDefinition;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StandardMigrations;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.ToolkitFactory;
import com.diamondq.common.model.interfaces.TranslatableString;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Octet;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedToolkit implements Toolkit {
	private static final Logger	sLogger								= LoggerFactory.getLogger(WrappedToolkit.class);

	protected Toolkit			mToolkit;

	protected volatile boolean	mWriteStructure						= false;

	protected volatile boolean	mWriteStructureDefinition			= false;

	protected volatile boolean	mWriteEditorStructureDefinition		= false;

	protected volatile boolean	mWriteResource						= false;

	protected volatile boolean	mDeleteStructure					= false;

	protected volatile boolean	mDeleteStructureDefinition			= false;

	protected volatile boolean	mDeleteEditorStructureDefinition	= false;

	protected volatile boolean	mDeleteResource						= false;

	@SuppressWarnings("null")
	public WrappedToolkit() {
		mToolkit = null;
	}

	public void onActivate() {
		sLogger.trace("onActivate() from {}", this);
		mToolkit = ToolkitFactory.newInstance().newToolkit();
	}

	private Scope dewrapScope(Scope pScope) {
		if (pScope instanceof WrappedScope)
			return ((WrappedScope) pScope).getScope();
		return pScope;
	}

	private @Nullable Scope dewrapNullableScope(@Nullable Scope pScope) {
		if (pScope == null)
			return null;
		if (pScope instanceof WrappedScope)
			return ((WrappedScope) pScope).getScope();
		return pScope;
	}

	public PersistenceLayer getPersistenceLayer(Scope pScope) {
		if (mToolkit instanceof GenericToolkit)
			return ((GenericToolkit) mToolkit).getPersistenceLayer(pScope);
		else
			throw new UnsupportedOperationException();
	}

	public Octet<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean> getModificationState() {
		return Octet.with(mWriteStructure, mWriteStructureDefinition, mWriteEditorStructureDefinition, mWriteResource,
			mDeleteStructure, mDeleteStructureDefinition, mDeleteEditorStructureDefinition, mDeleteResource);
	}

	public void setPersistenceLayer(Scope pScope, PersistenceLayer pLayer) {
		sLogger.trace("setPersistenceLayer({}, {}) from {}", pScope, pLayer, this);
		if (mToolkit instanceof GenericToolkit)
			((GenericToolkit) mToolkit).setPersistenceLayer(pScope, pLayer);
		else
			throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getAllScopes()
	 */
	@Override
	public Collection<Scope> getAllScopes() {
		return mToolkit.getAllScopes();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getScope(java.lang.String)
	 */
	@Override
	public @Nullable Scope getScope(String pName) {
		return mToolkit.getScope(pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getOrCreateScope(java.lang.String)
	 */
	@Override
	public Scope getOrCreateScope(String pName) {
		return mToolkit.getOrCreateScope(pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#removeScope(java.lang.String)
	 */
	@Override
	public boolean removeScope(String pName) {
		return mToolkit.removeScope(pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Scope pScope) {
		return mToolkit.getAllStructureDefinitionRefs(dewrapScope(pScope));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureDefinition createNewStructureDefinition(Scope pScope, String pName) {
		return mToolkit.createNewStructureDefinition(dewrapScope(pScope), pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, int)
	 */
	@Override
	public StructureDefinition createNewStructureDefinition(Scope pScope, String pName, int pRevision) {
		return mToolkit.createNewStructureDefinition(dewrapScope(pScope), pName, pRevision);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void writeStructureDefinition(Scope pScope, StructureDefinition pValue) {
		mWriteStructureDefinition = true;
		mToolkit.writeStructureDefinition(dewrapScope(pScope), pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public void deleteStructureDefinition(Scope pScope, StructureDefinition pValue) {
		mDeleteStructureDefinition = true;
		mToolkit.deleteStructureDefinition(dewrapScope(pScope), pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureDefinitionRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, boolean)
	 */
	@Override
	public StructureDefinitionRef createStructureDefinitionRef(Scope pScope, StructureDefinition pResolvable,
		boolean pWildcard) {
		return mToolkit.createStructureDefinitionRef(dewrapScope(pScope), pResolvable, pWildcard);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureDefinitionRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized) {
		return mToolkit.createStructureDefinitionRefFromSerialized(dewrapScope(pScope), pSerialized);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public StructureRef createStructureRef(Scope pScope, Structure pResolvable) {
		return mToolkit.createStructureRef(dewrapScope(pScope), pResolvable);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRefStr(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public String createStructureRefStr(Scope pScope, Structure pResolvable) {
		return mToolkit.createStructureRefStr(dewrapScope(pScope), pResolvable);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createPropertyDefinitionRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public PropertyDefinitionRef createPropertyDefinitionRef(Scope pScope, PropertyDefinition pResolvable,
		StructureDefinition pContaining) {
		return mToolkit.createPropertyDefinitionRef(dewrapScope(pScope), pResolvable, pContaining);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createPropertyRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Property, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public <@Nullable T> PropertyRef<T> createPropertyRef(Scope pScope, @Nullable Property<T> pResolvable,
		Structure pContaining) {
		return mToolkit.createPropertyRef(dewrapScope(pScope), pResolvable, pContaining);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public @Nullable StructureDefinition lookupStructureDefinitionByName(Scope pScope, String pName) {
		return mToolkit.lookupStructureDefinitionByName(dewrapScope(pScope), pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, java.lang.Integer)
	 */
	@Override
	public @Nullable StructureDefinition lookupStructureDefinitionByNameAndRevision(Scope pScope, String pName,
		@Nullable Integer pRevision) {
		return mToolkit.lookupStructureDefinitionByNameAndRevision(dewrapScope(pScope), pName, pRevision);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewPropertyDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	public PropertyDefinition createNewPropertyDefinition(Scope pScope, String pName, PropertyType pType) {
		return mToolkit.createNewPropertyDefinition(dewrapScope(pScope), pName, pType);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#collapsePrimaryKeys(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.List)
	 */
	@Override
	public String collapsePrimaryKeys(Scope pScope, List<@Nullable Object> pNames) {
		return mToolkit.collapsePrimaryKeys(dewrapScope(pScope), pNames);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public @Nullable Structure lookupStructureBySerializedRef(Scope pScope, String pSerializedRef) {
		return mToolkit.lookupStructureBySerializedRef(dewrapScope(pScope), pSerializedRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public Structure createNewStructure(Scope pScope, StructureDefinition pStructureDefinition) {
		return mToolkit.createNewStructure(dewrapScope(pScope), pStructureDefinition);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public void writeStructure(Scope pScope, Structure pStructure) {
		mWriteStructure = true;
		mToolkit.writeStructure(dewrapScope(pScope), pStructure);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public boolean writeStructure(Scope pScope, Structure pStructure, @Nullable Structure pOldStructure) {
		mWriteStructure = true;
		return mToolkit.writeStructure(dewrapScope(pScope), pStructure, pOldStructure);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteStructure(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public boolean deleteStructure(Scope pScope, Structure pOldStructure) {
		mDeleteStructure = true;
		return mToolkit.deleteStructure(dewrapScope(pScope), pOldStructure);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewProperty(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition, boolean, java.lang.Object)
	 */
	@Override
	public <@Nullable TYPE> Property<TYPE> createNewProperty(Scope pScope, PropertyDefinition pPropertyDefinition,
		boolean pIsValueSet, TYPE pValue) {
		return mToolkit.createNewProperty(dewrapScope(pScope), pPropertyDefinition, pIsValueSet, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewTranslatableString(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public TranslatableString createNewTranslatableString(Scope pScope, String pKey) {
		return mToolkit.createNewTranslatableString(dewrapScope(pScope), pKey);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupEditorStructureDefinitionByRef(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Scope pScope,
		StructureDefinitionRef pRef) {
		return mToolkit.lookupEditorStructureDefinitionByRef(dewrapScope(pScope), pRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewEditorGroupDefinition(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public EditorGroupDefinition createNewEditorGroupDefinition(Scope pScope) {
		return mToolkit.createNewEditorGroupDefinition(dewrapScope(pScope));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewEditorPropertyDefinition(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public EditorPropertyDefinition createNewEditorPropertyDefinition(Scope pScope) {
		return mToolkit.createNewEditorPropertyDefinition(dewrapScope(pScope));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewEditorStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public EditorStructureDefinition createNewEditorStructureDefinition(Scope pScope, String pName,
		StructureDefinitionRef pRef) {
		return mToolkit.createNewEditorStructureDefinition(dewrapScope(pScope), pName, pRef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeEditorStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void writeEditorStructureDefinition(Scope pScope, EditorStructureDefinition pEditorStructureDefinition) {
		mWriteEditorStructureDefinition = true;
		mToolkit.writeEditorStructureDefinition(dewrapScope(pScope), pEditorStructureDefinition);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	public void deleteEditorStructureDefinition(Scope pScope, EditorStructureDefinition pValue) {
		mDeleteEditorStructureDefinition = true;
		mToolkit.deleteEditorStructureDefinition(dewrapScope(pScope), pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureRef createStructureRefFromSerialized(Scope pScope, String pValue) {
		return mToolkit.createStructureRefFromSerialized(dewrapScope(pScope), pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStructureRefFromParts(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.Structure, java.lang.String,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, java.util.List)
	 */
	@Override
	public StructureRef createStructureRefFromParts(Scope pScope, @Nullable Structure pStructure,
		@Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys) {
		return mToolkit.createStructureRefFromParts(dewrapScope(pScope), pStructure, pPropName, pDef, pPrimaryKeys);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createPropertyRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Scope pScope, String pValue) {
		return mToolkit.createPropertyRefFromSerialized(dewrapScope(pScope), pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinitionRef, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Scope pScope, StructureDefinitionRef pRef,
		@Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
		return mToolkit.getAllStructuresByDefinition(dewrapScope(pScope), pRef, pParentKey, pParentPropertyDef);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupResourceString(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale, java.lang.String)
	 */
	@Override
	public @Nullable String lookupResourceString(Scope pScope, @Nullable Locale pLocale, String pKey) {
		return mToolkit.lookupResourceString(dewrapScope(pScope), pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#setGlobalDefaultLocale(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale)
	 */
	@Override
	public void setGlobalDefaultLocale(@Nullable Scope pScope, Locale pLocale) {
		mToolkit.setGlobalDefaultLocale(dewrapNullableScope(pScope), pLocale);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#setThreadLocale(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale)
	 */
	@Override
	public void setThreadLocale(@Nullable Scope pScope, @Nullable Locale pLocale) {
		mToolkit.setThreadLocale(dewrapNullableScope(pScope), pLocale);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Scope pScope) {
		return mToolkit.isResourceStringWritingSupported(dewrapScope(pScope));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#writeResourceString(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	public void writeResourceString(Scope pScope, Locale pLocale, String pKey, String pValue) {
		mWriteResource = true;
		mToolkit.writeResourceString(dewrapScope(pScope), pLocale, pKey, pValue);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#deleteResourceString(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale, java.lang.String)
	 */
	@Override
	public void deleteResourceString(Scope pScope, Locale pLocale, String pKey) {
		mDeleteResource = true;
		mToolkit.deleteResourceString(dewrapScope(pScope), pLocale, pKey);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Scope,
	 *      java.util.Locale)
	 */
	@Override
	public Map<String, String> getResourceStringsByLocale(Scope pScope, Locale pLocale) {
		return mToolkit.getResourceStringsByLocale(dewrapScope(pScope), pLocale);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#getResourceStringLocales(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<Locale> getResourceStringLocales(Scope pScope) {
		return mToolkit.getResourceStringLocales(dewrapScope(pScope));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createNewQueryBuilder(com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public QueryBuilder createNewQueryBuilder(Scope pScope) {
		return mToolkit.createNewQueryBuilder(dewrapScope(pScope));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupStructuresByQuery(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, com.diamondq.common.model.interfaces.QueryBuilder,
	 *      java.util.Map)
	 */
	@Override
	public List<Structure> lookupStructuresByQuery(Scope pScope, StructureDefinition pStructureDefinition,
		QueryBuilder pBuilder, @Nullable Map<String, Object> pParamValues) {
		return mToolkit.lookupStructuresByQuery(dewrapScope(pScope), pStructureDefinition, pBuilder, pParamValues);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#createStandardMigration(com.diamondq.common.model.interfaces.Scope,
	 *      com.diamondq.common.model.interfaces.StandardMigrations, java.lang.Object[])
	 */
	@Override
	public BiFunction<Structure, Structure, Structure> createStandardMigration(Scope pScope,
		StandardMigrations pMigrationType, @NonNull Object @Nullable... pParams) {
		return mToolkit.createStandardMigration(dewrapScope(pScope), pMigrationType, pParams);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#addMigration(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, int, int, java.util.function.BiFunction)
	 */
	@Override
	public void addMigration(Scope pScope, String pStructureDefinitionName, int pFromRevision, int pToRevision,
		BiFunction<Structure, Structure, Structure> pMigrationFunction) {
		mToolkit.addMigration(dewrapScope(pScope), pStructureDefinitionName, pFromRevision, pToRevision,
			pMigrationFunction);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#determineMigrationPath(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String, int, int)
	 */
	@Override
	public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
		Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision) {
		return mToolkit.determineMigrationPath(dewrapScope(pScope), pStructureDefName, pFromRevision, pToRevision);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Toolkit#lookupLatestStructureDefinitionRevision(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public @Nullable Integer lookupLatestStructureDefinitionRevision(Scope pScope, String pDefName) {
		return mToolkit.lookupLatestStructureDefinitionRevision(dewrapScope(pScope), pDefName);
	}

}

package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureAndProperty;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @param <STRUCTURECONFIGOBJ>
 */
public abstract class AbstractDocumentPersistenceLayer<STRUCTURECONFIGOBJ> extends AbstractCachingPersistenceLayer {

	protected final boolean	mPersistStructures;

	protected final boolean	mPersistStructureDefinitions;

	protected final boolean	mPersistEditorStructureDefinitions;

	protected final boolean	mPersistResources;

	public AbstractDocumentPersistenceLayer(Scope pScope, boolean pPersistStructures, boolean pCacheStructures,
		boolean pPersistStructureDefinitions, boolean pCacheStructureDefinitions,
		boolean pPersistEditorStructureDefinitions, boolean pCacheEditorStructureDefinitions, boolean pPersistResources,
		boolean pCacheResources) {
		super(pScope, pCacheStructures, pCacheStructureDefinitions, pCacheEditorStructureDefinitions, pCacheResources);
		mPersistStructures = pPersistStructures;
		mPersistStructureDefinitions = pPersistStructureDefinitions;
		mPersistEditorStructureDefinitions = pPersistEditorStructureDefinitions;
		mPersistResources = pPersistResources;
	}

	protected abstract STRUCTURECONFIGOBJ loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName,
		String pKey, boolean pCreateIfMissing);

	protected abstract <@NonNull R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig,
		boolean pIsMeta, String pKey, PropertyType pType, R pValue);

	protected abstract <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig,
		boolean pIsMeta, String pKey, PropertyType pType);

	protected abstract boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig,
		boolean pIsMeta, String pKey);

	protected abstract boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope,
		STRUCTURECONFIGOBJ pConfig, boolean pIsMeta, String pKey, PropertyType pType);

	protected abstract void saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		STRUCTURECONFIGOBJ pConfig);

	protected abstract boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig);

	protected abstract void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope,
		STRUCTURECONFIGOBJ pConfig, StructureDefinition pStructureDefinition, String pStructureDefName, String pKey,
		PropertyDefinition pPropDef, ImmutableList.Builder<StructureRef> pStructureRefListBuilder);

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure) {
		if (mPersistStructures == false)
			return;

		STRUCTURECONFIGOBJ config = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, true);

		boolean changed = false;

		String oldStructureDefName =
			getStructureConfigObjectProp(pToolkit, pScope, config, true, "structureDef", PropertyType.String);
		if (Objects.equals(oldStructureDefName, pDefName) == false) {
			setStructureConfigObjectProp(pToolkit, pScope, config, true, "structureDef", PropertyType.String, pDefName);
			changed = true;
		}

		Map<String, Property<?>> properties = pStructure.getProperties();

		for (Property<?> p : properties.values()) {
			PropertyDefinition propDef = p.getDefinition();
			if (propDef.getKeywords().containsEntry(CommonKeywordKeys.PERSIST, CommonKeywordValues.FALSE) == true)
				continue;
			Collection<String> containerValue = propDef.getKeywords().get(CommonKeywordKeys.CONTAINER);
			if ((containerValue != null) && (containerValue.isEmpty() == false)) {
				if (containerValue.contains(CommonKeywordValues.CONTAINER_PARENT)) {

					/*
					 * We are writing an object that is a pointer to a parent. See if the cached parent has this object,
					 * and if not, then invalidate the cache
					 */

					PropertyRef<?> parentPropRef = (PropertyRef<?>) p.getValue(pStructure);
					if (parentPropRef != null) {
						StructureAndProperty<?> structureAndProperty = parentPropRef.resolveToBoth();
						if ((structureAndProperty != null) && (structureAndProperty.property != null)) {
							@SuppressWarnings("unchecked")
							List<StructureRef> structureRefList = (List<StructureRef>) structureAndProperty.property
								.getValue(structureAndProperty.structure);
							boolean invalidate = true;
							if (structureRefList != null) {
								StructureRef thisRef = pToolkit.createStructureRef(pScope, pStructure);
								for (StructureRef testRef : structureRefList) {
									if (testRef.equals(thisRef)) {
										invalidate = false;
										break;
									}
								}
							}
							if (invalidate == true)
								invalidateStructure(pToolkit, pScope, structureAndProperty.structure);
						}
					}
				}
				if (persistContainerProp(pToolkit, pScope, pStructure, p) == false)
					continue;
			}

			String propName = propDef.getName();
			switch (p.getDefinition().getType()) {
			case String: {
				if (p.isValueSet() == true) {
					String value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String);
					String newValue = (String) p.getValue(pStructure);
					if (newValue == null)
						newValue = "";
					if (Objects.equals(value, newValue) == false) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String,
							newValue);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.String) == true)
						changed = true;
				}
				break;
			}
			case Boolean: {
				if (p.isValueSet() == true) {
					Boolean value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Boolean);
					Boolean newValue = (Boolean) p.getValue(pStructure);
					if (newValue == null)
						newValue = false;
					if (value != newValue) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Boolean,
							newValue);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.Boolean) == true)
						changed = true;
				}
				break;
			}
			case Decimal: {
				if (p.isValueSet() == true) {
					BigDecimal value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Decimal);
					Number newValue = (Number) p.getValue(pStructure);
					BigDecimal newDec;
					if (newValue == null)
						newDec = new BigDecimal(0.0);
					else if (newValue instanceof BigDecimal)
						newDec = (BigDecimal) newValue;
					else if (newValue instanceof BigInteger)
						newDec = new BigDecimal((BigInteger) newValue);
					else if (newValue instanceof Byte)
						newDec = new BigDecimal((Byte) newValue);
					else if (newValue instanceof Double)
						newDec = new BigDecimal((Double) newValue);
					else if (newValue instanceof Float)
						newDec = new BigDecimal((Float) newValue);
					else if (newValue instanceof Integer)
						newDec = new BigDecimal((Float) newValue);
					else if (newValue instanceof Long)
						newDec = new BigDecimal((Long) newValue);
					else if (newValue instanceof Short)
						newDec = new BigDecimal((Short) newValue);
					else
						throw new UnsupportedOperationException();
					if (value.equals(newDec) == false) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Decimal,
							newDec);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.Decimal) == true)
						changed = true;
				}
				break;
			}
			case Integer: {
				if (p.isValueSet() == true) {
					Integer value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Integer);
					Integer newValue = (Integer) p.getValue(pStructure);
					if (newValue == null)
						newValue = 0;
					if (value != newValue) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Integer,
							newValue);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.Integer) == true)
						changed = true;
				}
				break;
			}
			case PropertyRef: {
				if (p.isValueSet() == true) {
					String value = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.PropertyRef);
					PropertyRef<?> newValueRef = (PropertyRef<?>) p.getValue(pStructure);
					String newValue;
					if (newValueRef == null)
						newValue = "";
					else
						newValue = newValueRef.getSerializedString();
					if (Objects.equals(value, newValue) == false) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
							PropertyType.PropertyRef, newValue);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.PropertyRef) == true)
						changed = true;
				}
				break;
			}
			case StructureRef: {
				if (p.isValueSet() == true) {
					String value = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.StructureRef);
					StructureRef newValueRef = (StructureRef) p.getValue(pStructure);
					String newValue;
					if (newValueRef == null)
						newValue = "";
					else
						newValue = newValueRef.getSerializedString();
					if (Objects.equals(value, newValue) == false) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
							PropertyType.StructureRef, newValue);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.StructureRef) == true)
						changed = true;
				}
				break;
			}
			case StructureRefList: {
				if (p.isValueSet() == true) {
					String[] value = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.StructureRefList);
					String[] newValues = (String[]) p.getValue(pStructure);
					if (newValues == null)
						newValues = new String[0];
					if (Objects.equals(value, newValues) == false) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
							PropertyType.StructureRefList, newValues);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.StructureRefList) == true)
						changed = true;
				}
				break;
			}
			case EmbeddedStructureList: {
				throw new UnsupportedOperationException();
			}
			case Image: {
				throw new UnsupportedOperationException();
			}
			case Binary: {
				throw new UnsupportedOperationException();
			}
			case Timestamp: {
				if (p.isValueSet() == true) {
					Long value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Timestamp);
					Long newValue = (Long) p.getValue(pStructure);
					if (newValue == null)
						newValue = 0L;
					if (value != newValue) {
						setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Timestamp,
							newValue);
						changed = true;
					}
				}
				else {
					if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.Timestamp) == true)
						changed = true;
				}
				break;
			}
			}

		}

		if ((changed == true) || (isStructureConfigChanged(pToolkit, pScope, config) == true))
			saveStructureConfigObject(pToolkit, pScope, pDefName, pKey, config);
	}

	/**
	 * Checks whether this Property which has the CONTAINER keyword attached to it, should be persisted.
	 * 
	 * @param pToolkit the toolkit
	 * @param pScope the scope
	 * @param pStructure the structure
	 * @param pProp the Property
	 * @return true if it should be persisted or false if should not be
	 */
	protected abstract boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure,
		Property<?> pProp);

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef) {
		if (mPersistStructures == false)
			return Collections.emptyList();

		StructureDefinition sd = pRef.resolve();
		ImmutableList.Builder<StructureRef> builder = ImmutableList.builder();
		internalPopulateChildStructureList(pToolkit, pScope, null, sd, sd.getName(), null, null, builder);
		ImmutableList.Builder<Structure> sBuilder = ImmutableList.builder();
		for (StructureRef ref : builder.build())
			sBuilder.add(ref.resolve());
		return sBuilder.build();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
	 */
	@Override
	protected Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName, String pKey) {
		if (mPersistStructures == false)
			return null;

		STRUCTURECONFIGOBJ config = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, false);
		if (config == null)
			return null;

		String structureDefName =
			getStructureConfigObjectProp(pToolkit, pScope, config, true, "structureDef", PropertyType.String);

		StructureDefinition structureDef =
			mScope.getToolkit().lookupStructureDefinitionByName(mScope, structureDefName);
		if (structureDef == null)
			throw new IllegalArgumentException("The structure at " + pKey + " refers to a StructureDefinition "
				+ structureDefName + " that does not exist");

		Structure structure = mScope.getToolkit().createNewStructure(mScope, structureDef);
		int lastSlash = pKey.lastIndexOf('/');
		int secondLastSlash = pKey.lastIndexOf('/', lastSlash - 1);
		String parentRef = null;
		if (secondLastSlash != -1)
			parentRef = pKey.substring(0, secondLastSlash);
		if (parentRef != null) {
			Collection<Property<PropertyRef<?>>> parentProps = structure.lookupPropertiesByKeyword(
				CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT, PropertyType.PropertyRef);
			Property<PropertyRef<?>> parentProp = Iterables.getFirst(parentProps, null);
			if (parentProp != null)
				structure = structure.updateProperty(
					parentProp.setValue(mScope.getToolkit().createPropertyRefFromSerialized(mScope, parentRef)));
		}

		/* See if there are children */

		Collection<String> childrenPropNames = structureDef.lookupPropertyDefinitionNamesByKeyword(
			CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_CHILDREN, null);
		if (childrenPropNames.isEmpty() == false) {
			for (String childrenPropName : childrenPropNames) {
				PropertyDefinition propDef = structureDef.lookupPropertyDefinitionByName(childrenPropName);
				Property<List<StructureRef>> childProp = structure.lookupPropertyByName(childrenPropName);
				ImmutableList.Builder<StructureRef> builder = ImmutableList.builder();
				internalPopulateChildStructureList(pToolkit, pScope, config, structureDef, structureDefName, pKey,
					propDef, builder);
				structure = structure.updateProperty(childProp.setValue(builder.build()));
			}
		}

		for (Property<?> p : structure.getProperties().values()) {
			String propName = p.getDefinition().getName();
			switch (p.getDefinition().getType()) {
			case String: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					String string =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String);
					if ("".equals(string))
						string = null;
					@SuppressWarnings("unchecked")
					Property<String> ap = (Property<String>) p;
					ap = ap.setValue(string);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			case Boolean: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					boolean value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Boolean);
					@SuppressWarnings("unchecked")
					Property<Boolean> ap = (Property<Boolean>) p;
					ap = ap.setValue(value);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			case Decimal: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					BigDecimal value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Decimal);
					@SuppressWarnings("unchecked")
					Property<BigDecimal> ap = (Property<BigDecimal>) p;
					ap = ap.setValue(value);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			case Integer: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					int value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Integer);
					@SuppressWarnings("unchecked")
					Property<Integer> ap = (Property<Integer>) p;
					ap = ap.setValue(value);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			case PropertyRef: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					String string = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.PropertyRef);
					if ("".equals(string))
						string = null;
					PropertyRef<?> ref =
						(string == null ? null : pToolkit.createPropertyRefFromSerialized(pScope, string));
					@SuppressWarnings("unchecked")
					Property<PropertyRef<?>> ap = (Property<PropertyRef<?>>) p;
					ap = ap.setValue(ref);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			case StructureRef: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					String string = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
						PropertyType.StructureRef);
					if ("".equals(string))
						string = null;
					StructureRef ref =
						(string == null ? null : pToolkit.createStructureRefFromSerialized(pScope, string));
					@SuppressWarnings("unchecked")
					Property<StructureRef> ap = (Property<StructureRef>) p;
					ap = ap.setValue(ref);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			case StructureRefList: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					throw new UnsupportedOperationException();
				}
				break;
			}
			case EmbeddedStructureList: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					throw new UnsupportedOperationException();
				}
				break;
			}
			case Image: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					throw new UnsupportedOperationException();
				}
				break;
			}
			case Binary: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					throw new UnsupportedOperationException();
				}
				break;
			}
			case Timestamp: {
				if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
					Long value =
						getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Timestamp);
					@SuppressWarnings("unchecked")
					Property<Long> ap = (Property<Long>) p;
					ap = ap.setValue(value);
					structure = structure.updateProperty(ap);
				}
				break;
			}
			}
		}
		return structure;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	protected StructureDefinition internalLookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope,
		String pName) {
		if (mPersistStructureDefinitions == false)
			return null;

		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
	 */
	@Override
	protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
		Scope pScope, Cache<String, StructureDefinition> pStructureDefinitionCache) {
		if (mPersistStructureDefinitions == false)
			return Collections.emptyList();
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		if (mPersistEditorStructureDefinitions == false)
			return;

		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
		if (mPersistStructureDefinitions == false)
			return;

		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		if (mPersistResources == false)
			return null;

		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
	 */
	@Override
	protected void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
		String pValue) {
		if (mPersistResources == false)
			return;

		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	protected void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
		if (mPersistResources == false)
			return;

		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
	 */
	@Override
	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
		if (mPersistResources == false)
			return true;
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupEditorStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	protected List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
		Scope pScope, StructureDefinitionRef pRef) {
		if (mPersistEditorStructureDefinitions == false)
			return null;
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	protected void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pValue) {
		if (mPersistEditorStructureDefinitions == false)
			return;
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
	 */
	@Override
	protected void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pValue) {
		if (mPersistEditorStructureDefinitions == false)
			return;
		throw new UnsupportedOperationException();
	}
}
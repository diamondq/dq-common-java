package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureRef;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenericStructure implements Structure {

	private final Scope								mScope;

	private final StructureDefinition				mDefinition;

	private final ImmutableMap<String, Property<?>>	mProperties;

	private final Memoizer							mMemoizer	= new Memoizer();

	public GenericStructure(Scope pScope, StructureDefinition pDefinition, Map<String, Property<?>> pProperties) {
		super();
		assert pScope != null && pDefinition != null;
		mScope = pScope;
		mDefinition = pDefinition;
		ImmutableMap.Builder<String, Property<?>> b = ImmutableMap.builder();
		if (pProperties == null) {
			for (PropertyDefinition pd : pDefinition.getAllProperties().values()) {
				String name = pd.getName();
				Property<?> p = mScope.getToolkit().createNewProperty(mScope, pd, false, null);
				b.put(name, p);
			}
		}
		else {
			for (Map.Entry<String, Property<?>> pair : pProperties.entrySet()) {
				Property<?> existingProperty = pair.getValue();
				boolean isSet = existingProperty.isValueSet();
				PropertyDefinition pd =
					pDefinition.lookupPropertyDefinitionByName(existingProperty.getDefinition().getName());
				Property<?> newProp = mScope.getToolkit().createNewProperty(mScope, pd, isSet,
					isSet == true ? existingProperty.getValue(this) : null);
				b.put(pair.getKey(), newProp);
			}
		}
		mProperties = b.build();
	}

	private <T> PropertyRef<T> internalGetContainerRef() {
		Collection<String> names = mDefinition.lookupPropertyDefinitionNamesByKeyword(CommonKeywordKeys.CONTAINER,
			CommonKeywordValues.CONTAINER_PARENT, PropertyType.PropertyRef);

		String firstName = Iterables.getFirst(names, null);
		Property<PropertyRef<T>> first = firstName == null ? null : lookupPropertyByName(firstName);
		return first == null ? null : first.getValue(this);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#getContainerRef()
	 */
	@Override
	public <T> PropertyRef<T> getContainerRef() {
		return mMemoizer.memoize(this::internalGetContainerRef, "gcr");
	}

	private StructureRef internalGetReference() {
		return mScope.getToolkit().createStructureRef(mScope, GenericStructure.this);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Resolvable#getReference()
	 */
	@Override
	public StructureRef getReference() {
		return mMemoizer.memoize(this::internalGetReference, "gr");
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#getDefinition()
	 */
	@Override
	public StructureDefinition getDefinition() {
		return mDefinition;
	}

	private String internalGetLocalName() {
		List<String> propNames = mDefinition.lookupPrimaryKeyNames();
		List<Object> names = Lists.transform(propNames, (n) -> lookupPropertyByName(n).getValue(GenericStructure.this));
		return mScope.getToolkit().collapsePrimaryKeys(mScope, names);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#getLocalName()
	 */
	@Override
	public String getLocalName() {
		return mMemoizer.memoize(this::internalGetLocalName, "gn");
	}

	private StructureRef internalGetParentRef() {
		Collection<String> names = mDefinition.lookupPropertyDefinitionNamesByKeyword(CommonKeywordKeys.INHERIT_PARENT,
			null, PropertyType.StructureRef);

		String firstName = Iterables.getFirst(names, null);
		Property<StructureRef> first = firstName == null ? null : lookupPropertyByName(firstName);
		return first == null ? null : first.getValue(this);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#getParentRef()
	 */
	@Override
	public StructureRef getParentRef() {
		return mMemoizer.memoize(this::internalGetParentRef, "gpr");
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#getProperties()
	 */
	@Override
	public Map<String, Property<?>> getProperties() {
		return mProperties;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#updateProperty(com.diamondq.common.model.interfaces.Property)
	 */
	@Override
	public Structure updateProperty(Property<?> pValue) {
		return new GenericStructure(mScope, mDefinition, ImmutableMap.<String, Property<?>> builder()
			.putAll(Maps.filterKeys(mProperties, Predicates.not(Predicates.equalTo(pValue.getDefinition().getName()))))
			.put(pValue.getDefinition().getName(), pValue).build());
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#lookupPropertyByName(java.lang.String)
	 */
	@Override
	public <T> Property<T> lookupPropertyByName(String pName) {
		Property<?> prop = mProperties.get(pName);
		@SuppressWarnings({"cast", "unchecked", "rawtypes"})
		Property<T> result = (Property<T>) (Property) prop;
		return result;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Structure#lookupPropertiesByKeyword(java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	public <T> Collection<Property<T>> lookupPropertiesByKeyword(String pKey, String pValue, PropertyType pType) {
		Collection<String> names = mDefinition.lookupPropertyDefinitionNamesByKeyword(pKey, pValue, pType);
		return Collections2.transform(names, (n) -> lookupPropertyByName(n));
	}

	/**
	 * Internal function that calculates the hash code for this object
	 * 
	 * @return the hash code
	 */
	private Integer internalHashCode() {
		return Objects.hash(mScope, mDefinition, mProperties);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mMemoizer.memoize(this::internalHashCode, "hc");
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object pObj) {
		if (this == pObj)
			return true;
		if (pObj == null)
			return false;
		if (getClass() != pObj.getClass())
			return false;
		GenericStructure other = (GenericStructure) pObj;
		return Objects.equals(mScope, other.mScope) && Objects.equals(mDefinition, other.mDefinition)
			&& Objects.equals(mProperties, other.mProperties);
	}
}

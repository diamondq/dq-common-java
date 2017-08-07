package com.diamondq.common.model.generic;

import com.diamondq.common.lambda.Memoizer;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * 
 */
public class GenericStructureDefinition implements StructureDefinition {

	private final Scope										mScope;

	private final String									mName;

	private final @Nullable TranslatableString				mLabel;

	private final boolean									mSingleInstance;

	private final ImmutableMap<String, PropertyDefinition>	mProperties;

	private final ImmutableSet<StructureDefinitionRef>		mParentDefinitions;

	private final ImmutableMultimap<String, String>			mKeywords;

	private transient final Memoizer						mMemoizer			= new Memoizer();

	private static final Pattern							sValidNamePattern	= Pattern.compile("^[0-9a-zA-Z.-]+$");

	public GenericStructureDefinition(Scope pScope, String pName, @Nullable TranslatableString pLabel,
		boolean pSingleInstance, @Nullable Map<String, PropertyDefinition> pProperties,
		@Nullable Set<StructureDefinitionRef> pParentDefinitions, @Nullable Multimap<String, String> pKeywords) {
		super();
		mScope = pScope;
		mName = pName;
		mLabel = pLabel;
		mSingleInstance = pSingleInstance;
		// add virtual _type property
		mProperties = pProperties == null ? ImmutableMap.of() : ImmutableMap.copyOf(pProperties);
		mParentDefinitions = pParentDefinitions == null ? ImmutableSet.of() : ImmutableSet.copyOf(pParentDefinitions);
		mKeywords = pKeywords == null ? ImmutableMultimap.of() : ImmutableMultimap.copyOf(pKeywords);

		/* Validate the properties */

		mProperties.values().forEach((p) -> {
			if (p instanceof GenericPropertyDefinition)
				((GenericPropertyDefinition) p).validate();
		});
	}

	public void validate() {

		if (sValidNamePattern.matcher(mName).matches() == false)
			throw new IllegalArgumentException(
				"The StructureDefinition must have a valid name, which can only be the characters 0-9, a-z, A-Z, . and -.");
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	@Override
	public @Nullable TranslatableString getLabel() {
		return mLabel;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#setLabel(com.diamondq.common.model.interfaces.TranslatableString)
	 */
	@Override
	public StructureDefinition setLabel(@Nullable TranslatableString pValue) {
		return new GenericStructureDefinition(mScope, mName, pValue, mSingleInstance, mProperties, mParentDefinitions,
			mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#isSingleInstance()
	 */
	@Override
	public boolean isSingleInstance() {
		return mSingleInstance;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#setSingleInstance(boolean)
	 */
	@Override
	public StructureDefinition setSingleInstance(boolean pValue) {
		return new GenericStructureDefinition(mScope, mName, mLabel, pValue, mProperties, mParentDefinitions,
			mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getPropertyDefinitions()
	 */
	@Override
	public Map<String, PropertyDefinition> getPropertyDefinitions() {
		return mProperties;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#addPropertyDefinition(com.diamondq.common.model.interfaces.PropertyDefinition)
	 */
	@Override
	public StructureDefinition addPropertyDefinition(PropertyDefinition pValue) {
		String name = pValue.getName();
		return new GenericStructureDefinition(mScope, mName, mLabel, mSingleInstance,
			ImmutableMap.<String, PropertyDefinition> builder().putAll(mProperties).put(name, pValue).build(),
			mParentDefinitions, mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#removePropertyDefinition(com.diamondq.common.model.interfaces.PropertyDefinition)
	 */
	@Override
	public StructureDefinition removePropertyDefinition(PropertyDefinition pValue) {
		return new GenericStructureDefinition(mScope, mName, mLabel, mSingleInstance,
			Maps.filterValues(mProperties, Predicates.not(Predicates.equalTo(pValue))), mParentDefinitions, mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getParentDefinitions()
	 */
	@Override
	public Set<StructureDefinitionRef> getParentDefinitions() {
		return mParentDefinitions;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#addParentDefinition(com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public StructureDefinition addParentDefinition(StructureDefinitionRef pValue) {
		return new GenericStructureDefinition(mScope, mName, mLabel, mSingleInstance, mProperties,
			ImmutableSet.<StructureDefinitionRef> builder().addAll(mParentDefinitions).add(pValue).build(), mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#removeParentDefinition(com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public StructureDefinition removeParentDefinition(StructureDefinitionRef pValue) {
		return new GenericStructureDefinition(mScope, mName, mLabel, mSingleInstance, mProperties,
			Sets.filter(mParentDefinitions, Predicates.not(Predicates.equalTo(pValue))), mKeywords);
	}

	public Map<String, PropertyDefinition> internalGetAllProperties() {
		Builder<String, PropertyDefinition> builder = ImmutableMap.builder();

		/* Add the current properties */

		builder.putAll(getPropertyDefinitions());

		/* Add all the parent properties */

		Iterables.transform(mParentDefinitions, (sdr) -> sdr.resolve()).forEach((sd) -> {
			if (sd != null)
				builder.putAll(sd.getAllProperties());
		});

		return builder.build();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getAllProperties()
	 */
	@Override
	public Map<String, PropertyDefinition> getAllProperties() {
		return mMemoizer.memoize(this::internalGetAllProperties, "gap");
	}

	private StructureDefinitionRef internalGetReference() {
		return mScope.getToolkit().createStructureDefinitionRef(mScope, this);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Resolvable#getReference()
	 */
	@Override
	public StructureDefinitionRef getReference() {
		return mMemoizer.memoize(this::internalGetReference, "gr");
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#lookupPropertyDefinitionByName(java.lang.String)
	 */
	@Override
	public @Nullable PropertyDefinition lookupPropertyDefinitionByName(String pName) {
		return mMemoizer.memoize(this::internalGetAllProperties, "gap").get(pName);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#createNewStructure()
	 */
	@Override
	public Structure createNewStructure() {
		return mScope.getToolkit().createNewStructure(mScope, this);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getKeywords()
	 */
	@Override
	public Multimap<String, String> getKeywords() {
		return mKeywords;
	}

	private Multimap<String, String> internalGetAllKeywords() {
		ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

		/* Add the current keywords */

		builder.putAll(getKeywords());

		/* Add all the parent properties */

		Iterables.transform(mParentDefinitions, (sdr) -> sdr.resolve()).forEach((sd) -> {
			if (sd != null)
				builder.putAll(sd.getAllKeywords());
		});

		return builder.build();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getAllKeywords()
	 */
	@Override
	public Multimap<String, String> getAllKeywords() {
		return mMemoizer.memoize(this::internalGetAllKeywords, "gak");
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#addKeyword(java.lang.String, java.lang.String)
	 */
	@Override
	public StructureDefinition addKeyword(String pKey, String pValue) {
		return new GenericStructureDefinition(mScope, mName, mLabel, mSingleInstance, mProperties, mParentDefinitions,
			ImmutableMultimap.<String, String> builder()
				.putAll(Multimaps.filterEntries(mKeywords,
					Predicates
						.<Entry<String, String>> not((e) -> pKey.equals(e.getKey()) && pValue.equals(e.getValue()))))
				.put(pKey, pValue).build());
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#removeKeyword(java.lang.String, java.lang.String)
	 */
	@Override
	public StructureDefinition removeKeyword(String pKey, String pValue) {
		return new GenericStructureDefinition(mScope, mName, mLabel, mSingleInstance, mProperties, mParentDefinitions,
			Multimaps.filterEntries(mKeywords,
				Predicates.<Entry<String, String>> not((e) -> pKey.equals(e.getKey()) && pValue.equals(e.getValue()))));
	}

	/**
	 * Internal helper function that finds the PropertyDefinition names that match a given keyword key/value and
	 * optionally the PropertyType.
	 * 
	 * @param pKey the key
	 * @param pValue the value (or null if it matches all values)
	 * @param pType the type (or null if it matches all types)
	 * @return the list of matching names
	 */
	private Collection<String> internalLookupPropertyDefinitionNamesByKeyword(String pKey, @Nullable String pValue,
		@Nullable PropertyType pType) {

		/* Build the actual list of keyword based PropertyDefinitions */

		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		for (PropertyDefinition pd : getAllProperties().values()) {
			if ((pType != null) && (pType.equals(pd.getType()) == false))
				continue;
			if (pValue != null) {
				if (pd.getKeywords().containsEntry(pKey, pValue) == true)
					builder.add(pd.getName());
			}
			else {
				if (pd.getKeywords().containsKey(pKey) == true)
					builder.add(pd.getName());
			}
		}

		return builder.build();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#lookupPropertyDefinitionNamesByKeyword(java.lang.String,
	 *      java.lang.String, com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	public Collection<String> lookupPropertyDefinitionNamesByKeyword(String pKey, @Nullable String pValue, @Nullable PropertyType pType) {
		return mMemoizer.memoize(this::internalLookupPropertyDefinitionNamesByKeyword, pKey, pValue, pType, "pdnbk");
	}

	/**
	 * Internal helper function for doing the actual work of looking up the primaryKeyNames
	 * 
	 * @return the list of primary key PropertyDescription names
	 */
	private List<String> internalLookupPrimaryKeyNames() {

		/* Find the primary keys */

		TreeMap<Integer, String> names = Maps.newTreeMap();
		for (PropertyDefinition def : getAllProperties().values()) {
			if (def.isPrimaryKey() == false)
				continue;
			int primaryKeyOrder = def.getPrimaryKeyOrder();
			names.put(primaryKeyOrder, def.getName());
		}

		return ImmutableList.copyOf(names.values());
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#lookupPrimaryKeyNames()
	 */
	@Override
	public List<String> lookupPrimaryKeyNames() {
		return mMemoizer.memoize(this::internalLookupPrimaryKeyNames, "pkn");
	}

	/**
	 * Internal function that calculates the hash code for this object
	 * 
	 * @return the hash code
	 */
	private Integer internalHashCode() {
		return Objects.hash(mScope, mKeywords, mLabel, mName, mParentDefinitions, mProperties, mSingleInstance);
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
	public boolean equals(@Nullable Object pObj) {
		if (this == pObj)
			return true;
		if (pObj == null)
			return false;
		if (getClass() != pObj.getClass())
			return false;
		GenericStructureDefinition other = (GenericStructureDefinition) pObj;
		return Objects.equals(mScope, other.mScope) && Objects.equals(mKeywords, other.mKeywords)
			&& Objects.equals(mLabel, other.mLabel) && Objects.equals(mName, other.mName)
			&& Objects.equals(mParentDefinitions, other.mParentDefinitions)
			&& Objects.equals(mProperties, other.mProperties) && Objects.equals(mSingleInstance, other.mSingleInstance);
	}
}

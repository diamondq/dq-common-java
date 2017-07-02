package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.PropertyPattern;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Script;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class GenericPropertyDefinition implements PropertyDefinition {

	private final Scope									mScope;

	private final String								mName;

	private final TranslatableString					mLabel;

	private final boolean								mIsPrimaryKey;

	private final int									mPrimaryKeyOrder;

	private final PropertyType							mType;

	private final Script								mValidationScript;

	private final String								mDefaultValue;

	private final Script								mDefaultValueScript;

	private final ImmutableSet<StructureDefinitionRef>	mReferenceTypes;

	private final BigDecimal							mMinValue;

	private final BigDecimal							mMaxValue;

	private final Integer								mMaxLength;

	private final boolean								mFinal;

	private final PropertyPattern						mPropertyPattern;

	private final ImmutableMultimap<String, String>		mKeywords;

	private static final Pattern						sValidNamePattern	= Pattern.compile("^[0-9a-zA-Z.\\-]+$");

	public GenericPropertyDefinition(Scope pScope, String pName, TranslatableString pLabel, boolean pIsPrimaryKey,
		int pPrimaryKeyOrder, PropertyType pType, Script pValidationScript, String pDefaultValue,
		Script pDefaultValueScript, Collection<StructureDefinitionRef> pReferenceTypes, BigDecimal pMinValue,
		BigDecimal pMaxValue, Integer pMaxLength, boolean pFinal, PropertyPattern pPropertyPattern,
		Multimap<String, String> pKeywords) {
		super();
		mScope = pScope;
		mName = pName;
		mLabel = pLabel;
		mIsPrimaryKey = pIsPrimaryKey;
		mPrimaryKeyOrder = pPrimaryKeyOrder;
		mType = pType;
		mValidationScript = pValidationScript;
		mDefaultValue = pDefaultValue;
		mDefaultValueScript = pDefaultValueScript;
		mReferenceTypes = pReferenceTypes == null ? ImmutableSet.of() : ImmutableSet.copyOf(pReferenceTypes);
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;
		mMaxLength = pMaxLength;
		mFinal = pFinal;
		mPropertyPattern = pPropertyPattern;
		mKeywords = pKeywords == null ? ImmutableMultimap.of() : ImmutableMultimap.copyOf(pKeywords);
	}

	/**
	 * Validates that the contents of the PropertyDefinition are valid, and throws an exception if they are not.
	 */
	public void validate() {

		if (mName == null)
			throw new IllegalArgumentException("The PropertyDefinition must have a name.");
		if (sValidNamePattern.matcher(mName).matches() == false)
			throw new IllegalArgumentException(
				"The PropertyDefinition must have a valid name, which can only be the characters 0-9, a-z, A-Z, . and -.");
		if (mType == null)
			throw new IllegalArgumentException("The PropertyDefinition must have a type.");

		/* Verify that any container parent property is not a primary key */

		if (mIsPrimaryKey == true) {

			if (mKeywords.containsEntry(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT) == true)
				throw new IllegalArgumentException("A primary key cannot also be a CONTAINER PARENT");
		}

		/* If it's a CONTAINER PARENT, then it must be a PropertyRef */

		if (mKeywords.containsEntry(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT) == true) {
			if (mType != PropertyType.PropertyRef)
				throw new IllegalArgumentException("Only PropertyRef's are valid types for CONTAINER PARENT");
		}
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#getScope()
	 */
	@Override
	public Scope getScope() {
		return mScope;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.ResolvableWithContainer#getReference(java.lang.Object)
	 */
	@Override
	public PropertyDefinitionRef getReference(StructureDefinition pContainer) {
		return mScope.getToolkit().createPropertyDefinitionRef(mScope, this, pContainer);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#setName(java.lang.String)
	 */
	@Override
	public PropertyDefinition setName(String pValue) {
		return new GenericPropertyDefinition(mScope, pValue, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public TranslatableString getLabel() {
		return mLabel;
	}

	@Override
	public PropertyDefinition setLabel(TranslatableString pValue) {
		return new GenericPropertyDefinition(mScope, mName, pValue, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public boolean isPrimaryKey() {
		return mIsPrimaryKey;
	}

	@Override
	public PropertyDefinition setPrimaryKey(boolean pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, pValue, mPrimaryKeyOrder, mType, mValidationScript,
			mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
			mPropertyPattern, mKeywords);
	}

	@Override
	public PropertyType getType() {
		return mType;
	}

	@Override
	public PropertyDefinition setType(PropertyType pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, pValue,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public Script getValidationScript() {
		return mValidationScript;
	}

	@Override
	public PropertyDefinition setValidationScript(Script pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType, pValue,
			mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
			mPropertyPattern, mKeywords);
	}

	@Override
	public String getDefaultValue() {
		return mDefaultValue;
	}

	@Override
	public PropertyDefinition setDefaultValue(String pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, pValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
			mPropertyPattern, mKeywords);
	}

	@Override
	public Script getDefaultValueScript() {
		return mDefaultValueScript;
	}

	@Nonnull
	@Override
	public PropertyDefinition setDefaultValueScript(Script pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, pValue, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
			mPropertyPattern, mKeywords);
	}

	@Override
	public Collection<StructureDefinitionRef> getReferenceTypes() {
		return mReferenceTypes;
	}

	@Override
	public PropertyDefinition addReferenceType(StructureDefinitionRef pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript,
			ImmutableSet.<StructureDefinitionRef> builder().addAll(mReferenceTypes).add(pValue).build(), mMinValue,
			mMaxValue, mMaxLength, mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public PropertyDefinition removeReferenceType(StructureDefinitionRef pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript,
			Sets.filter(mReferenceTypes, Predicates.not(Predicates.equalTo(pValue))), mMinValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public BigDecimal getMinValue() {
		return mMinValue;
	}

	@Override
	public PropertyDefinition setMinValue(BigDecimal pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, pValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public BigDecimal getMaxValue() {
		return mMaxValue;
	}

	@Override
	public PropertyDefinition setMaxValue(BigDecimal pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, pValue, mMaxLength,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public Integer getMaxLength() {
		return mMaxLength;
	}

	@Override
	public PropertyDefinition setMaxLength(Integer pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, pValue,
			mFinal, mPropertyPattern, mKeywords);
	}

	@Override
	public boolean isFinal() {
		return mFinal;
	}

	@Override
	public PropertyDefinition setFinal(boolean pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			pValue, mPropertyPattern, mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#getPropertyPattern()
	 */
	@Override
	public PropertyPattern getPropertyPattern() {
		return mPropertyPattern;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#setPropertyPattern(com.diamondq.common.model.interfaces.PropertyPattern)
	 */
	@Override
	public PropertyDefinition setPropertyPattern(PropertyPattern pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			mFinal, pValue, mKeywords);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#getKeywords()
	 */
	@Override
	public Multimap<String, String> getKeywords() {
		return mKeywords;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.StructureDefinition#addKeyword(java.lang.String, java.lang.String)
	 */
	@Override
	public PropertyDefinition addKeyword(String pKey, String pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern,
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
	public PropertyDefinition removeKeyword(String pKey, String pValue) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
			mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength,
			mFinal, mPropertyPattern, Multimaps.filterEntries(mKeywords,
				Predicates.<Entry<String, String>> not((e) -> pKey.equals(e.getKey()) && pValue.equals(e.getValue()))));
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#getPrimaryKeyOrder()
	 */
	@Override
	public int getPrimaryKeyOrder() {
		return mPrimaryKeyOrder;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.PropertyDefinition#setPrimaryKeyOrder(int)
	 */
	@Override
	public PropertyDefinition setPrimaryKeyOrder(int pOrder) {
		return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, pOrder, mType, mValidationScript,
			mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
			mPropertyPattern, mKeywords);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(mScope, mDefaultValue, mDefaultValueScript, mFinal, mIsPrimaryKey, mPrimaryKeyOrder,
			mPropertyPattern, mLabel, mMaxValue, mMinValue, mName, mReferenceTypes, mType, mValidationScript,
			mKeywords);
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
		GenericPropertyDefinition other = (GenericPropertyDefinition) pObj;
		return Objects.equals(mScope, other.mScope) && Objects.equals(mDefaultValue, other.mDefaultValue)
			&& Objects.equals(mDefaultValueScript, other.mDefaultValueScript) && Objects.equals(mFinal, other.mFinal)
			&& Objects.equals(mIsPrimaryKey, other.mIsPrimaryKey)
			&& Objects.equals(mPrimaryKeyOrder, other.mPrimaryKeyOrder)
			&& Objects.equals(mPropertyPattern, other.mPropertyPattern) && Objects.equals(mLabel, other.mLabel)
			&& Objects.equals(mMaxValue, other.mMaxValue) && Objects.equals(mMinValue, other.mMinValue)
			&& Objects.equals(mName, other.mName) && Objects.equals(mReferenceTypes, other.mReferenceTypes)
			&& Objects.equals(mType, other.mType) && Objects.equals(mValidationScript, other.mValidationScript)
			&& Objects.equals(mKeywords, other.mKeywords);
	}
}

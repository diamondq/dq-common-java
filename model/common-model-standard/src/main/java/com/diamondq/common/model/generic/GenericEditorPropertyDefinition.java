package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorDisplayType;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EmbedEditorDirection;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.Script;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericEditorPropertyDefinition extends GenericEditorComponentDefinition<EditorPropertyDefinition>
	implements EditorPropertyDefinition {

	private final String							mName;

	private final EditorDisplayType					mDisplayType;

	private final PropertyDefinitionRef				mEnabledIfProperty;

	private final Set<String>						mEnabledIfValueEquals;

	private final boolean							mIsMandatory;

	private final TranslatableString				mMandatoryReason;

	private final Script							mValueMapScript;

	private final Map<String, TranslatableString>	mSimpleValueMap;

	private final PropertyDefinitionRef				mValueMapProperty;

	private final List<PropertyDefinitionRef>		mTableDisplayProperties;

	private final EmbedEditorDirection				mEmbedTableRowEditor;

	private final PropertyDefinitionRef				mDisplayRefImage;

	private final Script							mCustomScript;

	public GenericEditorPropertyDefinition(TranslatableString pLabel, int pColumn, int pColumnSpan, int pOrder,
		PropertyDefinitionRef pVisibleIfProperty, Set<String> pVisibleIfValueEquals, String pName,
		EditorDisplayType pDisplayType, PropertyDefinitionRef pEnabledIfProperty, Set<String> pEnabledIfValueEquals,
		boolean pIsMandatory, TranslatableString pMandatoryReason, Script pValueMapScript,
		Map<String, TranslatableString> pSimpleValueMap, PropertyDefinitionRef pValueMapProperty,
		List<PropertyDefinitionRef> pTableDisplayProperties, EmbedEditorDirection pEmbedTableRowEditor,
		PropertyDefinitionRef pDisplayRefImage, Script pCustomScript) {
		super(pLabel, pColumn, pColumnSpan, pOrder, pVisibleIfProperty, pVisibleIfValueEquals);
		mName = pName;
		mDisplayType = pDisplayType;
		mEnabledIfProperty = pEnabledIfProperty;
		mEnabledIfValueEquals = pEnabledIfValueEquals == null ? null : ImmutableSet.copyOf(pEnabledIfValueEquals);
		mIsMandatory = pIsMandatory;
		mMandatoryReason = pMandatoryReason;
		mValueMapScript = pValueMapScript;
		mSimpleValueMap = (pSimpleValueMap == null || pSimpleValueMap.isEmpty() == true) ? null
			: ImmutableMap.copyOf(pSimpleValueMap);
		mValueMapProperty = pValueMapProperty;
		mTableDisplayProperties =
			pTableDisplayProperties == null ? null : ImmutableList.copyOf(pTableDisplayProperties);
		mEmbedTableRowEditor = pEmbedTableRowEditor;
		mDisplayRefImage = pDisplayRefImage;
		mCustomScript = pCustomScript;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setName(java.lang.String)
	 */
	@Override
	public EditorPropertyDefinition setName(String pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, pValue, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties,
			mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getDisplayType()
	 */
	@Override
	public EditorDisplayType getDisplayType() {
		return mDisplayType;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setDisplayType(com.diamondq.common.model.interfaces.EditorDisplayType)
	 */
	@Override
	public EditorPropertyDefinition setDisplayType(EditorDisplayType pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, pValue, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties,
			mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getEnabledIfProperty()
	 */
	@Override
	public PropertyDefinitionRef getEnabledIfProperty() {
		return mEnabledIfProperty;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setEnabledIfProperty(com.diamondq.common.model.interfaces.PropertyDefinitionRef)
	 */
	@Override
	public EditorPropertyDefinition setEnabledIfProperty(PropertyDefinitionRef pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, pValue, mEnabledIfValueEquals, mIsMandatory, mMandatoryReason,
			mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties, mEmbedTableRowEditor,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getEnabledIfValueEquals()
	 */
	@Override
	public Collection<String> getEnabledIfValueEquals() {
		return mEnabledIfValueEquals;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#addEnabledIfValueEquals(java.lang.String)
	 */
	@Override
	public EditorPropertyDefinition addEnabledIfValueEquals(String pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty,
			ImmutableSet.<String> builder()
				.addAll(mEnabledIfValueEquals == null ? Collections.emptySet() : mEnabledIfValueEquals).add(pValue)
				.build(),
			mIsMandatory, mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty,
			mTableDisplayProperties, mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#removeEnabledIfValueEquals(java.lang.String)
	 */
	@Override
	public EditorPropertyDefinition removeEnabledIfValueEquals(String pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty,
			Sets.filter(mEnabledIfValueEquals == null ? Collections.emptySet() : mEnabledIfValueEquals,
				Predicates.not(Predicates.equalTo(pValue))),
			mIsMandatory, mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty,
			mTableDisplayProperties, mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#isMandatory()
	 */
	@Override
	public boolean isMandatory() {
		return mIsMandatory;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setMandatory(boolean)
	 */
	@Override
	public EditorPropertyDefinition setMandatory(boolean pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, pValue,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties,
			mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getMandatoryReason()
	 */
	@Override
	public TranslatableString getMandatoryReason() {
		return mMandatoryReason;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setMandatoryReason(com.diamondq.common.model.interfaces.TranslatableString)
	 */
	@Override
	public EditorPropertyDefinition setMandatoryReason(TranslatableString pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory, pValue,
			mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties, mEmbedTableRowEditor,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getValueMapScript()
	 */
	@Override
	public Script getValueMapScript() {
		return mValueMapScript;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setValueMapScript(com.diamondq.common.model.interfaces.Script)
	 */
	@Override
	public EditorPropertyDefinition setValueMapScript(Script pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, pValue, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties, mEmbedTableRowEditor,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getSimpleValueMap()
	 */
	@Override
	public Map<String, TranslatableString> getSimpleValueMap() {
		return mSimpleValueMap;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#putSimpleValueMapEntry(java.lang.String,
	 *      com.diamondq.common.model.interfaces.TranslatableString)
	 */
	@Override
	public EditorPropertyDefinition putSimpleValueMapEntry(String pKey, TranslatableString pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript,
			ImmutableMap.<String, TranslatableString> builder()
				.putAll(Maps.filterKeys(mSimpleValueMap == null ? Collections.emptyMap() : mSimpleValueMap,
					Predicates.not(Predicates.equalTo(pKey))))
				.put(pKey, pValue).build(),
			mValueMapProperty, mTableDisplayProperties, mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#removeSimpleValueMapEntry(java.lang.String)
	 */
	@Override
	public EditorPropertyDefinition removeSimpleValueMapEntry(String pKey) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript,
			Maps.filterKeys(mSimpleValueMap == null ? Collections.emptyMap() : mSimpleValueMap,
				Predicates.not(Predicates.equalTo(pKey))),
			mValueMapProperty, mTableDisplayProperties, mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getValueMapProperty()
	 */
	@Override
	public PropertyDefinitionRef getValueMapProperty() {
		return mValueMapProperty;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setValueMapProperty(com.diamondq.common.model.interfaces.PropertyDefinitionRef)
	 */
	@Override
	public EditorPropertyDefinition setValueMapProperty(PropertyDefinitionRef pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, pValue, mTableDisplayProperties, mEmbedTableRowEditor,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getTableDisplayProperties()
	 */
	@Override
	public List<PropertyDefinitionRef> getTableDisplayProperties() {
		return mTableDisplayProperties;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#addTableDisplayProperty(int,
	 *      com.diamondq.common.model.interfaces.PropertyDefinitionRef)
	 */
	@Override
	public EditorPropertyDefinition addTableDisplayProperty(int pIndex, PropertyDefinitionRef pValue) {
		List<PropertyDefinitionRef> newTable =
			Lists.newArrayList(mTableDisplayProperties == null ? Collections.emptyList() : mTableDisplayProperties);
		newTable.add(pIndex, pValue);
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, newTable, mEmbedTableRowEditor,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#removeTableDisplayProperty(int)
	 */
	@Override
	public EditorPropertyDefinition removeTableDisplayProperty(int pIndex) {
		List<PropertyDefinitionRef> newTable =
			Lists.newArrayList(mTableDisplayProperties == null ? Collections.emptyList() : mTableDisplayProperties);
		newTable.remove(pIndex);
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, newTable, mEmbedTableRowEditor,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getEmbedTableRowEditor()
	 */
	@Override
	public EmbedEditorDirection getEmbedTableRowEditor() {
		return mEmbedTableRowEditor;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setEmbedTableRowEditor(com.diamondq.common.model.interfaces.EmbedEditorDirection)
	 */
	@Override
	public EditorPropertyDefinition setEmbedTableRowEditor(EmbedEditorDirection pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties, pValue,
			mDisplayRefImage, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getDisplayRefImage()
	 */
	@Override
	public PropertyDefinitionRef getDisplayRefImage() {
		return mDisplayRefImage;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setDisplayRefImage(com.diamondq.common.model.interfaces.PropertyDefinitionRef)
	 */
	@Override
	public EditorPropertyDefinition setDisplayRefImage(PropertyDefinitionRef pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties,
			mEmbedTableRowEditor, pValue, mCustomScript);
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#getCustomScript()
	 */
	@Override
	public Script getCustomScript() {
		return mCustomScript;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorPropertyDefinition#setCustomScript(com.diamondq.common.model.interfaces.Script)
	 */
	@Override
	public EditorPropertyDefinition setCustomScript(Script pValue) {
		return new GenericEditorPropertyDefinition(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
			mVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties,
			mEmbedTableRowEditor, mDisplayRefImage, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.GenericEditorComponentDefinition#constructNew(com.diamondq.common.model.interfaces.TranslatableString,
	 *      int, int, int, com.diamondq.common.model.interfaces.PropertyDefinitionRef, java.util.Set)
	 */
	@Override
	protected EditorPropertyDefinition constructNew(TranslatableString pLabel, int pColumn, int pColumnSpan, int pOrder,
		PropertyDefinitionRef pVisibleIfProperty, Set<String> pVisibleIfValueEquals) {
		return new GenericEditorPropertyDefinition(pLabel, pColumn, pColumnSpan, pOrder, pVisibleIfProperty,
			pVisibleIfValueEquals, mName, mDisplayType, mEnabledIfProperty, mEnabledIfValueEquals, mIsMandatory,
			mMandatoryReason, mValueMapScript, mSimpleValueMap, mValueMapProperty, mTableDisplayProperties,
			mEmbedTableRowEditor, mDisplayRefImage, mCustomScript);
	}

}

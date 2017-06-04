package com.diamondq.common.model.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EditorPropertyDefinition extends EditorComponentDefinition<EditorPropertyDefinition> {

	/* name */

	@Nullable
	public String getName();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setName(@Nonnull String pName);

	/* displayType */

	public EditorDisplayType getDisplayType();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setDisplayType(EditorDisplayType pValue);

	/* enabledIfProperty */

	@Nullable
	public PropertyDefinitionRef getEnabledIfProperty();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setEnabledIfProperty(@Nullable PropertyDefinitionRef pValue);

	/* enabledIfValueEquals */

	@Nonnull
	public Collection<String> getEnabledIfValueEquals();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition addEnabledIfValueEquals(@Nonnull String pValue);

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition removeEnabledIfValueEquals(@Nonnull String pValue);

	/* mandatory */

	public boolean isMandatory();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setMandatory(boolean pValue);

	/* mandatoryReason */

	@Nullable
	public TranslatableString getMandatoryReason();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setMandatoryReason(@Nullable TranslatableString pValue);

	/* valueMapScript */

	/**
	 * The ValueMap Script is responsible for mapping between a 'key' and a 'display value'. The script must return a
	 * List of <Key,TranslatableString> pairs.
	 * 
	 * @return the Script or null
	 */
	@Nullable
	public Script getValueMapScript();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setValueMapScript(@Nullable Script pValue);

	/* simpleValueMap */

	/**
	 * The Simple ValueMap is a Map of Key,TranslatableString pairs used for mapping between a 'key' and a 'display
	 * value'. If null, then this is not used for mapping.
	 * 
	 * @return the map or null
	 */
	@Nullable
	public Map<String, TranslatableString> getSimpleValueMap();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition putSimpleValueMapEntry(@Nonnull String pKey, @Nonnull TranslatableString pValue);

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition removeSimpleValueMapEntry(@Nonnull String pKey);

	/* valueMapProperty */

	/**
	 * Returns a PropertyDefinitionRef used to find the property within the given StructureDefinition that will contain
	 * the display name for mapping.
	 * 
	 * @return the PropertyDefinitionRef or null
	 */
	@Nullable
	public PropertyDefinitionRef getValueMapProperty();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setValueMapProperty(@Nullable PropertyDefinitionRef pValue);

	/* tableDisplayProperties */

	@Nullable
	public List<PropertyDefinitionRef> getTableDisplayProperties();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition addTableDisplayProperty(int pIndex, @Nonnull PropertyDefinitionRef pValue);

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition removeTableDisplayProperty(int pIndex);

	/* embedTableRowEditor */

	@Nullable
	public EmbedEditorDirection getEmbedTableRowEditor();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setEmbedTableRowEditor(EmbedEditorDirection pValue);

	/* displayRefImage */

	@Nullable
	public PropertyDefinitionRef getDisplayRefImage();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setDisplayRefImage(@Nullable PropertyDefinitionRef pValue);

	/* customScript */

	@Nullable
	public Script getCustomScript();

	@CheckReturnValue
	@Nonnull
	public EditorPropertyDefinition setCustomScript(@Nullable Script pValue);
}

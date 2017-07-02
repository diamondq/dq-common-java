package com.diamondq.common.model.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;


public interface EditorPropertyDefinition extends EditorComponentDefinition<EditorPropertyDefinition> {

	/* name */

	@Nullable
	public String getName();

	
	
	public EditorPropertyDefinition setName( String pName);

	/* displayType */

	public EditorDisplayType getDisplayType();

	
	
	public EditorPropertyDefinition setDisplayType(EditorDisplayType pValue);

	/* enabledIfProperty */

	@Nullable
	public PropertyDefinitionRef getEnabledIfProperty();

	
	
	public EditorPropertyDefinition setEnabledIfProperty(@Nullable PropertyDefinitionRef pValue);

	/* enabledIfValueEquals */

	
	public Collection<String> getEnabledIfValueEquals();

	
	
	public EditorPropertyDefinition addEnabledIfValueEquals( String pValue);

	
	
	public EditorPropertyDefinition removeEnabledIfValueEquals( String pValue);

	/* mandatory */

	public boolean isMandatory();

	
	
	public EditorPropertyDefinition setMandatory(boolean pValue);

	/* mandatoryReason */

	@Nullable
	public TranslatableString getMandatoryReason();

	
	
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

	
	
	public EditorPropertyDefinition putSimpleValueMapEntry( String pKey,  TranslatableString pValue);

	
	
	public EditorPropertyDefinition removeSimpleValueMapEntry( String pKey);

	/* valueMapProperty */

	/**
	 * Returns a PropertyDefinitionRef used to find the property within the given StructureDefinition that will contain
	 * the display name for mapping.
	 * 
	 * @return the PropertyDefinitionRef or null
	 */
	@Nullable
	public PropertyDefinitionRef getValueMapProperty();

	
	
	public EditorPropertyDefinition setValueMapProperty(@Nullable PropertyDefinitionRef pValue);

	/* tableDisplayProperties */

	@Nullable
	public List<PropertyDefinitionRef> getTableDisplayProperties();

	
	
	public EditorPropertyDefinition addTableDisplayProperty(int pIndex,  PropertyDefinitionRef pValue);

	
	
	public EditorPropertyDefinition removeTableDisplayProperty(int pIndex);

	/* embedTableRowEditor */

	@Nullable
	public EmbedEditorDirection getEmbedTableRowEditor();

	
	
	public EditorPropertyDefinition setEmbedTableRowEditor(EmbedEditorDirection pValue);

	/* displayRefImage */

	@Nullable
	public PropertyDefinitionRef getDisplayRefImage();

	
	
	public EditorPropertyDefinition setDisplayRefImage(@Nullable PropertyDefinitionRef pValue);

	/* customScript */

	@Nullable
	public Script getCustomScript();

	
	
	public EditorPropertyDefinition setCustomScript(@Nullable Script pValue);
}

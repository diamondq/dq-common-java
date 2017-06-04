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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface PersistenceLayer {

	/* StructureDefinitions */

	public StructureDefinition createNewStructureDefinition(Toolkit pToolkit, Scope pScope, String pName);

	public void writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

	public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope);

	public StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName);

	/* Reference */

	public StructureDefinitionRef createStructureDefinitionRef(Toolkit pToolkit, Scope pScope,
		StructureDefinition pResolvable);

	public PropertyDefinitionRef createPropertyDefinitionRef(Toolkit pToolkit, Scope pScope,
		PropertyDefinition pResolvable, StructureDefinition pContaining);

	public StructureRef createStructureRef(Toolkit pToolkit, Scope pScope, Structure pResolvable);

	public String createStructureRefStr(Toolkit pToolkit, Scope pScope, Structure pResolvable);

	public <T> PropertyRef<T> createPropertyRef(Toolkit pToolkit, Scope pScope, Property<T> pResolvable,
		Structure pContaining);

	/* PropertyDefinition */

	public PropertyDefinition createNewPropertyDefinition(Toolkit pToolkit, Scope pScope);

	public String collapsePrimaryKeys(Toolkit pToolkit, Scope pScope, List<Object> pNames);

	/* Structure */

	public Structure createNewStructure(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition);

	public void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure);

	public Structure lookupStructureBySerializedRef(Toolkit pGenericToolkit, Scope pScope, String pSerializedRef);

	public void deleteStructure(Toolkit pToolkit, Scope pScope, Structure pValue);

	/* Property */

	public <T> Property<T> createNewProperty(Toolkit pToolkit, Scope pScope, PropertyDefinition pPropertyDefinition,
		boolean pIsValueSet, T pValue);

	/* TranslatableString */

	public TranslatableString createNewTranslatableString(Toolkit pToolkit, Scope pScope, String pKey);

	/* EditorGroupDefinition */

	public EditorGroupDefinition createNewEditorGroupDefinition(Toolkit pToolkit, Scope pScope);

	/* EditorPropertyDefinition */

	public EditorPropertyDefinition createNewEditorPropertyDefinition(Toolkit pToolkit, Scope pScope);

	/* EditorStructureDefinition */

	public EditorStructureDefinition createNewEditorStructureDefinition(Toolkit pToolkit, Scope pScope, String pName,
		StructureDefinitionRef pRef);

	public void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		EditorStructureDefinition pEditorStructureDefinition);

	public void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue);

	public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef);

	public StructureRef createStructureRefFromSerialized(Toolkit pToolkit, Scope pScope, String pValue);

	public <T> PropertyRef<T> createPropertyRefFromSerialized(Toolkit pGenericToolkit, Scope pScope, String pValue);

	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef);

	public String lookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey);

	public void setGlobalDefaultLocale(Toolkit pToolkit, Scope pScope, Locale pLocale);

	public void setThreadLocale(Toolkit pToolkit, Scope pScope, Locale pLocale);

	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope);

	public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue);

	public void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey);

	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope);

	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale);

	public EditorStructureDefinition createDynamicEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinition pStructureDefinition);

	public List<Structure> lookupStructuresByQuery(Toolkit pToolkit, Scope pScope,
		StructureDefinition pStructureDefinition, QueryBuilder pBuilder, Map<String, Object> pParamValues);

	public QueryBuilder createNewQueryBuilder(Toolkit pToolkit, Scope pScope);

	public StructureRef createStructureRefFromParts(Toolkit pToolkit, Scope pScope, Structure pStructure,
		String pPropName, StructureDefinition pDef, List<Object> pPrimaryKeys);

}

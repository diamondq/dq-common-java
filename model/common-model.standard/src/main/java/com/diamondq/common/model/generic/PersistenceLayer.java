package com.diamondq.common.model.generic;

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
import com.diamondq.common.model.interfaces.TranslatableString;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public interface PersistenceLayer {

	/* StructureDefinitions */

	public StructureDefinition createNewStructureDefinition(Toolkit pToolkit, Scope pScope, String pName,
		int pRevision);

	public StructureDefinition createNewTombstoneStructureDefinition(Toolkit pToolkit, Scope pScope, String pName);

	public void writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

	public void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope);

	public @Nullable StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName);

	public @Nullable StructureDefinition lookupStructureDefinitionByNameAndRevision(Toolkit pToolkit, Scope pScope,
		String pName, @Nullable Integer pRevision);

	/* Reference */

	public StructureDefinitionRef createStructureDefinitionRef(Toolkit pToolkit, Scope pScope,
		StructureDefinition pResolvable, boolean pWildcard);

	public PropertyDefinitionRef createPropertyDefinitionRef(Toolkit pToolkit, Scope pScope,
		PropertyDefinition pResolvable, StructureDefinition pContaining);

	public StructureRef createStructureRef(Toolkit pToolkit, Scope pScope, Structure pResolvable);

	public String createStructureRefStr(Toolkit pToolkit, Scope pScope, Structure pResolvable);

	public <@Nullable T> PropertyRef<T> createPropertyRef(Toolkit pToolkit, Scope pScope,
		@Nullable Property<T> pResolvable, Structure pContaining);

	/* PropertyDefinition */

	public PropertyDefinition createNewPropertyDefinition(Toolkit pToolkit, Scope pScope, String pName,
		PropertyType pType);

	public String collapsePrimaryKeys(Toolkit pToolkit, Scope pScope, List<@Nullable Object> pNames);

	/* Structure */

	public Structure createNewStructure(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition);

	public Structure createNewTombstoneStructure(Toolkit pToolkit, Scope pScope, Structure pOldStructure);

	public void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure);

	public boolean writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure,
		@Nullable Structure pOldStructure);

	public @Nullable Structure lookupStructureBySerializedRef(Toolkit pGenericToolkit, Scope pScope,
		String pSerializedRef);

	public @Nullable Structure lookupStructureByPrimaryKeys(GenericToolkit pGenericToolkit, Scope pScope,
		StructureDefinition pStructureDef, @Nullable Object[] pPrimaryKeys);

	public boolean deleteStructure(Toolkit pToolkit, Scope pScope, Structure pValue);

	/* Property */

	public <@Nullable T> Property<T> createNewProperty(Toolkit pToolkit, Scope pScope,
		PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue);

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

	public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Toolkit pGenericToolkit, Scope pScope,
		String pValue);

	public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinitionRef pRef, @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef);

	public @Nullable String lookupResourceString(Toolkit pToolkit, Scope pScope, @Nullable Locale pLocale, String pKey);

	public void setGlobalDefaultLocale(Toolkit pToolkit, @Nullable Scope pScope, Locale pLocale);

	public void setThreadLocale(Toolkit pToolkit, @Nullable Scope pScope, @Nullable Locale pLocale);

	public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope);

	public void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue);

	public void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey);

	public String createNewTombstoneResourceString(Toolkit pToolkit, Scope pScope);

	public boolean isTombstoneResourceString(Toolkit pToolkit, Scope pScope, String pValue);

	public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope);

	public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale);

	public EditorStructureDefinition createDynamicEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinition pStructureDefinition);

	public List<Structure> lookupStructuresByQuery(Toolkit pToolkit, Scope pScope,
		StructureDefinition pStructureDefinition, QueryBuilder pBuilder, @Nullable Map<String, Object> pParamValues);

	public QueryBuilder createNewQueryBuilder(Toolkit pToolkit, Scope pScope);

	public StructureRef createStructureRefFromParts(Toolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
		@Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys);

	public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized);

	public BiFunction<Structure, Structure, Structure> createStandardMigration(Toolkit pToolkit, Scope pScope,
		StandardMigrations pMigrationType, @NonNull Object @Nullable [] pParams);

	public void addMigration(Toolkit pToolkit, Scope pScope, String pStructureDefinitionName, int pFromRevision,
		int pToRevision, BiFunction<Structure, Structure, Structure> pMigrationFunction);

	public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
		Toolkit pToolkit, Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision);

	public @Nullable Integer lookupLatestStructureDefinitionRevision(Toolkit pToolkit, Scope pScope, String pDefName);

}

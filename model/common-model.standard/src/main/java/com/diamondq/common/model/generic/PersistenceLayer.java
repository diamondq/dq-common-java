package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorGroupDefinition;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.ModelQuery;
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
import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public interface PersistenceLayer {

  /* StructureDefinitions */

  StructureDefinition createNewStructureDefinition(Toolkit pToolkit, Scope pScope, String pName, int pRevision);

  StructureDefinition populateStructureDefinition(Toolkit pToolkit, Scope pScope, byte[] pBytes);

  StructureDefinition createNewTombstoneStructureDefinition(Toolkit pToolkit, Scope pScope, String pName);

  StructureDefinition writeStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

  /**
   * Only called by the Combined Persistence Layer to enable the StructureDefinition on layers that don't persist the
   * structure.
   *
   * @param pToolkit the toolkit
   * @param pScope the scope
   * @param pValue the value
   */
  void enableStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

  void deleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue);

  Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Toolkit pToolkit, Scope pScope);

  @Nullable
  StructureDefinition lookupStructureDefinitionByName(Toolkit pToolkit, Scope pScope, String pName);

  @Nullable
  StructureDefinition lookupStructureDefinitionByNameAndRevision(Toolkit pToolkit, Scope pScope, String pName,
    @Nullable Integer pRevision);

  /* Reference */

  StructureDefinitionRef createStructureDefinitionRef(Toolkit pToolkit, Scope pScope, StructureDefinition pResolvable,
    boolean pWildcard);

  PropertyDefinitionRef createPropertyDefinitionRef(Toolkit pToolkit, Scope pScope, PropertyDefinition pResolvable,
    StructureDefinition pContaining);

  StructureRef createStructureRef(Toolkit pToolkit, Scope pScope, Structure pResolvable);

  String createStructureRefStr(Toolkit pToolkit, Scope pScope, Structure pResolvable);

  <T extends @Nullable Object> PropertyRef<T> createPropertyRef(Toolkit pToolkit, Scope pScope,
    @Nullable Property<T> pResolvable, Structure pContaining);

  /* PropertyDefinition */

  PropertyDefinition createNewPropertyDefinition(Toolkit pToolkit, Scope pScope, String pName, PropertyType pType);

  String collapsePrimaryKeys(Toolkit pToolkit, Scope pScope, List<@Nullable Object> pNames);

  /* Structure */

  Structure createNewStructure(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition);

  Structure createNewTombstoneStructure(Toolkit pToolkit, Scope pScope, Structure pOldStructure);

  void writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure);

  boolean writeStructure(Toolkit pToolkit, Scope pScope, Structure pStructure, @Nullable Structure pOldStructure);

  @Nullable
  Structure lookupStructureBySerializedRef(Toolkit pToolkit, Scope pScope, String pSerializedRef);

  @Nullable
  Structure lookupStructureByPrimaryKeys(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDef,
    @Nullable Object[] pPrimaryKeys);

  boolean deleteStructure(Toolkit pToolkit, Scope pScope, Structure pValue);

  /* Property */

  <T extends @Nullable Object> Property<T> createNewProperty(Toolkit pToolkit, Scope pScope,
    PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue);

  /* TranslatableString */

  TranslatableString createNewTranslatableString(Toolkit pToolkit, Scope pScope, String pKey);

  /* EditorGroupDefinition */

  EditorGroupDefinition createNewEditorGroupDefinition(Toolkit pToolkit, Scope pScope);

  /* EditorPropertyDefinition */

  EditorPropertyDefinition createNewEditorPropertyDefinition(Toolkit pToolkit, Scope pScope);

  /* EditorStructureDefinition */

  EditorStructureDefinition createNewEditorStructureDefinition(Toolkit pToolkit, Scope pScope, String pName,
    StructureDefinitionRef pRef);

  void writeEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pEditorStructureDefinition);

  void deleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope, EditorStructureDefinition pValue);

  List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Toolkit pToolkit, Scope pScope,
    StructureDefinitionRef pRef);

  StructureRef createStructureRefFromSerialized(Toolkit pToolkit, Scope pScope, String pValue);

  <T extends @Nullable Object> PropertyRef<T> createPropertyRefFromSerialized(Toolkit pToolkit, Scope pScope,
    String pValue);

  Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope, StructureDefinitionRef pRef,
    @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef);

  @Nullable
  String lookupResourceString(Toolkit pToolkit, Scope pScope, @Nullable Locale pLocale, String pKey);

  void setGlobalDefaultLocale(Toolkit pToolkit, @Nullable Scope pScope, Locale pLocale);

  void setThreadLocale(Toolkit pToolkit, @Nullable Scope pScope, @Nullable Locale pLocale);

  boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope);

  void writeResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey, String pValue);

  void deleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey);

  String createNewTombstoneResourceString(Toolkit pToolkit, Scope pScope);

  boolean isTombstoneResourceString(Toolkit pToolkit, Scope pScope, String pValue);

  Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope);

  Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale);

  EditorStructureDefinition createDynamicEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDefinition);

  List<Structure> lookupStructuresByQuery(Toolkit pToolkit, Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues);

  QueryBuilder createNewQueryBuilder(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition,
    String pQueryName);

  ModelQuery writeQueryBuilder(Toolkit pToolkit, Scope pScope, QueryBuilder pQueryBuilder);

  StructureRef createStructureRefFromParts(Toolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys);

  StructureDefinitionRef createStructureDefinitionRefFromSerialized(Toolkit pToolkit, Scope pScope, String pSerialized);

  BiFunction<Structure, Structure, Structure> createStandardMigration(Toolkit pToolkit, Scope pScope,
    StandardMigrations pMigrationType, Object @Nullable [] pParams);

  void addMigration(Toolkit pToolkit, Scope pScope, String pStructureDefinitionName, int pFromRevision, int pToRevision,
    BiFunction<Structure, Structure, Structure> pMigrationFunction);

  @Nullable
  List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(Toolkit pToolkit,
    Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision);

  @Nullable
  Integer lookupLatestStructureDefinitionRevision(Toolkit pToolkit, Scope pScope, String pDefName);

  AsyncPersistenceLayer getAsyncPersistenceLayer();

  boolean inferStructureDefinitions(GenericToolkit pGenericToolkit, Scope pScope);

  void clearStructures(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDef);

  int countByQuery(Toolkit pToolkit, Scope pScope, ModelQuery pQuery, @Nullable Map<String, Object> pParamValues);

}

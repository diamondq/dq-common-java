package com.diamondq.common.model.generic;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.model.interfaces.AsyncToolkit;
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
import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface AsyncPersistenceLayer {

  /* StructureDefinitions */

  StructureDefinition createNewStructureDefinition(AsyncToolkit pToolkit, Scope pScope, String pName, int pRevision);

  StructureDefinition createNewTombstoneStructureDefinition(AsyncToolkit pToolkit, Scope pScope, String pName);

  ContextExtendedCompletionStage<StructureDefinition> writeStructureDefinition(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pValue);

  /**
   * Only called by the CombinedPersistenceLayer to enable the StructureDefinition on layers that don't persist the
   * structure.
   *
   * @param pToolkit the toolkit
   * @param pScope the scope
   * @param pValue the value
   */
  void enableStructureDefinition(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pValue);

  ContextExtendedCompletionStage<@Nullable Void> deleteStructureDefinition(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pValue);

  ContextExtendedCompletionStage<Collection<StructureDefinitionRef>> getAllStructureDefinitionRefs(
    AsyncToolkit pToolkit, Scope pScope);

  ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByName(AsyncToolkit pToolkit,
    Scope pScope, String pName);

  ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByNameAndRevision(
    AsyncToolkit pToolkit, Scope pScope, String pName, @Nullable Integer pRevision);

  /* Reference */

  StructureDefinitionRef createStructureDefinitionRef(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pResolvable, boolean pWildcard);

  PropertyDefinitionRef createPropertyDefinitionRef(AsyncToolkit pToolkit, Scope pScope, PropertyDefinition pResolvable,
    StructureDefinition pContaining);

  StructureRef createStructureRef(AsyncToolkit pToolkit, Scope pScope, Structure pResolvable);

  String createStructureRefStr(AsyncToolkit pToolkit, Scope pScope, Structure pResolvable);

  <T extends @Nullable Object> PropertyRef<T> createPropertyRef(AsyncToolkit pToolkit, Scope pScope,
    @Nullable Property<T> pResolvable, Structure pContaining);

  /* PropertyDefinition */

  PropertyDefinition createNewPropertyDefinition(AsyncToolkit pToolkit, Scope pScope, String pName, PropertyType pType);

  String collapsePrimaryKeys(AsyncToolkit pToolkit, Scope pScope, List<@Nullable Object> pNames);

  /* Structure */

  Structure createNewStructure(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition);

  Structure createNewTombstoneStructure(AsyncToolkit pToolkit, Scope pScope, Structure pOldStructure);

  ContextExtendedCompletionStage<@Nullable Void> writeStructure(AsyncToolkit pToolkit, Scope pScope,
    Structure pStructure);

  ContextExtendedCompletionStage<Boolean> writeStructure(AsyncToolkit pToolkit, Scope pScope, Structure pStructure,
    @Nullable Structure pOldStructure);

  ContextExtendedCompletionStage<@Nullable Structure> lookupStructureBySerializedRef(AsyncToolkit pToolkit,
    Scope pScope, String pSerializedRef);

  ContextExtendedCompletionStage<@Nullable Structure> lookupStructureByPrimaryKeys(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDef, @Nullable Object[] pPrimaryKeys);

  ContextExtendedCompletionStage<Boolean> deleteStructure(AsyncToolkit pToolkit, Scope pScope, Structure pValue);

  /* Property */

  <T extends @Nullable Object> Property<T> createNewProperty(AsyncToolkit pToolkit, Scope pScope,
    PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue);

  StructureRef createStructureRefFromSerialized(AsyncToolkit pToolkit, Scope pScope, String pValue);

  <T extends @Nullable Object> PropertyRef<T> createPropertyRefFromSerialized(AsyncToolkit pToolkit, Scope pScope,
    String pValue);

  ContextExtendedCompletionStage<Collection<Structure>> getAllStructuresByDefinition(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinitionRef pRef, @Nullable String pParentKey,
    @Nullable PropertyDefinition pParentPropertyDef);

  ContextExtendedCompletionStage<List<Structure>> lookupStructuresByQuery(AsyncToolkit pToolkit, Scope pScope,
    ModelQuery pQuery, @Nullable Map<String, Object> pParamValues);

  QueryBuilder createNewQueryBuilder(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition,
    String pQueryName);

  ContextExtendedCompletionStage<ModelQuery> writeQueryBuilder(AsyncToolkit pToolkit, Scope pScope,
    QueryBuilder pQueryBuilder);

  StructureRef createStructureRefFromParts(AsyncToolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys);

  StructureDefinitionRef createStructureDefinitionRefFromSerialized(AsyncToolkit pToolkit, Scope pScope,
    String pSerialized);

  BiFunction<Structure, Structure, Structure> createStandardMigration(AsyncToolkit pToolkit, Scope pScope,
    StandardMigrations pMigrationType, Object @Nullable [] pParams);

  void addMigration(AsyncToolkit pToolkit, Scope pScope, String pStructureDefinitionName, int pFromRevision,
    int pToRevision, BiFunction<Structure, Structure, Structure> pMigrationFunction);

  @Nullable
  List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(AsyncToolkit pToolkit,
    Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision);

  ContextExtendedCompletionStage<@Nullable Integer> lookupLatestStructureDefinitionRevision(AsyncToolkit pToolkit,
    Scope pScope, String pDefName);

  ContextExtendedCompletionStage<@Nullable Void> clearStructures(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDef);

  ContextExtendedCompletionStage<Integer> countByQuery(AsyncToolkit pToolkit, Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues);

}

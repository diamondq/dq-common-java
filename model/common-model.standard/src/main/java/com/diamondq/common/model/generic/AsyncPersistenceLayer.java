package com.diamondq.common.model.generic;

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
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public interface AsyncPersistenceLayer {

  /* StructureDefinitions */

  public StructureDefinition createNewStructureDefinition(AsyncToolkit pToolkit, Scope pScope, String pName,
    int pRevision);

  public StructureDefinition createNewTombstoneStructureDefinition(AsyncToolkit pToolkit, Scope pScope, String pName);

  public ContextExtendedCompletionStage<StructureDefinition> writeStructureDefinition(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinition pValue);

  /**
   * Only called by the CombinedPeristenceLayer to enable the StructureDefinition on layers that don't persist the
   * structure.
   * 
   * @param pToolkit
   * @param pScope
   * @param pValue
   */
  public void enableStructureDefinition(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pValue);

  public ContextExtendedCompletionStage<@Nullable Void> deleteStructureDefinition(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pValue);

  public ContextExtendedCompletionStage<Collection<StructureDefinitionRef>> getAllStructureDefinitionRefs(
    AsyncToolkit pToolkit, Scope pScope);

  public ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByName(
    AsyncToolkit pToolkit, Scope pScope, String pName);

  public ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByNameAndRevision(
    AsyncToolkit pToolkit, Scope pScope, String pName, @Nullable Integer pRevision);

  /* Reference */

  public StructureDefinitionRef createStructureDefinitionRef(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pResolvable, boolean pWildcard);

  public PropertyDefinitionRef createPropertyDefinitionRef(AsyncToolkit pToolkit, Scope pScope,
    PropertyDefinition pResolvable, StructureDefinition pContaining);

  public StructureRef createStructureRef(AsyncToolkit pToolkit, Scope pScope, Structure pResolvable);

  public String createStructureRefStr(AsyncToolkit pToolkit, Scope pScope, Structure pResolvable);

  public <@Nullable T> PropertyRef<T> createPropertyRef(AsyncToolkit pToolkit, Scope pScope,
    @Nullable Property<T> pResolvable, Structure pContaining);

  /* PropertyDefinition */

  public PropertyDefinition createNewPropertyDefinition(AsyncToolkit pToolkit, Scope pScope, String pName,
    PropertyType pType);

  public String collapsePrimaryKeys(AsyncToolkit pToolkit, Scope pScope, List<@Nullable Object> pNames);

  /* Structure */

  public Structure createNewStructure(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition);

  public Structure createNewTombstoneStructure(AsyncToolkit pToolkit, Scope pScope, Structure pOldStructure);

  public ContextExtendedCompletionStage<@Nullable Void> writeStructure(AsyncToolkit pToolkit, Scope pScope,
    Structure pStructure);

  public ContextExtendedCompletionStage<Boolean> writeStructure(AsyncToolkit pToolkit, Scope pScope,
    Structure pStructure, @Nullable Structure pOldStructure);

  public ContextExtendedCompletionStage<@Nullable Structure> lookupStructureBySerializedRef(AsyncToolkit pToolkit,
    Scope pScope, String pSerializedRef);

  public ContextExtendedCompletionStage<@Nullable Structure> lookupStructureByPrimaryKeys(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinition pStructureDef, @Nullable Object[] pPrimaryKeys);

  public ContextExtendedCompletionStage<Boolean> deleteStructure(AsyncToolkit pToolkit, Scope pScope, Structure pValue);

  /* Property */

  public <@Nullable T> Property<T> createNewProperty(AsyncToolkit pToolkit, Scope pScope,
    PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue);

  public StructureRef createStructureRefFromSerialized(AsyncToolkit pToolkit, Scope pScope, String pValue);

  public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(AsyncToolkit pToolkit, Scope pScope,
    String pValue);

  public ContextExtendedCompletionStage<Collection<Structure>> getAllStructuresByDefinition(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinitionRef pRef, @Nullable String pParentKey,
    @Nullable PropertyDefinition pParentPropertyDef);

  public ContextExtendedCompletionStage<List<Structure>> lookupStructuresByQuery(AsyncToolkit pToolkit, Scope pScope,
    ModelQuery pQuery, @Nullable Map<String, Object> pParamValues);

  public QueryBuilder createNewQueryBuilder(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDefinition, String pQueryName);

  public ContextExtendedCompletionStage<ModelQuery> writeQueryBuilder(AsyncToolkit pToolkit, Scope pScope,
    QueryBuilder pQueryBuilder);

  public StructureRef createStructureRefFromParts(AsyncToolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys);

  public StructureDefinitionRef createStructureDefinitionRefFromSerialized(AsyncToolkit pToolkit, Scope pScope,
    String pSerialized);

  public BiFunction<Structure, Structure, Structure> createStandardMigration(AsyncToolkit pToolkit, Scope pScope,
    StandardMigrations pMigrationType, @NonNull Object @Nullable [] pParams);

  public void addMigration(AsyncToolkit pToolkit, Scope pScope, String pStructureDefinitionName, int pFromRevision,
    int pToRevision, BiFunction<Structure, Structure, Structure> pMigrationFunction);

  public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
    AsyncToolkit pToolkit, Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision);

  public ContextExtendedCompletionStage<@Nullable Integer> lookupLatestStructureDefinitionRevision(
    AsyncToolkit pToolkit, Scope pScope, String pDefName);

  public ContextExtendedCompletionStage<@Nullable Void> clearStructures(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDef);

  public ContextExtendedCompletionStage<Integer> countByQuery(AsyncToolkit pToolkit, Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues);

}

package com.diamondq.common.model.generic;

import com.diamondq.common.lambda.future.FutureUtils;
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
import com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class SyncAsyncPersistenceLayer implements AsyncPersistenceLayer {

  private final PersistenceLayer mPersistenceLayer;

  public SyncAsyncPersistenceLayer(PersistenceLayer pPersistenceLayer) {
    mPersistenceLayer = pPersistenceLayer;
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewStructureDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, int)
   */
  @Override
  public StructureDefinition createNewStructureDefinition(AsyncToolkit pToolkit, Scope pScope, String pName,
    int pRevision) {
    return mPersistenceLayer.createNewStructureDefinition(pToolkit.getSyncToolkit(), pScope, pName, pRevision);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewTombstoneStructureDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public StructureDefinition createNewTombstoneStructureDefinition(AsyncToolkit pToolkit, Scope pScope, String pName) {
    return mPersistenceLayer.createNewTombstoneStructureDefinition(pToolkit.getSyncToolkit(), pScope, pName);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#writeStructureDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public ContextExtendedCompletionStage<StructureDefinition> writeStructureDefinition(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinition pValue) {
    mPersistenceLayer.writeStructureDefinition(pToolkit.getSyncToolkit(), pScope, pValue);
    StructureDefinition result =
      mPersistenceLayer.lookupStructureDefinitionByName(pToolkit.getSyncToolkit(), pScope, pValue.getName());
    if (result == null)
      throw new IllegalArgumentException();
    return FutureUtils.completedFuture(result);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#enableStructureDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void enableStructureDefinition(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    mPersistenceLayer.enableStructureDefinition(pToolkit.getSyncToolkit(), pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#deleteStructureDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> deleteStructureDefinition(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pValue) {
    mPersistenceLayer.deleteStructureDefinition(pToolkit.getSyncToolkit(), pScope, pValue);
    return FutureUtils.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public ContextExtendedCompletionStage<Collection<StructureDefinitionRef>> getAllStructureDefinitionRefs(
    AsyncToolkit pToolkit, Scope pScope) {
    return ContextExtendedCompletableFuture
      .completedFuture(mPersistenceLayer.getAllStructureDefinitionRefs(pToolkit.getSyncToolkit(), pScope));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByName(
    AsyncToolkit pToolkit, Scope pScope, String pName) {
    return ContextExtendedCompletableFuture
      .completedFuture(mPersistenceLayer.lookupStructureDefinitionByName(pToolkit.getSyncToolkit(), pScope, pName));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#lookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.Integer)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByNameAndRevision(
    AsyncToolkit pToolkit, Scope pScope, String pName, @Nullable Integer pRevision) {
    return FutureUtils.completedFuture(mPersistenceLayer
      .lookupStructureDefinitionByNameAndRevision(pToolkit.getSyncToolkit(), pScope, pName, pRevision));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStructureDefinitionRef(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition, boolean)
   */
  @Override
  public StructureDefinitionRef createStructureDefinitionRef(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pResolvable, boolean pWildcard) {
    return mPersistenceLayer.createStructureDefinitionRef(pToolkit.getSyncToolkit(), pScope, pResolvable, pWildcard);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createPropertyDefinitionRef(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.PropertyDefinition,
   *      com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public PropertyDefinitionRef createPropertyDefinitionRef(AsyncToolkit pToolkit, Scope pScope,
    PropertyDefinition pResolvable, StructureDefinition pContaining) {
    return mPersistenceLayer.createPropertyDefinitionRef(pToolkit.getSyncToolkit(), pScope, pResolvable, pContaining);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStructureRef(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public StructureRef createStructureRef(AsyncToolkit pToolkit, Scope pScope, Structure pResolvable) {
    return mPersistenceLayer.createStructureRef(pToolkit.getSyncToolkit(), pScope, pResolvable);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStructureRefStr(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public String createStructureRefStr(AsyncToolkit pToolkit, Scope pScope, Structure pResolvable) {
    return mPersistenceLayer.createStructureRefStr(pToolkit.getSyncToolkit(), pScope, pResolvable);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createPropertyRef(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Property,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRef(AsyncToolkit pToolkit, Scope pScope,
    @Nullable Property<T> pResolvable, Structure pContaining) {
    return mPersistenceLayer.createPropertyRef(pToolkit.getSyncToolkit(), pScope, pResolvable, pContaining);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewPropertyDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  public PropertyDefinition createNewPropertyDefinition(AsyncToolkit pToolkit, Scope pScope, String pName,
    PropertyType pType) {
    return mPersistenceLayer.createNewPropertyDefinition(pToolkit.getSyncToolkit(), pScope, pName, pType);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#collapsePrimaryKeys(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.List)
   */
  @Override
  public String collapsePrimaryKeys(AsyncToolkit pToolkit, Scope pScope, List<@Nullable Object> pNames) {
    return mPersistenceLayer.collapsePrimaryKeys(pToolkit.getSyncToolkit(), pScope, pNames);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewStructure(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public Structure createNewStructure(AsyncToolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition) {
    return mPersistenceLayer.createNewStructure(pToolkit.getSyncToolkit(), pScope, pStructureDefinition);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewTombstoneStructure(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public Structure createNewTombstoneStructure(AsyncToolkit pToolkit, Scope pScope, Structure pOldStructure) {
    return mPersistenceLayer.createNewTombstoneStructure(pToolkit.getSyncToolkit(), pScope, pOldStructure);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> writeStructure(AsyncToolkit pToolkit, Scope pScope,
    Structure pStructure) {
    mPersistenceLayer.writeStructure(pToolkit.getSyncToolkit(), pScope, pStructure);
    return FutureUtils.completedFuture(null);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#writeStructure(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public ContextExtendedCompletionStage<Boolean> writeStructure(AsyncToolkit pToolkit, Scope pScope,
    Structure pStructure, @Nullable Structure pOldStructure) {
    return ContextExtendedCompletableFuture
      .completedFuture(mPersistenceLayer.writeStructure(pToolkit.getSyncToolkit(), pScope, pStructure, pOldStructure));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Structure> lookupStructureBySerializedRef(AsyncToolkit pToolkit,
    Scope pScope, String pSerializedRef) {
    return FutureUtils.completedFuture(
      mPersistenceLayer.lookupStructureBySerializedRef(pToolkit.getSyncToolkit(), pScope, pSerializedRef));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#lookupStructureByPrimaryKeys(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition,
   *      java.lang.Object[])
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Structure> lookupStructureByPrimaryKeys(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinition pStructureDef, @Nullable Object[] pPrimaryKeys) {
    return FutureUtils.completedFuture(
      mPersistenceLayer.lookupStructureByPrimaryKeys(pToolkit.getSyncToolkit(), pScope, pStructureDef, pPrimaryKeys));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#deleteStructure(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public ContextExtendedCompletionStage<Boolean> deleteStructure(AsyncToolkit pToolkit, Scope pScope,
    Structure pValue) {
    return ContextExtendedCompletableFuture
      .completedFuture(mPersistenceLayer.deleteStructure(pToolkit.getSyncToolkit(), pScope, pValue));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewProperty(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.PropertyDefinition, boolean,
   *      java.lang.Object)
   */
  @Override
  public <@Nullable T> Property<T> createNewProperty(AsyncToolkit pToolkit, Scope pScope,
    PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue) {
    return mPersistenceLayer.createNewProperty(pToolkit.getSyncToolkit(), pScope, pPropertyDefinition, pIsValueSet,
      pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStructureRefFromSerialized(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public StructureRef createStructureRefFromSerialized(AsyncToolkit pToolkit, Scope pScope, String pValue) {
    return mPersistenceLayer.createStructureRefFromSerialized(pToolkit.getSyncToolkit(), pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createPropertyRefFromSerialized(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(AsyncToolkit pToolkit, Scope pScope,
    String pValue) {
    return mPersistenceLayer.createPropertyRefFromSerialized(pToolkit.getSyncToolkit(), pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef,
   *      java.lang.String, com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public ContextExtendedCompletionStage<Collection<Structure>> getAllStructuresByDefinition(AsyncToolkit pToolkit,
    Scope pScope, StructureDefinitionRef pRef, @Nullable String pParentKey,
    @Nullable PropertyDefinition pParentPropertyDef) {
    return FutureUtils.completedFuture(mPersistenceLayer.getAllStructuresByDefinition(pToolkit.getSyncToolkit(), pScope,
      pRef, pParentKey, pParentPropertyDef));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#lookupStructuresByQuery(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.ModelQuery, java.util.Map)
   */
  @Override
  public ContextExtendedCompletionStage<List<Structure>> lookupStructuresByQuery(AsyncToolkit pToolkit, Scope pScope,
    ModelQuery pQuery, @Nullable Map<String, Object> pParamValues) {
    return FutureUtils.completedFuture(
      mPersistenceLayer.lookupStructuresByQuery(pToolkit.getSyncToolkit(), pScope, pQuery, pParamValues));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createNewQueryBuilder(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition,
   *      java.lang.String)
   */
  @Override
  public QueryBuilder createNewQueryBuilder(AsyncToolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDefinition, String pQueryName) {
    return mPersistenceLayer.createNewQueryBuilder(pToolkit.getSyncToolkit(), pScope, pStructureDefinition, pQueryName);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#writeQueryBuilder(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.QueryBuilder)
   */
  @Override
  public ContextExtendedCompletionStage<ModelQuery> writeQueryBuilder(AsyncToolkit pToolkit, Scope pScope,
    QueryBuilder pQueryBuilder) {
    return ContextExtendedCompletableFuture
      .completedFuture(mPersistenceLayer.writeQueryBuilder(pToolkit.getSyncToolkit(), pScope, pQueryBuilder));
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStructureRefFromParts(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure, java.lang.String,
   *      com.diamondq.common.model.interfaces.StructureDefinition, java.util.List)
   */
  @Override
  public StructureRef createStructureRefFromParts(AsyncToolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys) {
    return mPersistenceLayer.createStructureRefFromParts(pToolkit.getSyncToolkit(), pScope, pStructure, pPropName, pDef,
      pPrimaryKeys);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStructureDefinitionRefFromSerialized(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public StructureDefinitionRef createStructureDefinitionRefFromSerialized(AsyncToolkit pToolkit, Scope pScope,
    String pSerialized) {
    return mPersistenceLayer.createStructureDefinitionRefFromSerialized(pToolkit.getSyncToolkit(), pScope, pSerialized);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#createStandardMigration(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StandardMigrations,
   *      java.lang.Object[])
   */
  @Override
  public BiFunction<Structure, Structure, Structure> createStandardMigration(AsyncToolkit pToolkit, Scope pScope,
    StandardMigrations pMigrationType, @NonNull Object @Nullable [] pParams) {
    return mPersistenceLayer.createStandardMigration(pToolkit.getSyncToolkit(), pScope, pMigrationType, pParams);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#addMigration(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, int, int, java.util.function.BiFunction)
   */
  @Override
  public void addMigration(AsyncToolkit pToolkit, Scope pScope, String pStructureDefinitionName, int pFromRevision,
    int pToRevision, BiFunction<Structure, Structure, Structure> pMigrationFunction) {
    mPersistenceLayer.addMigration(pToolkit.getSyncToolkit(), pScope, pStructureDefinitionName, pFromRevision,
      pToRevision, pMigrationFunction);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#determineMigrationPath(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, int, int)
   */
  @Override
  public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
    AsyncToolkit pToolkit, Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision) {
    return mPersistenceLayer.determineMigrationPath(pToolkit.getSyncToolkit(), pScope, pStructureDefName, pFromRevision,
      pToRevision);
  }

  /**
   * @see com.diamondq.common.model.generic.AsyncPersistenceLayer#lookupLatestStructureDefinitionRevision(com.diamondq.common.model.interfaces.AsyncToolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Integer> lookupLatestStructureDefinitionRevision(
    AsyncToolkit pToolkit, Scope pScope, String pDefName) {
    return FutureUtils.completedFuture(
      mPersistenceLayer.lookupLatestStructureDefinitionRevision(pToolkit.getSyncToolkit(), pScope, pDefName));
  }

}

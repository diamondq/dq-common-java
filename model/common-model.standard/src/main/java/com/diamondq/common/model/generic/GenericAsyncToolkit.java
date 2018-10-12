package com.diamondq.common.model.generic;

import com.diamondq.common.lambda.future.ExtendedCompletionStage;
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
import com.diamondq.common.model.interfaces.Toolkit;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class GenericAsyncToolkit implements AsyncToolkit {

  private final ConcurrentMap<Scope, PersistenceLayer>                 mPersistence;

  private final ConcurrentMap<PersistenceLayer, AsyncPersistenceLayer> mAsyncPersistence;

  private final GenericToolkit                                         mSyncToolkit;

  public GenericAsyncToolkit(ConcurrentMap<Scope, PersistenceLayer> pPersistence, ConcurrentMap<String, Scope> pScopes,
    GenericToolkit pGenericToolkit) {
    mPersistence = pPersistence;
    mSyncToolkit = pGenericToolkit;
    mAsyncPersistence = new ConcurrentHashMap<>();
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#getSyncToolkit()
   */
  @Override
  public Toolkit getSyncToolkit() {
    return mSyncToolkit;
  }

  /**
   * Internal helper method that either returns a PersistenceLayer or throws an exception explaining that the scope is
   * unknown.
   *
   * @param pScope the scope
   * @return the persistence layer, never null.
   */
  protected AsyncPersistenceLayer getPersistenceLayer(Scope pScope) {
    PersistenceLayer layer = mPersistence.get(pScope);
    if (layer == null)
      throw new UnknownScopeException(pScope);
    AsyncPersistenceLayer asyncPersistenceLayer = mAsyncPersistence.get(layer);
    if (asyncPersistenceLayer == null) {
      AsyncPersistenceLayer newAsyncPersistenceLayer = layer.getAsyncPersistenceLayer();
      if ((asyncPersistenceLayer = mAsyncPersistence.putIfAbsent(layer, newAsyncPersistenceLayer)) == null)
        asyncPersistenceLayer = newAsyncPersistenceLayer;
    }
    return asyncPersistenceLayer;
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#getAllStructureDefinitionRefs(com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public ExtendedCompletionStage<Collection<StructureDefinitionRef>> getAllStructureDefinitionRefs(Scope pScope) {
    return getPersistenceLayer(pScope).getAllStructureDefinitionRefs(this, pScope);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createNewStructureDefinition(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String, int)
   */
  @Override
  public StructureDefinition createNewStructureDefinition(Scope pScope, String pName, int pRevision) {
    return getPersistenceLayer(pScope).createNewStructureDefinition(this, pScope, pName, pRevision);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#writeStructureDefinition(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public ExtendedCompletionStage<StructureDefinition> writeStructureDefinition(Scope pScope,
    StructureDefinition pValue) {
    return getPersistenceLayer(pScope).writeStructureDefinition(this, pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#deleteStructureDefinition(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> deleteStructureDefinition(Scope pScope, StructureDefinition pValue) {
    return getPersistenceLayer(pScope).deleteStructureDefinition(this, pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStructureDefinitionRef(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinition, boolean)
   */
  @Override
  public StructureDefinitionRef createStructureDefinitionRef(Scope pScope, StructureDefinition pResolvable,
    boolean pWildcard) {
    return getPersistenceLayer(pScope).createStructureDefinitionRef(this, pScope, pResolvable, pWildcard);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStructureDefinitionRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String)
   */
  @Override
  public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized) {
    return getPersistenceLayer(pScope).createStructureDefinitionRefFromSerialized(this, pScope, pSerialized);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStructureRef(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public StructureRef createStructureRef(Scope pScope, Structure pResolvable) {
    return getPersistenceLayer(pScope).createStructureRef(this, pScope, pResolvable);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStructureRefStr(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public String createStructureRefStr(Scope pScope, Structure pResolvable) {
    return getPersistenceLayer(pScope).createStructureRefStr(this, pScope, pResolvable);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createPropertyDefinitionRef(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.PropertyDefinition,
   *      com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public PropertyDefinitionRef createPropertyDefinitionRef(Scope pScope, PropertyDefinition pResolvable,
    StructureDefinition pContaining) {
    return getPersistenceLayer(pScope).createPropertyDefinitionRef(this, pScope, pResolvable, pContaining);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createPropertyRef(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Property, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public <@Nullable T> PropertyRef<@Nullable T> createPropertyRef(Scope pScope,
    @Nullable Property<@Nullable T> pResolvable, Structure pContaining) {
    return getPersistenceLayer(pScope).createPropertyRef(this, pScope, pResolvable, pContaining);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#lookupStructureDefinitionByName(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String)
   */
  @Override
  public ExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByName(Scope pScope,
    String pName) {
    return getPersistenceLayer(pScope).lookupStructureDefinitionByName(this, pScope, pName);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#lookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String, java.lang.Integer)
   */
  @Override
  public ExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByNameAndRevision(Scope pScope,
    String pName, @Nullable Integer pRevision) {
    return getPersistenceLayer(pScope).lookupStructureDefinitionByNameAndRevision(this, pScope, pName, pRevision);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createNewPropertyDefinition(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String, com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  public PropertyDefinition createNewPropertyDefinition(Scope pScope, String pName, PropertyType pType) {
    return getPersistenceLayer(pScope).createNewPropertyDefinition(this, pScope, pName, pType);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#collapsePrimaryKeys(com.diamondq.common.model.interfaces.Scope,
   *      java.util.List)
   */
  @Override
  public String collapsePrimaryKeys(Scope pScope, List<@Nullable Object> pNames) {
    return getPersistenceLayer(pScope).collapsePrimaryKeys(this, pScope, pNames);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#lookupStructureBySerializedRef(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Structure> lookupStructureBySerializedRef(Scope pScope,
    String pSerializedRef) {
    return getPersistenceLayer(pScope).lookupStructureBySerializedRef(this, pScope, pSerializedRef);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#lookupStructureByPrimaryKeys(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinition, java.lang.Object[])
   */
  @Override
  public ExtendedCompletionStage<@Nullable Structure> lookupStructureByPrimaryKeys(Scope pScope,
    StructureDefinition pStructureDef, @Nullable Object @NonNull... pPrimaryKeys) {
    return getPersistenceLayer(pScope).lookupStructureByPrimaryKeys(this, pScope, pStructureDef, pPrimaryKeys);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createNewStructure(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public Structure createNewStructure(Scope pScope, StructureDefinition pStructureDefinition) {
    return getPersistenceLayer(pScope).createNewStructure(this, pScope, pStructureDefinition);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#writeStructure(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> writeStructure(Scope pScope, Structure pStructure) {
    return getPersistenceLayer(pScope).writeStructure(this, pScope, pStructure);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#writeStructure(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Structure, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public ExtendedCompletionStage<Boolean> writeStructure(Scope pScope, Structure pStructure,
    @Nullable Structure pOldStructure) {
    return getPersistenceLayer(pScope).writeStructure(this, pScope, pStructure, pOldStructure);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#deleteStructure(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public ExtendedCompletionStage<Boolean> deleteStructure(Scope pScope, Structure pOldStructure) {
    return getPersistenceLayer(pScope).deleteStructure(this, pScope, pOldStructure);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createNewProperty(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.PropertyDefinition, boolean, java.lang.Object)
   */
  @Override
  public <@Nullable TYPE> Property<@Nullable TYPE> createNewProperty(Scope pScope,
    PropertyDefinition pPropertyDefinition, boolean pIsValueSet, @Nullable TYPE pValue) {
    return getPersistenceLayer(pScope).createNewProperty(this, pScope, pPropertyDefinition, pIsValueSet, pValue);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStructureRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String)
   */
  @Override
  public StructureRef createStructureRefFromSerialized(Scope pScope, String pValue) {
    return getPersistenceLayer(pScope).createStructureRefFromSerialized(this, pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStructureRefFromParts(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.Structure, java.lang.String,
   *      com.diamondq.common.model.interfaces.StructureDefinition, java.util.List)
   */
  @Override
  public StructureRef createStructureRefFromParts(Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys) {
    return getPersistenceLayer(pScope).createStructureRefFromParts(this, pScope, pStructure, pPropName, pDef,
      pPrimaryKeys);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createPropertyRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String)
   */
  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Scope pScope, String pValue) {
    return getPersistenceLayer(pScope).createPropertyRefFromSerialized(this, pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinitionRef, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public ExtendedCompletionStage<Collection<Structure>> getAllStructuresByDefinition(Scope pScope,
    StructureDefinitionRef pRef, @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
    return getPersistenceLayer(pScope).getAllStructuresByDefinition(this, pScope, pRef, pParentKey, pParentPropertyDef);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createNewQueryBuilder(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StructureDefinition, java.lang.String)
   */
  @Override
  public QueryBuilder createNewQueryBuilder(Scope pScope, StructureDefinition pStructureDefinition, String pQueryName) {
    return getPersistenceLayer(pScope).createNewQueryBuilder(this, pScope, pStructureDefinition, pQueryName);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#writeQueryBuilder(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.QueryBuilder)
   */
  @Override
  public ExtendedCompletionStage<ModelQuery> writeQueryBuilder(Scope pScope, QueryBuilder pQueryBuilder) {
    return getPersistenceLayer(pScope).writeQueryBuilder(this, pScope, pQueryBuilder);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#lookupStructuresByQuery(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.ModelQuery, java.util.Map)
   */
  @Override
  public ExtendedCompletionStage<List<Structure>> lookupStructuresByQuery(Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues) {
    return getPersistenceLayer(pScope).lookupStructuresByQuery(this, pScope, pQuery, pParamValues);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#createStandardMigration(com.diamondq.common.model.interfaces.Scope,
   *      com.diamondq.common.model.interfaces.StandardMigrations, java.lang.Object[])
   */
  @Override
  public BiFunction<Structure, Structure, Structure> createStandardMigration(Scope pScope,
    StandardMigrations pMigrationType, @NonNull Object @Nullable... pParams) {
    return getPersistenceLayer(pScope).createStandardMigration(this, pScope, pMigrationType, pParams);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#addMigration(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String, int, int, java.util.function.BiFunction)
   */
  @Override
  public void addMigration(Scope pScope, String pStructureDefinitionName, int pFromRevision, int pToRevision,
    BiFunction<Structure, Structure, Structure> pMigrationFunction) {
    getPersistenceLayer(pScope).addMigration(this, pScope, pStructureDefinitionName, pFromRevision, pToRevision,
      pMigrationFunction);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#determineMigrationPath(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String, int, int)
   */
  @Override
  public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
    Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision) {
    return getPersistenceLayer(pScope).determineMigrationPath(this, pScope, pStructureDefName, pFromRevision,
      pToRevision);
  }

  /**
   * @see com.diamondq.common.model.interfaces.AsyncToolkit#lookupLatestStructureDefinitionRevision(com.diamondq.common.model.interfaces.Scope,
   *      java.lang.String)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Integer> lookupLatestStructureDefinitionRevision(Scope pScope,
    String pDefName) {
    return getPersistenceLayer(pScope).lookupLatestStructureDefinitionRevision(this, pScope, pDefName);
  }

}

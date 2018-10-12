package com.diamondq.common.model.generic.osgi;

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
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

public class WrappedAsyncToolkit implements AsyncToolkit {

  private final AsyncToolkit mAsyncToolkit;

  public WrappedAsyncToolkit(AsyncToolkit pAsyncToolkit) {
    mAsyncToolkit = pAsyncToolkit;
  }

  private Scope dewrapScope(Scope pScope) {
    if (pScope instanceof WrappedScope)
      return ((WrappedScope) pScope).getScope();
    return pScope;
  }

  @Override
  public ExtendedCompletionStage<Collection<StructureDefinitionRef>> getAllStructureDefinitionRefs(Scope pScope) {
    return mAsyncToolkit.getAllStructureDefinitionRefs(dewrapScope(pScope));
  }

  @Override
  public StructureDefinition createNewStructureDefinition(Scope pScope, String pName, int pRevision) {
    return mAsyncToolkit.createNewStructureDefinition(dewrapScope(pScope), pName, pRevision);
  }

  @Override
  public ExtendedCompletionStage<StructureDefinition> writeStructureDefinition(Scope pScope,
    StructureDefinition pValue) {
    return mAsyncToolkit.writeStructureDefinition(dewrapScope(pScope), pValue);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> deleteStructureDefinition(Scope pScope, StructureDefinition pValue) {
    return mAsyncToolkit.deleteStructureDefinition(dewrapScope(pScope), pValue);
  }

  @Override
  public StructureDefinitionRef createStructureDefinitionRef(Scope pScope, StructureDefinition pResolvable,
    boolean pWildcard) {
    return mAsyncToolkit.createStructureDefinitionRef(dewrapScope(pScope), pResolvable, pWildcard);
  }

  @Override
  public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized) {
    return mAsyncToolkit.createStructureDefinitionRefFromSerialized(dewrapScope(pScope), pSerialized);
  }

  @Override
  public StructureRef createStructureRef(Scope pScope, Structure pResolvable) {
    return mAsyncToolkit.createStructureRef(dewrapScope(pScope), pResolvable);
  }

  @Override
  public String createStructureRefStr(Scope pScope, Structure pResolvable) {
    return mAsyncToolkit.createStructureRefStr(dewrapScope(pScope), pResolvable);
  }

  @Override
  public PropertyDefinitionRef createPropertyDefinitionRef(Scope pScope, PropertyDefinition pResolvable,
    StructureDefinition pContaining) {
    return mAsyncToolkit.createPropertyDefinitionRef(dewrapScope(pScope), pResolvable, pContaining);
  }

  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRef(Scope pScope, @Nullable Property<T> pResolvable,
    Structure pContaining) {
    return mAsyncToolkit.createPropertyRef(dewrapScope(pScope), pResolvable, pContaining);
  }

  @Override
  public ExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByName(Scope pScope,
    String pName) {
    return mAsyncToolkit.lookupStructureDefinitionByName(dewrapScope(pScope), pName);
  }

  @Override
  public ExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByNameAndRevision(Scope pScope,
    String pName, @Nullable Integer pRevision) {
    return mAsyncToolkit.lookupStructureDefinitionByNameAndRevision(dewrapScope(pScope), pName, pRevision);
  }

  @Override
  public PropertyDefinition createNewPropertyDefinition(Scope pScope, String pName, PropertyType pType) {
    return mAsyncToolkit.createNewPropertyDefinition(dewrapScope(pScope), pName, pType);
  }

  @Override
  public String collapsePrimaryKeys(Scope pScope, List<@Nullable Object> pNames) {
    return mAsyncToolkit.collapsePrimaryKeys(dewrapScope(pScope), pNames);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Structure> lookupStructureBySerializedRef(Scope pScope,
    String pSerializedRef) {
    return mAsyncToolkit.lookupStructureBySerializedRef(dewrapScope(pScope), pSerializedRef);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Structure> lookupStructureByPrimaryKeys(Scope pScope,
    StructureDefinition pStructureDef, @Nullable Object @NonNull... pPrimaryKeys) {
    return mAsyncToolkit.lookupStructureByPrimaryKeys(dewrapScope(pScope), pStructureDef, pPrimaryKeys);
  }

  @Override
  public Structure createNewStructure(Scope pScope, StructureDefinition pStructureDefinition) {
    return mAsyncToolkit.createNewStructure(dewrapScope(pScope), pStructureDefinition);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> writeStructure(Scope pScope, Structure pStructure) {
    return mAsyncToolkit.writeStructure(dewrapScope(pScope), pStructure);
  }

  @Override
  public ExtendedCompletionStage<Boolean> writeStructure(Scope pScope, Structure pStructure,
    @Nullable Structure pOldStructure) {
    return mAsyncToolkit.writeStructure(dewrapScope(pScope), pStructure, pOldStructure);
  }

  @Override
  public ExtendedCompletionStage<Boolean> deleteStructure(Scope pScope, Structure pOldStructure) {
    return mAsyncToolkit.deleteStructure(dewrapScope(pScope), pOldStructure);
  }

  @Override
  public <@Nullable TYPE> Property<TYPE> createNewProperty(Scope pScope, PropertyDefinition pPropertyDefinition,
    boolean pIsValueSet, TYPE pValue) {
    return mAsyncToolkit.createNewProperty(dewrapScope(pScope), pPropertyDefinition, pIsValueSet, pValue);
  }

  @Override
  public StructureRef createStructureRefFromSerialized(Scope pScope, String pValue) {
    return mAsyncToolkit.createStructureRefFromSerialized(dewrapScope(pScope), pValue);
  }

  @Override
  public StructureRef createStructureRefFromParts(Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys) {
    return mAsyncToolkit.createStructureRefFromParts(dewrapScope(pScope), pStructure, pPropName, pDef, pPrimaryKeys);
  }

  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Scope pScope, String pValue) {
    return mAsyncToolkit.createPropertyRefFromSerialized(dewrapScope(pScope), pValue);
  }

  @Override
  public ExtendedCompletionStage<Collection<Structure>> getAllStructuresByDefinition(Scope pScope,
    StructureDefinitionRef pRef, @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
    return mAsyncToolkit.getAllStructuresByDefinition(dewrapScope(pScope), pRef, pParentKey, pParentPropertyDef);
  }

  @Override
  public QueryBuilder createNewQueryBuilder(Scope pScope, StructureDefinition pStructureDefinition, String pQueryName) {
    return mAsyncToolkit.createNewQueryBuilder(dewrapScope(pScope), pStructureDefinition, pQueryName);
  }

  @Override
  public ExtendedCompletionStage<ModelQuery> writeQueryBuilder(Scope pScope, QueryBuilder pQueryBuilder) {
    return mAsyncToolkit.writeQueryBuilder(dewrapScope(pScope), pQueryBuilder);
  }

  @Override
  public ExtendedCompletionStage<List<Structure>> lookupStructuresByQuery(Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues) {
    return mAsyncToolkit.lookupStructuresByQuery(dewrapScope(pScope), pQuery, pParamValues);
  }

  @Override
  public BiFunction<Structure, Structure, Structure> createStandardMigration(Scope pScope,
    StandardMigrations pMigrationType, @NonNull Object @Nullable... pParams) {
    return mAsyncToolkit.createStandardMigration(dewrapScope(pScope), pMigrationType, pParams);
  }

  @Override
  public void addMigration(Scope pScope, String pStructureDefinitionName, int pFromRevision, int pToRevision,
    BiFunction<Structure, Structure, Structure> pMigrationFunction) {
    mAsyncToolkit.addMigration(dewrapScope(pScope), pStructureDefinitionName, pFromRevision, pToRevision,
      pMigrationFunction);
  }

  @Override
  public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
    Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision) {
    return mAsyncToolkit.determineMigrationPath(dewrapScope(pScope), pStructureDefName, pFromRevision, pToRevision);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Integer> lookupLatestStructureDefinitionRevision(Scope pScope,
    String pDefName) {
    return mAsyncToolkit.lookupLatestStructureDefinitionRevision(dewrapScope(pScope), pDefName);
  }

  @Override
  public Toolkit getSyncToolkit() {
    return mAsyncToolkit.getSyncToolkit();
  }

}

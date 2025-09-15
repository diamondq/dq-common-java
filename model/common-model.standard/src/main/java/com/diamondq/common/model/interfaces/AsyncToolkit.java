package com.diamondq.common.model.interfaces;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface AsyncToolkit {

  /**
   * Returns the complete set of StructureDefinitions (references).
   *
   * @param pScope the scope
   * @return the set
   */

  ContextExtendedCompletionStage<Collection<StructureDefinitionRef>> getAllStructureDefinitionRefs(Scope pScope);

  /**
   * Creates a new StructureDefinition that has not yet persisted.
   *
   * @param pScope the scope
   * @param pName the name for the StructureDefinition
   * @param pRevision the revision
   * @return the blank StructureDefinition
   */
  StructureDefinition createNewStructureDefinition(Scope pScope, String pName, int pRevision);

  /**
   * Writes a StructureDefinition back to persistent storage.<br/> Future queries will return the persisting value, but
   * existing StructureDefinition will not be automatically updated.
   *
   * @param pScope the scope
   * @param pValue the StructureDefinition to write
   * @return the stored StructureDefinition
   */
  ContextExtendedCompletionStage<StructureDefinition> writeStructureDefinition(Scope pScope,
    StructureDefinition pValue);

  /**
   * Deletes an existing StructureDefinition from permanent storage.
   *
   * @param pScope the scope
   * @param pValue the StructureDefinition to delete.
   * @return completion future
   */
  ContextExtendedCompletionStage<@Nullable Void> deleteStructureDefinition(Scope pScope, StructureDefinition pValue);

  /**
   * Creates a reference for a StructureDefinition
   *
   * @param pScope the scope
   * @param pResolvable the StructureDefinition
   * @param pWildcard true of the reference should be a wildcard reference or false if not
   * @return the reference
   */
  StructureDefinitionRef createStructureDefinitionRef(Scope pScope, StructureDefinition pResolvable, boolean pWildcard);

  /**
   * Creates a reference for StructureDefinition
   *
   * @param pScope the scope
   * @param pSerialized the serialized reference
   * @return the reference
   */

  StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized);

  /**
   * Creates a reference for a Structure
   *
   * @param pScope the scope
   * @param pResolvable the Structure
   * @return the reference
   */

  StructureRef createStructureRef(Scope pScope, Structure pResolvable);

  /**
   * Creates a reference string for a Structure
   *
   * @param pScope the scope
   * @param pResolvable the Structure
   * @return the reference string
   */

  String createStructureRefStr(Scope pScope, Structure pResolvable);

  /**
   * Creates a reference for a PropertyDefinition
   *
   * @param pScope the scope
   * @param pResolvable propertyDefinition
   * @param pContaining the containing StructureDefinition
   * @return the reference
   */

  PropertyDefinitionRef createPropertyDefinitionRef(Scope pScope, PropertyDefinition pResolvable,
    StructureDefinition pContaining);

  /**
   * Creates a reference for a Property
   *
   * @param pScope the scope
   * @param pResolvable the Property (can be null for a 'loose' PropertyDefinitionRef)
   * @param pContaining the containing Structure
   * @return the reference
   */

  <T extends @Nullable Object> PropertyRef<T> createPropertyRef(Scope pScope, @Nullable Property<T> pResolvable,
    Structure pContaining);

  /**
   * Looks up a StructureDefinition by name
   *
   * @param pScope the scope
   * @param pName the name
   * @return the StructureDefinition or null
   */
  ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByName(Scope pScope,
    String pName);

  /**
   * Looks up a StructureDefinition by name and revision
   *
   * @param pScope the scope
   * @param pName the name
   * @param pRevision the revision (if null, then find the latest)
   * @return the StructureDefinition or null
   */
  ContextExtendedCompletionStage<@Nullable StructureDefinition> lookupStructureDefinitionByNameAndRevision(Scope pScope,
    String pName, @Nullable Integer pRevision);

  /**
   * Creates a new blank PropertyDefinition.
   *
   * @param pScope the scope
   * @param pName the property name
   * @param pType the property type
   * @return the PropertyDefinition, never null.
   */
  PropertyDefinition createNewPropertyDefinition(Scope pScope, String pName, PropertyType pType);

  /**
   * Given a list of primary keys, collapse it into a single name. This usually just returns a String separated by a
   * separator such as '$'. NOTE: It also usually munges the primary keys to guarantee the ability to separate the
   * pieces again. There is no standard for the format. The caller must use the same 'toolkit' to pull apart a primary
   * key.
   *
   * @param pScope the scope
   * @param pNames the names
   * @return the collapsed primary key
   */
  String collapsePrimaryKeys(Scope pScope, List<@Nullable Object> pNames);

  /**
   * Looks up a Structure by the full serialized reference string. This is actually called by the
   * {@link StructureRef#resolve()}.
   *
   * @param pScope the scope
   * @param pSerializedRef the serialized reference string (generally created from
   *   {@link StructureRef#getSerializedString()}
   * @return structure or null
   */
  ContextExtendedCompletionStage<@Nullable Structure> lookupStructureBySerializedRef(Scope pScope,
    String pSerializedRef);

  /**
   * Looks up a Structure with the given primary keys
   *
   * @param pScope the scope
   * @param pStructureDef the structure definition
   * @param pPrimaryKeys the primary keys
   * @return the structure or null if there is no match
   */
  ContextExtendedCompletionStage<@Nullable Structure> lookupStructureByPrimaryKeys(Scope pScope,
    StructureDefinition pStructureDef, @Nullable Object... pPrimaryKeys);

  /**
   * Creates a new Structure given a StructureDefinition. NOTE: The StructureDefinition must be in the same scope as
   * that provided. NOTE: The new Structure is NOT automatically written to the toolkit.
   *
   * @param pScope the scope
   * @param pStructureDefinition the StructureDefinition to use
   * @return the new, empty, Structure
   */
  Structure createNewStructure(Scope pScope, StructureDefinition pStructureDefinition);

  /**
   * Writes a Structure to the persistence layer.
   *
   * @param pScope the scope
   * @param pStructure the Structure
   * @return the completion future
   */
  ContextExtendedCompletionStage<@Nullable Void> writeStructure(Scope pScope, Structure pStructure);

  /**
   * Writes a Structure to the persistence layer if the old structure is what was previously in the persistence layer
   * (used to provide optimistic transactions)
   *
   * @param pScope the scope
   * @param pStructure the structure
   * @param pOldStructure the old structure or null if there shouldn't be a matching structure
   * @return a future true if the structure was written or false if it wasn't written because it didn't match the old
   *   structure
   */
  ContextExtendedCompletionStage<Boolean> writeStructure(Scope pScope, Structure pStructure,
    @Nullable Structure pOldStructure);

  /**
   * Deletes an existing Structure from permanent storage.
   *
   * @param pScope the scope
   * @param pOldStructure the Structure to delete.
   * @return a future true if the structure was deleted or false if it wasn't deleted because there wasn't a structure
   *   that matches the old structure.
   */
  ContextExtendedCompletionStage<Boolean> deleteStructure(Scope pScope, Structure pOldStructure);

  /**
   * Creates a new Property given a PropertyDefinition. NOTE: The PropertyDefinition must be in the same scope as that
   * provided. NOTE: The new Property is NOT automatically attached to a Structure.
   *
   * @param pScope the scope
   * @param pPropertyDefinition the PropertyDefinition to use
   * @param isValueSet true if the value is set or false if it's not
   * @param pValue the value (if the value is not set, then this is ignored)
   * @return the new, empty, Property
   */
  <TYPE extends @Nullable Object> Property<TYPE> createNewProperty(Scope pScope, PropertyDefinition pPropertyDefinition,
    boolean isValueSet, TYPE pValue);

  /**
   * Creates a new StructureRef from a serialized string
   *
   * @param pScope the scope
   * @param pValue the serialized string
   * @return the StructureRef
   */
  StructureRef createStructureRefFromSerialized(Scope pScope, String pValue);

  /**
   * Creates a new StructureRef from the parts of a serialized string. There are multiple different ways that this can
   * be provided
   * <ul>
   * <li>&lt;pStructure></li>
   * <li>&lt;pDef>/&lt;pPrimaryKeys></li>
   * <li>&lt;pStructure>/&lt;pDef>/&lt;pPrimaryKeys></li>
   * <li>&lt;pStructure>/&lt;pPropName>/&lt;pDef>/&lt;pPrimaryKeys></li>
   * </ul>
   *
   * @param pScope the scope
   * @param pStructure the Structure
   * @param pPropName the Property Definition name
   * @param pDef the StructureDefinition
   * @param pPrimaryKeys the set of primary keys in the appropriate order
   * @return the StructureRef
   */
  StructureRef createStructureRefFromParts(Scope pScope, @Nullable Structure pStructure, @Nullable String pPropName,
    @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys);

  /**
   * Creates a new PropertyRef from a serialized string
   *
   * @param pScope the scope
   * @param pValue the serialized string
   * @return the PropertyRef
   */
  <T extends @Nullable Object> PropertyRef<T> createPropertyRefFromSerialized(Scope pScope, String pValue);

  ContextExtendedCompletionStage<Collection<Structure>> getAllStructuresByDefinition(Scope pScope,
    StructureDefinitionRef pRef, @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef);

  /**
   * Creates a new empty QueryBuilder object.
   *
   * @param pScope the scope
   * @param pStructureDefinition the structure definition
   * @param pQueryName the query name
   * @return the QueryBuilder
   */
  QueryBuilder createNewQueryBuilder(Scope pScope, StructureDefinition pStructureDefinition, String pQueryName);

  /**
   * Writes the query builder.
   *
   * @param pScope the scope
   * @param pQueryBuilder the query builder
   * @return the query
   */
  ContextExtendedCompletionStage<ModelQuery> writeQueryBuilder(Scope pScope, QueryBuilder pQueryBuilder);

  /**
   * Executes a previously written query
   *
   * @param pScope the scope
   * @param pQuery the query
   * @param pParamValues the map of parameters
   * @return the result
   */
  ContextExtendedCompletionStage<List<Structure>> lookupStructuresByQuery(Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues);

  /**
   * Executes a previously written query and returns the number of matching records
   *
   * @param pScope the scope
   * @param pQuery the query
   * @param pParamValues the map of parameters
   * @return the number of matching records
   */
  ContextExtendedCompletionStage<Integer> countByQuery(Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues);

  /**
   * Creates a new standard migration
   *
   * @param pScope the scope
   * @param pMigrationType the type of migration
   * @param pParams parameters needed for that migration type
   * @return the migration function
   */
  BiFunction<Structure, Structure, Structure> createStandardMigration(Scope pScope, StandardMigrations pMigrationType,
    Object @Nullable ... pParams);

  /**
   * Adds a migration between two revisions of a Structure's Definition
   *
   * @param pScope the scope
   * @param pStructureDefinitionName the StructureDefinition name
   * @param pFromRevision the older revision of the StructureDefinition
   * @param pToRevision the newer revision of the StructureDefinition
   * @param pMigrationFunction the function that takes the older Structure and migrates it to the new Structure. The
   *   starting point of the new Structure is passed in as the second parameter and must be returned as the result.
   */
  void addMigration(Scope pScope, String pStructureDefinitionName, int pFromRevision, int pToRevision,
    BiFunction<Structure, Structure, Structure> pMigrationFunction);

  /**
   * Determines the migration path from one revision to another
   *
   * @param pScope the scope
   * @param pStructureDefName the StructureDefinition name
   * @param pFromRevision the starting revision
   * @param pToRevision the ending revision
   * @return the path or null if there is no possible path
   */
  @Nullable
  List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(Scope pScope,
    String pStructureDefName, int pFromRevision, int pToRevision);

  /**
   * Returns the latest revision of a given structure definition name
   *
   * @param pScope the scope
   * @param pDefName the definition name
   * @return the revision (or null if the definition doesn't exist)
   */
  ContextExtendedCompletionStage<@Nullable Integer> lookupLatestStructureDefinitionRevision(Scope pScope,
    String pDefName);

  /**
   * Returns the synchronous version of the toolkit
   *
   * @return the toolkit
   */
  Toolkit getSyncToolkit();

  /**
   * Deletes all structures for the given structure definition
   *
   * @param pScope the scope
   * @param pStructureDef the structure definition
   * @return the future
   */
  ContextExtendedCompletionStage<@Nullable Void> clearStructures(Scope pScope, StructureDefinition pStructureDef);
}

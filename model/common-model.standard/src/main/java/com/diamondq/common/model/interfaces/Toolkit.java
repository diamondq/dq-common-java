package com.diamondq.common.model.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

/**
 * The toolkit represents the top of the model chain. Get access to a Toolkit via the {@link ToolkitFactory}.
 */
public interface Toolkit {

  /**
   * Returns the complete list of scopes that are supported by this toolkit
   *
   * @return the scopes
   */

  public Collection<Scope> getAllScopes();

  /**
   * Returns a scope with a given name
   *
   * @param pName the name
   * @return the Scope or null if there is no scope by that name
   */
  public @Nullable Scope getScope(String pName);

  /**
   * Returns a scope with a given name
   *
   * @param pName the name
   * @return the Scope (the existing one or a new one will be created)
   */

  public Scope getOrCreateScope(String pName);

  /**
   * Removes a scope by name
   * 
   * @param pName the name
   * @return true if the scope was removed or false if the scope didn't exist
   */
  public boolean removeScope(String pName);

  /**
   * Returns the complete set of StructureDefinitions (references).
   *
   * @param pScope the scope
   * @return the set
   */

  public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(Scope pScope);

  /**
   * Creates a new StructureDefinition that is not yet persisted. NOTE: The revision is assumed to be 1.
   *
   * @param pScope the scope
   * @param pName the name for the StructureDefinition
   * @return the blank StructureDefinition
   */

  public StructureDefinition createNewStructureDefinition(Scope pScope, String pName);

  /**
   * Creates a new StructureDefinition that is not yet persisted.
   *
   * @param pScope the scope
   * @param pName the name for the StructureDefinition
   * @param pRevision the revision
   * @return the blank StructureDefinition
   */
  public StructureDefinition createNewStructureDefinition(Scope pScope, String pName, int pRevision);

  /**
   * Writes a StructureDefinition back to persistent storage. Future queries will return the persisted value, but
   * existing StructureDefinition's will not be automatically updated.
   *
   * @param pScope the scope
   * @param pValue the StructureDefinition to write
   */
  public void writeStructureDefinition(Scope pScope, StructureDefinition pValue);

  /**
   * Deletes an existing StructureDefinition from permanent storage.
   *
   * @param pScope the scope
   * @param pValue the StructureDefinition to delete.
   */
  public void deleteStructureDefinition(Scope pScope, StructureDefinition pValue);

  /**
   * Creates a reference for a StructureDefinition
   *
   * @param pScope the scope
   * @param pResolvable the StructureDefinition
   * @param pWildcard true of the reference should be a wildcard reference or false if not
   * @return the reference
   */
  public StructureDefinitionRef createStructureDefinitionRef(Scope pScope, StructureDefinition pResolvable,
    boolean pWildcard);

  /**
   * Creates a reference for StructureDefinition
   *
   * @param pScope the scope
   * @param pSerialized the serialized reference
   * @return the reference
   */

  public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized);

  /**
   * Creates a reference for a Structure
   *
   * @param pScope the scope
   * @param pResolvable the Structure
   * @return the reference
   */

  public StructureRef createStructureRef(Scope pScope, Structure pResolvable);

  /**
   * Creates a reference string for a Structure
   *
   * @param pScope the scope
   * @param pResolvable the Structure
   * @return the reference string
   */

  public String createStructureRefStr(Scope pScope, Structure pResolvable);

  /**
   * Creates a reference for a PropertyDefinition
   *
   * @param pScope the scope
   * @param pResolvable the PropertyDefinition
   * @param pContaining the containing StructureDefinition
   * @return the reference
   */

  public PropertyDefinitionRef createPropertyDefinitionRef(Scope pScope, PropertyDefinition pResolvable,
    StructureDefinition pContaining);

  /**
   * Creates a reference for a Property
   *
   * @param pScope the scope
   * @param pResolvable the Property (can be null for a 'loose' PropertyDefinitionRef)
   * @param pContaining the containing Structure
   * @return the reference
   */

  public <@Nullable T> PropertyRef<T> createPropertyRef(Scope pScope, @Nullable Property<T> pResolvable,
    Structure pContaining);

  /**
   * Looks up a StructureDefinition by name
   *
   * @param pScope the scope
   * @param pName the name
   * @return the StructureDefinition or null
   */
  public @Nullable StructureDefinition lookupStructureDefinitionByName(Scope pScope, String pName);

  /**
   * Looks up a StructureDefinition by name and revision
   *
   * @param pScope the scope
   * @param pName the name
   * @param pRevision the revision (if null then find the latest)
   * @return the StructureDefinition or null
   */
  public @Nullable StructureDefinition lookupStructureDefinitionByNameAndRevision(Scope pScope, String pName,
    @Nullable Integer pRevision);

  /**
   * Creates a new blank PropertyDefinition.
   *
   * @param pScope the scope
   * @param pName the property name
   * @param pType the property type
   * @return the PropertyDefinition, never null.
   */
  public PropertyDefinition createNewPropertyDefinition(Scope pScope, String pName, PropertyType pType);

  /**
   * Given a list of primary keys, collapse it into a single name. This usually just returns a String separated by a
   * separator such as '$'. NOTE: It also usually munges the primary keys to guarantee the ability to separate the
   * pieces again. There is no standard for the format. You must use the same 'toolkit' to pull apart a primary key.
   *
   * @param pScope the scope
   * @param pNames the names
   * @return the collapsed primary key
   */
  public String collapsePrimaryKeys(Scope pScope, List<@Nullable Object> pNames);

  /**
   * Looks up a Structure by the full serialized reference string. This is actually called by the
   * {@link StructureRef#resolve()}.
   *
   * @param pScope the scope
   * @param pSerializedRef the serialized reference string (generally created from
   *          {@link StructureRef#getSerializedString()}
   * @return the Structure or null
   */
  public @Nullable Structure lookupStructureBySerializedRef(Scope pScope, String pSerializedRef);

  /**
   * Looks up a Structure with the given primary keys
   * 
   * @param pScope the scope
   * @param pStructureDef the structure definition
   * @param pPrimaryKeys the primary keys
   * @return the structure or null if there is no match
   */
  public @Nullable Structure lookupStructureByPrimaryKeys(Scope pScope, StructureDefinition pStructureDef,
    @Nullable Object... pPrimaryKeys);

  /**
   * Creates a new Structure given a StructureDefinition. NOTE: The StructureDefinition must be in the same scope as
   * that provided. NOTE: The new Structure is NOT automatically written to the toolkit.
   *
   * @param pScope the scope
   * @param pStructureDefinition the StructureDefinition to use
   * @return the new, empty, Structure
   */
  public Structure createNewStructure(Scope pScope, StructureDefinition pStructureDefinition);

  /**
   * Writes a Structure to the persistence layer.
   *
   * @param pScope the scope
   * @param pStructure the Structure
   */
  public void writeStructure(Scope pScope, Structure pStructure);

  /**
   * Writes a Structure to the persistence layer if the old structure is what was previously in the persistence layer
   * (used to provide optimistic transactions)
   *
   * @param pScope the scope
   * @param pStructure the structure
   * @param pOldStructure the old structure or null if there shouldn't be a matching structure
   * @return true if the structure was written or false if it wasn't written because it didn't match the old structure
   */
  public boolean writeStructure(Scope pScope, Structure pStructure, @Nullable Structure pOldStructure);

  /**
   * Deletes an existing Structure from permanent storage.
   *
   * @param pScope the scope
   * @param pOldStructure the Structure to delete.
   * @return true if the structure was deleted or false if it wasn't deleted because there wasn't a structure that
   *         matches the old structure.
   */
  public boolean deleteStructure(Scope pScope, Structure pOldStructure);

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
  public <@Nullable TYPE> Property<TYPE> createNewProperty(Scope pScope, PropertyDefinition pPropertyDefinition,
    boolean isValueSet, TYPE pValue);

  /**
   * Creates a new TranslatableString object for the given key
   *
   * @param pScope the scope
   * @param pKey the key
   * @return the TranslatableString
   */
  public TranslatableString createNewTranslatableString(Scope pScope, String pKey);

  /**
   * Lookup for existing EditorStructureDefinition's that correspond to the provided StructureDefinition.
   *
   * @param pScope the scope
   * @param pRef the reference to the StructureDefinition of interest
   * @return the list of EditorStructureDefinition's in descending order of priority. At minimum, will include a generic
   *         editor structure definition.
   */
  public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(Scope pScope,
    StructureDefinitionRef pRef);

  /**
   * Creates a new EditorGroupDefinition. NOTE: It is not attached to any parent upon creation.
   *
   * @param pScope the scope
   * @return the new EditorGroupDefinition
   */
  public EditorGroupDefinition createNewEditorGroupDefinition(Scope pScope);

  /**
   * Creates a new EditorPropertyDefinition. NOTE: It is not attached to any parent upon creation.
   *
   * @param pScope the scope
   * @return the new EditorPropertyDefinition
   */
  public EditorPropertyDefinition createNewEditorPropertyDefinition(Scope pScope);

  /**
   * Creates a new EditorStructureDefinition. NOTE: It is not persisted upon creation.
   *
   * @param pScope the scope
   * @param pName the name
   * @param pRef the reference
   * @return the new EditorStructureDefinition
   */
  public EditorStructureDefinition createNewEditorStructureDefinition(Scope pScope, String pName,
    StructureDefinitionRef pRef);

  /**
   * Writes an EditorStructureDefintion to persistent storage.
   *
   * @param pScope the scope
   * @param pEditorStructureDefinition the object to write
   */
  public void writeEditorStructureDefinition(Scope pScope, EditorStructureDefinition pEditorStructureDefinition);

  /**
   * Deletes an existing EditorStructureDefinition from permanent storage.
   *
   * @param pScope the scope
   * @param pValue the EditorStructureDefinition to delete.
   */
  public void deleteEditorStructureDefinition(Scope pScope, EditorStructureDefinition pValue);

  /**
   * Creates a new StructureRef from a serialized string
   *
   * @param pScope
   * @param pValue the serialized string
   * @return the StructureRef
   */
  public StructureRef createStructureRefFromSerialized(Scope pScope, String pValue);

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
  public StructureRef createStructureRefFromParts(Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys);

  /**
   * Creates a new PropertyRef from a serialized string
   *
   * @param pScope the scope
   * @param pValue the serialized string
   * @return the PropertyRef
   */
  public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Scope pScope, String pValue);

  public Collection<Structure> getAllStructuresByDefinition(Scope pScope, StructureDefinitionRef pRef,
    @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef);

  /**
   * Looks up a resource string under the given scope, locale and key
   *
   * @param pScope the scope
   * @param pLocale the locale. If null, then the 'default' is used
   * @param pKey the key
   * @return the matching resource string or null if there is none anywhere.
   */
  public @Nullable String lookupResourceString(Scope pScope, @Nullable Locale pLocale, String pKey);

  /**
   * Sets the global default locale (applies to all threads that have not specifically been set)
   *
   * @param pScope the scope or if null, then it applies to all existing scopes (but not to-be created ones)
   * @param pLocale the locale
   */
  public void setGlobalDefaultLocale(@Nullable Scope pScope, Locale pLocale);

  /**
   * Sets the thread specific locale
   *
   * @param pScope the scope or if null, then it applies to all existing scopes (but not to-be created ones)
   * @param pLocale the locale or if null, then it's clearing the thread specific locale
   */
  public void setThreadLocale(@Nullable Scope pScope, @Nullable Locale pLocale);

  /**
   * Returns whether it is valid to write a resource string.
   *
   * @param pScope the scope
   * @return true if it is supported or false otherwise.
   */
  public boolean isResourceStringWritingSupported(Scope pScope);

  /**
   * This writes a new resource string to the scope. NOTE: Not all scopes will support this. Check with
   * {@link Toolkit#isResourceStringWritingSupported(Scope)} before calling or risk an exception.
   *
   * @param pScope the scope
   * @param pLocale the locale
   * @param pKey the key
   * @param pValue the value
   */
  public void writeResourceString(Scope pScope, Locale pLocale, String pKey, String pValue);

  /**
   * This deletes an existing resource string from the scope. NOTE: Not all scopes will support this. Check with
   * {@link #isResourceStringWritingSupported(Scope)} before calling or risk an exception.
   *
   * @param pScope the scope
   * @param pLocale the locale
   * @param pKey the key
   */
  public void deleteResourceString(Scope pScope, Locale pLocale, String pKey);

  /**
   * Returns the set of key/value pairs for a given locale. This will not do any form of fallbacks, and will only return
   * the key/values for the exact locale.
   *
   * @param pScope the scope
   * @param pLocale the locale
   * @return the set of key/value pairs, never null
   */
  public Map<String, String> getResourceStringsByLocale(Scope pScope, Locale pLocale);

  /**
   * Returns the set of Locales that have ResourceStrings assigned to them
   *
   * @param pScope the scope
   * @return the set of Locales, never null
   */
  public Collection<Locale> getResourceStringLocales(Scope pScope);

  /**
   * Creates a new empty QueryBuilder object.
   *
   * @param pScope the scope
   * @param pStructureDefinition the structure definition
   * @param pQueryName the query name
   * @return the QueryBuilder
   */
  public QueryBuilder createNewQueryBuilder(Scope pScope, StructureDefinition pStructureDefinition, String pQueryName);

  /**
   * Writes the query builder.
   * 
   * @param pScope the scope
   * @param pQueryBuilder the query builder
   * @return the query
   */
  public Query writeQueryBuilder(Scope pScope, QueryBuilder pQueryBuilder);

  /**
   * Executes a previously written query
   * 
   * @param pScope the scope
   * @param pQuery the query
   * @param pParamValues the map of parameters
   * @return the result
   */
  public List<Structure> lookupStructuresByQuery(Scope pScope, Query pQuery,
    @Nullable Map<String, Object> pParamValues);

  /**
   * Creates a new standard migration
   *
   * @param pScope the scope
   * @param pMigrationType the type of migration
   * @param pParams parameters needed for that migration type
   * @return the migration function
   */
  public BiFunction<Structure, Structure, Structure> createStandardMigration(Scope pScope,
    StandardMigrations pMigrationType, @NonNull Object @Nullable... pParams);

  /**
   * Adds a migration between two revisions of a Structure's Definition
   *
   * @param pScope the scope
   * @param pStructureDefinitionName the StructureDefinition name
   * @param pFromRevision the older revision of the StructureDefinition
   * @param pToRevision the newer revision of the StructureDefinition
   * @param pMigrationFunction the function that takes the older Structure and migrates it to the new Structure. The
   *          starting point of the new Structure is passed in as the second parameter, and must be returned as the
   *          result.
   */
  public void addMigration(Scope pScope, String pStructureDefinitionName, int pFromRevision, int pToRevision,
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
  public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
    Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision);

  /**
   * Returns the latest revision of a given structure definition name
   *
   * @param pScope the scope
   * @param pDefName the definition name
   * @return the revision (or null if the definition doesn't exist)
   */
  public @Nullable Integer lookupLatestStructureDefinitionRevision(Scope pScope, String pDefName);
}

package com.diamondq.common.model.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The toolkit represents the top of the model chain. Get access to a Toolkit via the {@link ToolkitFactory}.
 */
public interface Toolkit {

	/**
	 * Returns the complete list of scopes that are supported by this toolkit
	 * 
	 * @return the scopes
	 */
	@Nonnull
	public Collection<Scope> getAllScopes();

	/**
	 * Returns a scope with a given name
	 * 
	 * @param pName the name
	 * @return the Scope or null if there is no scope by that name
	 */
	@Nullable
	public Scope getScope(@Nonnull String pName);

	/**
	 * Returns a scope with a given name
	 * 
	 * @param pName the name
	 * @return the Scope (the existing one or a new one will be created)
	 */
	@Nonnull
	public Scope getOrCreateScope(@Nonnull String pName);

	/**
	 * Returns the complete set of StructureDefinitions (references).
	 * 
	 * @param pScope the scope
	 * @return the set
	 */
	@Nonnull
	public Collection<StructureDefinitionRef> getAllStructureDefinitionRefs(@Nonnull Scope pScope);

	/**
	 * Creates a new StructureDefinition that is not yet persisted.
	 * 
	 * @param pScope the scope
	 * @param pName the name for the StructureDefinition
	 * @return the blank StructureDefinition
	 */
	@Nonnull
	public StructureDefinition createNewStructureDefinition(@Nonnull Scope pScope, @Nonnull String pName);

	/**
	 * Writes a StructureDefinition back to persistent storage. Future queries will return the persisted value, but
	 * existing StructureDefinition's will not be automatically updated.
	 * 
	 * @param pScope the scope
	 * @param pValue the StructureDefinition to write
	 */
	public void writeStructureDefinition(@Nonnull Scope pScope, @Nonnull StructureDefinition pValue);

	/**
	 * Deletes an existing StructureDefinition from permanent storage.
	 * 
	 * @param pScope the scope
	 * @param pValue the StructureDefinition to delete.
	 */
	public void deleteStructureDefinition(@Nonnull Scope pScope, @Nonnull StructureDefinition pValue);

	/**
	 * Creates a reference for a StructureDefinition
	 * 
	 * @param pScope the scope
	 * @param pResolvable the StructureDefinition
	 * @return the reference
	 */
	@Nonnull
	public StructureDefinitionRef createStructureDefinitionRef(@Nonnull Scope pScope,
		@Nonnull StructureDefinition pResolvable);

	/**
	 * Creates a reference for a Structure
	 * 
	 * @param pScope the scope
	 * @param pResolvable the Structure
	 * @return the reference
	 */
	@Nonnull
	public StructureRef createStructureRef(@Nonnull Scope pScope, @Nonnull Structure pResolvable);

	/**
	 * Creates a reference string for a Structure
	 * 
	 * @param pScope the scope
	 * @param pResolvable the Structure
	 * @return the reference string
	 */
	@Nonnull
	public String createStructureRefStr(@Nonnull Scope pScope, @Nonnull Structure pResolvable);

	/**
	 * Creates a reference for a PropertyDefinition
	 * 
	 * @param pScope the scope
	 * @param pResolvable the PropertyDefinition
	 * @param pContaining the containing StructureDefinition
	 * @return the reference
	 */
	@Nonnull
	public PropertyDefinitionRef createPropertyDefinitionRef(@Nonnull Scope pScope,
		@Nonnull PropertyDefinition pResolvable, @Nonnull StructureDefinition pContaining);

	/**
	 * Creates a reference for a Property
	 * 
	 * @param pScope the scope
	 * @param pResolvable the Property (can be null for a 'loose' PropertyDefinitionRef)
	 * @param pContaining the containing Structure
	 * @return the reference
	 */
	@Nonnull
	public <T> PropertyRef<T> createPropertyRef(@Nonnull Scope pScope, @Nullable Property<T> pResolvable,
		@Nonnull Structure pContaining);

	/**
	 * Looks up a StructureDefinition by name
	 * 
	 * @param pScope the scope
	 * @param pName the name
	 * @return the StructureDefinition or null
	 */
	@Nullable
	public StructureDefinition lookupStructureDefinitionByName(@Nonnull Scope pScope, @Nonnull String pName);

	/**
	 * Creates a new blank PropertyDefinition.
	 * 
	 * @param pScope the scope
	 * @return the PropertyDefinition, never null.
	 */
	@Nonnull
	public PropertyDefinition createNewPropertyDefinition(@Nonnull Scope pScope);

	/**
	 * Given a list of primary keys, collapse it into a single name. This usually just returns a String separated by a
	 * separator such as '$'. NOTE: It also usually munges the primary keys to guarantee the ability to separate the
	 * pieces again. There is no standard for the format. You must use the same 'toolkit' to pull apart a primary key.
	 * 
	 * @param pScope the scope
	 * @param pNames the names
	 * @return the collapsed primary key
	 */
	@Nonnull
	public String collapsePrimaryKeys(@Nonnull Scope pScope, @Nonnull List<Object> pNames);

	/**
	 * Looks up a Structure by the full serialized reference string. This is actually called by the
	 * {@link StructureRef#resolve()}.
	 * 
	 * @param pScope the scope
	 * @param pSerializedRef the serialized reference string (generally created from
	 *            {@link StructureRef#getSerializedString()}
	 * @return the Structure or null
	 */
	@Nullable
	public Structure lookupStructureBySerializedRef(@Nonnull Scope pScope, @Nonnull String pSerializedRef);

	/**
	 * Creates a new Structure given a StructureDefinition. NOTE: The StructureDefinition must be in the same scope as
	 * that provided. NOTE: The new Structure is NOT automatically written to the toolkit.
	 * 
	 * @param pScope the scope
	 * @param pStructureDefinition the StructureDefinition to use
	 * @return the new, empty, Structure
	 */
	@Nonnull
	public Structure createNewStructure(@Nonnull Scope pScope, @Nonnull StructureDefinition pStructureDefinition);

	/**
	 * Writes a Structure to the persistence layer.
	 * 
	 * @param pScope the scope
	 * @param pStructure the Structure
	 */
	public void writeStructure(@Nonnull Scope pScope, @Nonnull Structure pStructure);

	/**
	 * Deletes an existing Structure from permanent storage.
	 * 
	 * @param pScope the scope
	 * @param pValue the Structure to delete.
	 */
	public void deleteStructure(@Nonnull Scope pScope, @Nonnull Structure pValue);

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
	@Nonnull
	public <TYPE> Property<TYPE> createNewProperty(@Nonnull Scope pScope,
		@Nonnull PropertyDefinition pPropertyDefinition, boolean isValueSet, TYPE pValue);

	/**
	 * Creates a new TranslatableString object for the given key
	 * 
	 * @param pScope the scope
	 * @param pKey the key
	 * @return the TranslatableString
	 */
	@Nonnull
	public TranslatableString createNewTranslatableString(@Nonnull Scope pScope, @Nonnull String pKey);

	/**
	 * Lookup for existing EditorStructureDefinition's that correspond to the provided StructureDefinition.
	 * 
	 * @param pScope the scope
	 * @param pRef the reference to the StructureDefinition of interest
	 * @return the list of EditorStructureDefinition's in descending order of priority. At minimum, will include a
	 *         generic editor structure definition.
	 */
	@Nonnull
	public List<EditorStructureDefinition> lookupEditorStructureDefinitionByRef(@Nonnull Scope pScope,
		@Nonnull StructureDefinitionRef pRef);

	/**
	 * Creates a new EditorGroupDefinition. NOTE: It is not attached to any parent upon creation.
	 * 
	 * @param pScope the scope
	 * @return the new EditorGroupDefinition
	 */
	@Nonnull
	public EditorGroupDefinition createNewEditorGroupDefinition(@Nonnull Scope pScope);

	/**
	 * Creates a new EditorPropertyDefinition. NOTE: It is not attached to any parent upon creation.
	 * 
	 * @param pScope the scope
	 * @return the new EditorPropertyDefinition
	 */
	@Nonnull
	public EditorPropertyDefinition createNewEditorPropertyDefinition(@Nonnull Scope pScope);

	/**
	 * Creates a new EditorStructureDefinition. NOTE: It is not persisted upon creation.
	 * 
	 * @param pScope the scope
	 * @param pName the name
	 * @param pRef the reference
	 * @return the new EditorStructureDefinition
	 */
	public EditorStructureDefinition createNewEditorStructureDefinition(@Nonnull Scope pScope, @Nonnull String pName,
		@Nonnull StructureDefinitionRef pRef);

	/**
	 * Writes an EditorStructureDefintion to persistent storage.
	 * 
	 * @param pScope the scope
	 * @param pEditorStructureDefinition the object to write
	 */
	public void writeEditorStructureDefinition(@Nonnull Scope pScope,
		@Nonnull EditorStructureDefinition pEditorStructureDefinition);

	/**
	 * Deletes an existing EditorStructureDefinition from permanent storage.
	 * 
	 * @param pScope the scope
	 * @param pValue the EditorStructureDefinition to delete.
	 */
	public void deleteEditorStructureDefinition(@Nonnull Scope pScope, @Nonnull EditorStructureDefinition pValue);

	/**
	 * Creates a new StructureRef from a serialized string
	 * 
	 * @param pScope
	 * @param pValue the serialized string
	 * @return the StructureRef
	 */
	@Nonnull
	public StructureRef createStructureRefFromSerialized(@Nonnull Scope pScope, @Nonnull String pValue);

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
	@Nonnull
	public StructureRef createStructureRefFromParts(@Nonnull Scope pScope, @Nullable Structure pStructure,
		@Nullable String pPropName, @Nullable StructureDefinition pDef, List<Object> pPrimaryKeys);

	/**
	 * Creates a new PropertyRef from a serialized string
	 * 
	 * @param pScope the scope
	 * @param pValue the serialized string
	 * @return the PropertyRef
	 */
	@Nonnull
	public <T> PropertyRef<T> createPropertyRefFromSerialized(@Nonnull Scope pScope, @Nonnull String pValue);

	@Nonnull
	public Collection<Structure> getAllStructuresByDefinition(@Nonnull Scope pScope,
		@Nonnull StructureDefinitionRef pRef);

	/**
	 * Looks up a resource string under the given scope, locale and key
	 * 
	 * @param pScope the scope
	 * @param pLocale the locale. If null, then the 'default' is used
	 * @param pKey the key
	 * @return the matching resource string or null if there is none anywhere.
	 */
	@Nullable
	public String lookupResourceString(@Nonnull Scope pScope, @Nullable Locale pLocale, @Nonnull String pKey);

	/**
	 * Sets the global default locale (applies to all threads that have not specifically been set)
	 * 
	 * @param pScope the scope or if null, then it applies to all existing scopes (but not to-be created ones)
	 * @param pLocale the locale
	 */
	public void setGlobalDefaultLocale(@Nullable Scope pScope, @Nonnull Locale pLocale);

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
	public boolean isResourceStringWritingSupported(@Nonnull Scope pScope);

	/**
	 * This writes a new resource string to the scope. NOTE: Not all scopes will support this. Check with
	 * {@link Toolkit#isResourceStringWritingSupported(Scope)} before calling or risk an exception.
	 * 
	 * @param pScope the scope
	 * @param pLocale the locale
	 * @param pKey the key
	 * @param pValue the value
	 */
	public void writeResourceString(@Nonnull Scope pScope, @Nonnull Locale pLocale, String pKey, String pValue);

	/**
	 * This deletes an existing resource string from the scope. NOTE: Not all scopes will support this. Check with
	 * {@link #isResourceStringWritingSupported(Scope)} before calling or risk an exception.
	 * 
	 * @param pScope the scope
	 * @param pLocale the locale
	 * @param pKey the key
	 */
	public void deleteResourceString(@Nonnull Scope pScope, @Nonnull Locale pLocale, String pKey);

	/**
	 * Returns the set of key/value pairs for a given locale. This will not do any form of fallbacks, and will only
	 * return the key/values for the exact locale.
	 * 
	 * @param pScope the scope
	 * @param pLocale the locale
	 * @return the set of key/value pairs, never null
	 */
	@Nonnull
	public Map<String, String> getResourceStringsByLocale(@Nonnull Scope pScope, @Nonnull Locale pLocale);

	/**
	 * Returns the set of Locales that have ResourceStrings assigned to them
	 * 
	 * @param pScope the scope
	 * @return the set of Locales, never null
	 */
	@Nonnull
	public Collection<Locale> getResourceStringLocales(@Nonnull Scope pScope);

	/**
	 * Creates a new empty QueryBuilder object.
	 * 
	 * @param pScope the scope
	 * @return the QueryBuilder
	 */
	public QueryBuilder createNewQueryBuilder(@Nonnull Scope pScope);

	public List<Structure> lookupStructuresByQuery(Scope pScope, StructureDefinition pStructureDefinition,
		QueryBuilder pBuilder, Map<String, Object> pParamValues);

}

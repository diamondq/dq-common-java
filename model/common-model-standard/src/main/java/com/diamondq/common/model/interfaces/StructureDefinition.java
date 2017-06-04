package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface StructureDefinition extends Resolvable<StructureDefinition, StructureDefinitionRef> {

	/* Name */

	/**
	 * Returns the unique, primary identifier of this StructureDefinition. All StructureDefinitions exist in a 'flat'
	 * namespace, and must have a unique name.
	 * 
	 * @return the name
	 */
	@Nonnull
	public String getName();

	/* Label */

	@Nullable
	public TranslatableString getLabel();

	@CheckReturnValue
	@Nonnull
	public StructureDefinition setLabel(@Nullable TranslatableString pValue);

	/* Single Instance */

	public boolean isSingleInstance();

	@CheckReturnValue
	@Nonnull
	public StructureDefinition setSingleInstance(boolean pValue);

	/* Properties */

	@Nonnull
	public Map<String, PropertyDefinition> getPropertyDefinitions();

	@CheckReturnValue
	@Nonnull
	public StructureDefinition addPropertyDefinition(@Nonnull PropertyDefinition pValue);

	@CheckReturnValue
	@Nonnull
	public StructureDefinition removePropertyDefinition(@Nonnull PropertyDefinition pValue);

	/* Parents */

	/**
	 * Returns the set of StructureDefinition References that represent the set of parents whose Properties are merged
	 * together to make the complete set of Properties within this StructureDefinition.
	 * 
	 * @return the set of StructureDefinitionRef's
	 */
	@Nonnull
	public Set<StructureDefinitionRef> getParentDefinitions();

	@CheckReturnValue
	@Nonnull
	public StructureDefinition addParentDefinition(@Nonnull StructureDefinitionRef pValue);

	@CheckReturnValue
	@Nonnull
	public StructureDefinition removeParentDefinition(@Nonnull StructureDefinitionRef pValue);

	/* Complete Properties (virtual) */

	/**
	 * The complete set of property definitions achieved by merging the current PropertyDefinition's on this
	 * StructureDefinition with the set of PropertyDefinitions on all parent StructureDefinitions (and their parents,
	 * and their parents, and so on). <br/>
	 * <br/>
	 * NOTE: That while the overall StructureDefinition is an immutable data structure, this map is not, as this
	 * StructureDefinition only holds StructureDefinitionRef's and not StructureDefinitions of their parents, and thus,
	 * those parents can change independently of this StructureDefinition. Thus, each call to getAllProperties may
	 * return a different map. The map returned, however, is still immutable.
	 * 
	 * @return a Map of PropertyDefinitions using their name as the key
	 */
	@Nonnull
	public Map<String, PropertyDefinition> getAllProperties();

	@Nullable
	public PropertyDefinition lookupPropertyDefinitionByName(@Nonnull String pName);

	/**
	 * Returns back the set of PropertyDefinition names that match the given keyword
	 * 
	 * @param pKey the key
	 * @param pValue the value (or null if any value is valid)
	 * @param pType the type (or null if any type is valid)
	 * @return the collection of PropertyDefinition name's
	 */
	@Nullable
	public Collection<String> lookupPropertyDefinitionNamesByKeyword(@Nonnull String pKey, @Nullable String pValue,
		@Nullable PropertyType pType);

	/**
	 * Returns the ordered list of names of PropertyDescriptions that represent the primary key for this
	 * StructureDefinition.
	 * 
	 * @return the list, may be empty, but not null
	 */
	@Nonnull
	public List<String> lookupPrimaryKeyNames();

	/**
	 * Creates a new Structure from this StructureDefinition. NOTE: This Structure is NOT automatically written to the
	 * toolkit.
	 * 
	 * @return the new, empty, Structure
	 */
	@Nonnull
	public Structure createNewStructure();

	/* keywords */

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs). NOTE: This map only includes those keywords attached to
	 * this specific StructureDefinition and not any of the parents.
	 * 
	 * @return the multimap
	 */
	@Nonnull
	public Multimap<String, String> getKeywords();

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs). NOTE: This map includes merged keywords between this
	 * StructureDefinition and all parents (recursively).
	 * 
	 * @return the multimap
	 */
	@Nonnull
	public Multimap<String, String> getAllKeywords();

	/**
	 * Adds a new keyword to this StructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated StructureDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public StructureDefinition addKeyword(@Nonnull String pKey, @Nonnull String pValue);

	/**
	 * Removes a keyword from this StructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated StructureDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public StructureDefinition removeKeyword(@Nonnull String pKey, @Nonnull String pValue);

}

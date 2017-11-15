package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface StructureDefinition extends Resolvable<StructureDefinition, StructureDefinitionRef> {

	/* Name */

	/**
	 * Returns the unique, primary identifier of this StructureDefinition. All StructureDefinitions exist in a 'flat'
	 * namespace, and must have a unique name.
	 *
	 * @return the name
	 */

	public String getName();

	/* Revision */

	public int getRevision();

	/* Label */

	public @Nullable TranslatableString getLabel();

	public StructureDefinition setLabel(@Nullable TranslatableString pValue);

	/* Single Instance */

	public boolean isSingleInstance();

	public StructureDefinition setSingleInstance(boolean pValue);

	/* Properties */

	public Map<String, PropertyDefinition> getPropertyDefinitions();

	public StructureDefinition addPropertyDefinition(PropertyDefinition pValue);

	public StructureDefinition removePropertyDefinition(PropertyDefinition pValue);

	/* Parents */

	/**
	 * Returns the set of StructureDefinition References that represent the set of parents whose Properties are merged
	 * together to make the complete set of Properties within this StructureDefinition.
	 *
	 * @return the set of StructureDefinitionRef's
	 */

	public Set<StructureDefinitionRef> getParentDefinitions();

	public StructureDefinition addParentDefinition(StructureDefinitionRef pValue);

	public StructureDefinition removeParentDefinition(StructureDefinitionRef pValue);

	/**
	 * Returns a reference to this StructureDefinition without the revision. This can then be used to match against
	 * multiple revisions of this StructureDefinition (and usually the latest).
	 *
	 * @return the reference
	 */
	public StructureDefinitionRef getWildcardReference();

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

	public Map<String, PropertyDefinition> getAllProperties();

	public @Nullable PropertyDefinition lookupPropertyDefinitionByName(String pName);

	/**
	 * Returns back the set of PropertyDefinition names that match the given keyword
	 *
	 * @param pKey the key
	 * @param pValue the value (or null if any value is valid)
	 * @param pType the type (or null if any type is valid)
	 * @return the collection of PropertyDefinition name's
	 */
	public Collection<String> lookupPropertyDefinitionNamesByKeyword(String pKey, @Nullable String pValue,
		@Nullable PropertyType pType);

	/**
	 * Returns the ordered list of names of PropertyDescriptions that represent the primary key for this
	 * StructureDefinition.
	 *
	 * @return the list, may be empty, but not null
	 */
	public List<String> lookupPrimaryKeyNames();

	/**
	 * Creates a new Structure from this StructureDefinition. NOTE: This Structure is NOT automatically written to the
	 * toolkit.
	 *
	 * @return the new, empty, Structure
	 */
	public Structure createNewStructure();

	/* keywords */

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs). NOTE: This map only includes those keywords attached to
	 * this specific StructureDefinition and not any of the parents.
	 *
	 * @return the multimap
	 */
	public Multimap<String, String> getKeywords();

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs). NOTE: This map includes merged keywords between this
	 * StructureDefinition and all parents (recursively).
	 *
	 * @return the multimap
	 */
	public Multimap<String, String> getAllKeywords();

	/**
	 * Adds a new keyword to this StructureDefinition
	 *
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated StructureDefinition
	 */
	public StructureDefinition addKeyword(String pKey, String pValue);

	/**
	 * Removes a keyword from this StructureDefinition
	 *
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated StructureDefinition
	 */
	public StructureDefinition removeKeyword(String pKey, String pValue);

}

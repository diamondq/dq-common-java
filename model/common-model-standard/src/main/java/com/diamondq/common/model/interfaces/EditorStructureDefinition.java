package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface EditorStructureDefinition {

	/* Name */
	
	@Nonnull
	public String getName();

	/* StructureType */

	/**
	 * Returns the StructureDefinition reference that is supported by this view.
	 * 
	 * @return the reference, never null
	 */
	@Nonnull
	public StructureDefinitionRef getStructureDefinitionRef();

	/* components */

	@CheckReturnValue
	@Nonnull
	public List<? extends EditorComponentDefinition<?>> getComponents();

	@CheckReturnValue
	@Nonnull
	public <T extends EditorComponentDefinition<T>> EditorStructureDefinition addComponent(@Nonnull T pValue);

	@CheckReturnValue
	@Nonnull
	public <T extends EditorComponentDefinition<T>> EditorStructureDefinition removeComponent(@Nonnull T pValue);

	/* keywords */

	/**
	 * Returns the Multimap of keywords (ie. key=value pairs).
	 * 
	 * @return the multimap
	 */
	@Nonnull
	public Multimap<String, String> getKeywords();

	/**
	 * Adds a new keyword to this EditorStructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated EditorStructureDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public EditorStructureDefinition addKeyword(@Nonnull String pKey, @Nonnull String pValue);

	/**
	 * Removes a keyword from this EditorStructureDefinition
	 * 
	 * @param pKey the key
	 * @param pValue the value
	 * @return the updated EditorStructureDefinition
	 */
	@CheckReturnValue
	@Nonnull
	public EditorStructureDefinition removeKeyword(@Nonnull String pKey, @Nonnull String pValue);

}

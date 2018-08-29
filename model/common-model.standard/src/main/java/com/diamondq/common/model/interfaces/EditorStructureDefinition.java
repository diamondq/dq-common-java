package com.diamondq.common.model.interfaces;

import com.google.common.collect.Multimap;

import java.util.List;

public interface EditorStructureDefinition {

  /* Name */

  public String getName();

  /* StructureType */

  /**
   * Returns the StructureDefinition reference that is supported by this view.
   * 
   * @return the reference, never null
   */

  public StructureDefinitionRef getStructureDefinitionRef();

  /* components */

  public List<? extends EditorComponentDefinition<?>> getComponents();

  public <T extends EditorComponentDefinition<T>> EditorStructureDefinition addComponent(T pValue);

  public <T extends EditorComponentDefinition<T>> EditorStructureDefinition removeComponent(T pValue);

  /* keywords */

  /**
   * Returns the Multimap of keywords (ie. key=value pairs).
   * 
   * @return the multimap
   */
  public Multimap<String, String> getKeywords();

  /**
   * Adds a new keyword to this EditorStructureDefinition
   * 
   * @param pKey the key
   * @param pValue the value
   * @return the updated EditorStructureDefinition
   */
  public EditorStructureDefinition addKeyword(String pKey, String pValue);

  /**
   * Removes a keyword from this EditorStructureDefinition
   * 
   * @param pKey the key
   * @param pValue the value
   * @return the updated EditorStructureDefinition
   */
  public EditorStructureDefinition removeKeyword(String pKey, String pValue);

}

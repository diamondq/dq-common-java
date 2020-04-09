package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorComponentDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.List;
import java.util.Map.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GenericEditorStructureDefinition implements EditorStructureDefinition {

  private final String                                       mName;

  private final StructureDefinitionRef                       mStructureDefinitionRef;

  private final List<? extends EditorComponentDefinition<?>> mComponents;

  private final ImmutableMultimap<String, String>            mKeywords;

  public GenericEditorStructureDefinition(String pName, StructureDefinitionRef pStructureDefinitionRef,
    @Nullable List<? extends EditorComponentDefinition<?>> pComponents, @Nullable Multimap<String, String> pKeywords) {
    super();
    assert (pName != null) && (pStructureDefinitionRef != null);
    mName = pName;
    mStructureDefinitionRef = pStructureDefinitionRef;
    mComponents = pComponents == null ? ImmutableList.of() : ImmutableList.copyOf(pComponents);
    mKeywords = pKeywords == null ? ImmutableMultimap.of() : ImmutableMultimap.copyOf(pKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#getStructureDefinitionRef()
   */
  @Override
  public StructureDefinitionRef getStructureDefinitionRef() {
    return mStructureDefinitionRef;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#getComponents()
   */
  @Override
  public List<? extends EditorComponentDefinition<?>> getComponents() {
    return mComponents;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#addComponent(com.diamondq.common.model.interfaces.EditorComponentDefinition)
   */
  @Override
  public <T extends EditorComponentDefinition<T>> EditorStructureDefinition addComponent(T pValue) {
    @SuppressWarnings("null")
    @NonNull
    Predicate<EditorComponentDefinition<?>> equalTo = Predicates.equalTo(pValue);
    return new GenericEditorStructureDefinition(mName, mStructureDefinitionRef,
      ImmutableList.<EditorComponentDefinition<?>> builder()
        .addAll(Iterables.filter(mComponents, Predicates.not(equalTo))).add(pValue).build(),
      mKeywords);
  }

  @Override
  public <T extends EditorComponentDefinition<T>> EditorStructureDefinition removeComponent(T pValue) {
    @SuppressWarnings("null")
    @NonNull Predicate<EditorComponentDefinition<?>> equalTo = Predicates.equalTo(pValue);
    return new GenericEditorStructureDefinition(mName, mStructureDefinitionRef,
      ImmutableList.<EditorComponentDefinition<?>> builder()
        .addAll(Iterables.filter(mComponents, Predicates.not(equalTo))).build(),
      mKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#getKeywords()
   */
  @Override
  public Multimap<String, String> getKeywords() {
    return mKeywords;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#addKeyword(java.lang.String, java.lang.String)
   */
  @Override
  public EditorStructureDefinition addKeyword(String pKey, String pValue) {
    return new GenericEditorStructureDefinition(mName, mStructureDefinitionRef, mComponents,
      ImmutableMultimap.<String, String> builder()
        .putAll(Multimaps.filterEntries(mKeywords,
          Predicates.<Entry<String, String>> not(
            (e) -> (e != null) && pKey.equals(e.getKey()) && pValue.equals(e.getValue()))))
        .put(pKey, pValue).build());
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#removeKeyword(java.lang.String, java.lang.String)
   */
  @Override
  public EditorStructureDefinition removeKeyword(String pKey, String pValue) {
    return new GenericEditorStructureDefinition(mName, mStructureDefinitionRef, mComponents,
      Multimaps.filterEntries(mKeywords, Predicates
        .<Entry<String, String>> not((e) -> (e != null) && pKey.equals(e.getKey()) && pValue.equals(e.getValue()))));
  }

}

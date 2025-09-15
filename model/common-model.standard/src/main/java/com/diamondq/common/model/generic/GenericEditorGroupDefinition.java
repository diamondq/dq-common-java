package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorComponentDefinition;
import com.diamondq.common.model.interfaces.EditorComponentDirection;
import com.diamondq.common.model.interfaces.EditorGroupDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GenericEditorGroupDefinition extends GenericEditorComponentDefinition<EditorGroupDefinition>
  implements EditorGroupDefinition {

  private final @Nullable EditorComponentDirection mDirection;

  private final int mNumColumns;

  private final List<EditorComponentDefinition<?>> mComponents;

  public GenericEditorGroupDefinition(@Nullable TranslatableString pLabel, int pColumn, int pColumnSpan, int pOrder,
    @Nullable PropertyDefinitionRef pVisibleIfProperty, @Nullable Set<String> pVisibleIfValueEquals,
    @Nullable EditorComponentDirection pDirection, int pNumColumns,
    @Nullable List<EditorComponentDefinition<?>> pComponents) {
    super(pLabel, pColumn, pColumnSpan, pOrder, pVisibleIfProperty, pVisibleIfValueEquals);
    mDirection = pDirection;
    mNumColumns = pNumColumns;
    pComponents = pComponents == null ? Collections.emptyList() : pComponents;
    Collections.sort(pComponents, (a, b) -> {
        return a.getOrder() - b.getOrder();
      }
    );
    mComponents = ImmutableList.copyOf(pComponents);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#getDirection()
   */
  @Override
  public @Nullable EditorComponentDirection getDirection() {
    return mDirection;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#setDirection(com.diamondq.common.model.interfaces.EditorComponentDirection)
   */
  @Override
  public EditorGroupDefinition setDirection(@Nullable EditorComponentDirection pValue) {
    return new GenericEditorGroupDefinition(mLabel,
      mColumn,
      mColumnSpan,
      mOrder,
      mVisibleIfProperty,
      mVisibleIfValueEquals,
      pValue,
      mNumColumns,
      mComponents
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#getNumColumns()
   */
  @Override
  public int getNumColumns() {
    return mNumColumns;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#setNumColumns(int)
   */
  @Override
  public EditorGroupDefinition setNumColumns(int pValue) {
    return new GenericEditorGroupDefinition(mLabel,
      mColumn,
      mColumnSpan,
      mOrder,
      mVisibleIfProperty,
      mVisibleIfValueEquals,
      mDirection,
      pValue,
      mComponents
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#getComponents()
   */
  @Override
  public List<? extends EditorComponentDefinition<?>> getComponents() {
    return mComponents;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#addComponent(com.diamondq.common.model.interfaces.EditorComponentDefinition)
   */
  @Override
  public <T extends EditorComponentDefinition<T>> EditorGroupDefinition addComponent(T pValue) {
    return new GenericEditorGroupDefinition(mLabel,
      mColumn,
      mColumnSpan,
      mOrder,
      mVisibleIfProperty,
      mVisibleIfValueEquals,
      mDirection,
      mNumColumns,
      ImmutableList.<EditorComponentDefinition<?>>builder().addAll(mComponents).add(pValue).build()
    );
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorGroupDefinition#removeComponent(com.diamondq.common.model.interfaces.EditorComponentDefinition)
   */
  @Override
  public <T extends EditorComponentDefinition<T>> EditorGroupDefinition removeComponent(T pValue) {
    Predicate<EditorComponentDefinition<?>> equalTo = Predicates.equalTo(pValue);
    return new GenericEditorGroupDefinition(mLabel,
      mColumn,
      mColumnSpan,
      mOrder,
      mVisibleIfProperty,
      mVisibleIfValueEquals,
      mDirection,
      mNumColumns,
      ImmutableList.copyOf(Iterables.filter(mComponents, Predicates.not(equalTo)))
    );
  }

  /**
   * @see com.diamondq.common.model.generic.GenericEditorComponentDefinition#constructNew(com.diamondq.common.model.interfaces.TranslatableString,
   *   int, int, int, com.diamondq.common.model.interfaces.PropertyDefinitionRef, java.util.Set)
   */
  @Override
  protected EditorGroupDefinition constructNew(@Nullable TranslatableString pLabel, int pColumn, int pColumnSpan,
    int pOrder, @Nullable PropertyDefinitionRef pVisibleIfProperty, @Nullable Set<String> pVisibleIfValueEquals) {
    return new GenericEditorGroupDefinition(pLabel,
      pColumn,
      pColumnSpan,
      pOrder,
      pVisibleIfProperty,
      pVisibleIfValueEquals,
      mDirection,
      mNumColumns,
      mComponents
    );
  }

}

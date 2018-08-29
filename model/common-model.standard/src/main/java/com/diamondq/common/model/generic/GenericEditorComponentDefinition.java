package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.EditorComponentDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class GenericEditorComponentDefinition<T extends EditorComponentDefinition<T>>
  implements EditorComponentDefinition<T> {

  protected final @Nullable TranslatableString    mLabel;

  protected final int                             mColumn;

  protected final int                             mColumnSpan;

  protected final int                             mOrder;

  protected final @Nullable PropertyDefinitionRef mVisibleIfProperty;

  protected final @Nullable Set<String>           mVisibleIfValueEquals;

  protected GenericEditorComponentDefinition(@Nullable TranslatableString pLabel, int pColumn, int pColumnSpan,
    int pOrder, @Nullable PropertyDefinitionRef pVisibleIfProperty, @Nullable Set<String> pVisibleIfValueEquals) {
    super();
    mLabel = pLabel;
    mColumn = pColumn;
    mColumnSpan = pColumnSpan;
    mOrder = pOrder;
    mVisibleIfProperty = pVisibleIfProperty;
    mVisibleIfValueEquals = pVisibleIfValueEquals == null ? null : ImmutableSet.copyOf(pVisibleIfValueEquals);
  }

  protected abstract T constructNew(@Nullable TranslatableString pLabel, int pColumn, int pColumnSpan, int pOrder,
    @Nullable PropertyDefinitionRef pVisibleIfProperty, @Nullable Set<String> pVisibleIfValueEquals);

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#getLabel()
   */
  @Override
  public @Nullable TranslatableString getLabel() {
    return mLabel;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#setLabel(com.diamondq.common.model.interfaces.TranslatableString)
   */
  @Override
  public T setLabel(@Nullable TranslatableString pValue) {
    return constructNew(pValue, mColumn, mColumnSpan, mOrder, mVisibleIfProperty, mVisibleIfValueEquals);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#getColumn()
   */
  @Override
  public int getColumn() {
    return mColumn;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#setColumn(int)
   */
  @Override
  public T setColumn(int pValue) {
    return constructNew(mLabel, pValue, mColumnSpan, mOrder, mVisibleIfProperty, mVisibleIfValueEquals);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#getColumnSpan()
   */
  @Override
  public int getColumnSpan() {
    return mColumnSpan;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#setColumnSpan(int)
   */
  @Override
  public T setColumnSpan(int pValue) {
    return constructNew(mLabel, mColumn, pValue, mOrder, mVisibleIfProperty, mVisibleIfValueEquals);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#getOrder()
   */
  @Override
  public int getOrder() {
    return mOrder;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#setOrder(int)
   */
  @Override
  public T setOrder(int pValue) {
    return constructNew(mLabel, mColumn, mColumnSpan, pValue, mVisibleIfProperty, mVisibleIfValueEquals);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#getVisibleIfProperty()
   */
  @Override
  public @Nullable PropertyDefinitionRef getVisibleIfProperty() {
    return mVisibleIfProperty;
  }

  @Override
  public T setVisibleIfProperty(@Nullable PropertyDefinitionRef pValue) {
    return constructNew(mLabel, mColumn, mColumnSpan, mOrder, pValue, mVisibleIfValueEquals);
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#getVisibleIfValueEquals()
   */
  @Override
  public @Nullable Set<String> getVisibleIfValueEquals() {
    return mVisibleIfValueEquals;
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#addVisibleIfValueEquals(java.lang.String)
   */
  @Override
  public T addVisibleIfValueEquals(String pValue) {
    return constructNew(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
      ImmutableSet.<String> builder()
        .addAll(Sets.filter(mVisibleIfValueEquals == null ? Collections.emptySet() : mVisibleIfValueEquals,
          Predicates.not(Predicates.equalTo(pValue))))
        .add(pValue).build());
  }

  /**
   * @see com.diamondq.common.model.interfaces.EditorComponentDefinition#removeVisibleIfValueEquals(java.lang.String)
   */
  @Override
  public T removeVisibleIfValueEquals(String pValue) {
    return constructNew(mLabel, mColumn, mColumnSpan, mOrder, mVisibleIfProperty,
      Sets.filter(mVisibleIfValueEquals == null ? Collections.emptySet() : mVisibleIfValueEquals,
        Predicates.not(Predicates.equalTo(pValue))));
  }
}
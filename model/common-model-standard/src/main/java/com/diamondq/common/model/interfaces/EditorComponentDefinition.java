package com.diamondq.common.model.interfaces;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface EditorComponentDefinition<T extends EditorComponentDefinition<T>> {

	/* label */

	public @Nullable TranslatableString getLabel();

	public T setLabel(@Nullable TranslatableString pValue);

	/* Column */

	/**
	 * The column number for this component. Columns start at 0.
	 * 
	 * @return the column number
	 */
	public int getColumn();

	public T setColumn(int pValue);

	/* Column Span */

	public int getColumnSpan();

	public T setColumnSpan(int pValue);

	/* Order */

	public int getOrder();

	public T setOrder(int pValue);

	/* VisibleIfProperty */

	public @Nullable PropertyDefinitionRef getVisibleIfProperty();

	public T setVisibleIfProperty(@Nullable PropertyDefinitionRef pValue);

	/* VisibleIfValueEquals */

	public @Nullable Set<String> getVisibleIfValueEquals();

	public T addVisibleIfValueEquals(String pValue);

	public T removeVisibleIfValueEquals(String pValue);
}

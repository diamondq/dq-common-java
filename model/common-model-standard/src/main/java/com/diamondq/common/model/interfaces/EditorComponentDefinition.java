package com.diamondq.common.model.interfaces;

import java.util.Set;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EditorComponentDefinition<T extends EditorComponentDefinition<T>> {

	/* label */

	@Nullable
	public TranslatableString getLabel();

	@CheckReturnValue
	@Nonnull
	public T setLabel(@Nullable TranslatableString pValue);

	/* Column */

	/**
	 * The column number for this component. Columns start at 0.
	 * 
	 * @return the column number
	 */
	public int getColumn();

	@CheckReturnValue
	@Nonnull
	public T setColumn(int pValue);

	/* Column Span */

	public int getColumnSpan();

	@CheckReturnValue
	@Nonnull
	public T setColumnSpan(int pValue);

	/* Order */

	public int getOrder();

	@CheckReturnValue
	@Nonnull
	public T setOrder(int pValue);

	/* VisibleIfProperty */

	@Nullable
	public PropertyDefinitionRef getVisibleIfProperty();

	@CheckReturnValue
	@Nonnull
	public T setVisibleIfProperty(@Nullable PropertyDefinitionRef pValue);

	/* VisibleIfValueEquals */

	@Nonnull
	public Set<String> getVisibleIfValueEquals();

	@CheckReturnValue
	@Nonnull
	public T addVisibleIfValueEquals(@Nonnull String pValue);

	@CheckReturnValue
	@Nonnull
	public T removeVisibleIfValueEquals(@Nonnull String pValue);
}

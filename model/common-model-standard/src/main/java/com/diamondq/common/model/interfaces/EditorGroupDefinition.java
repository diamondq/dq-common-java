package com.diamondq.common.model.interfaces;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EditorGroupDefinition extends EditorComponentDefinition<EditorGroupDefinition> {

	/* direction */

	@Nullable
	public EditorComponentDirection getDirection();

	@CheckReturnValue
	@Nonnull
	public EditorGroupDefinition setDirection(@Nullable EditorComponentDirection pValue);

	/* numColumns */

	public int getNumColumns();

	@CheckReturnValue
	@Nonnull
	public EditorGroupDefinition setNumColumns(int pValue);

	/* components */

	@Nonnull
	public List<? extends EditorComponentDefinition<?>> getComponents();

	@CheckReturnValue
	@Nonnull
	public <T extends EditorComponentDefinition<T>> EditorGroupDefinition addComponent(@Nonnull T pValue);

	@CheckReturnValue
	@Nonnull
	public <T extends EditorComponentDefinition<T>> EditorGroupDefinition removeComponent(@Nonnull T pValue);

}

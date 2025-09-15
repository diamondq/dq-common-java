package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.List;

public interface EditorGroupDefinition extends EditorComponentDefinition<EditorGroupDefinition> {

  /* direction */

  public @Nullable EditorComponentDirection getDirection();

  public EditorGroupDefinition setDirection(@Nullable EditorComponentDirection pValue);

  /* numColumns */

  public int getNumColumns();

  public EditorGroupDefinition setNumColumns(int pValue);

  /* components */

  public List<? extends EditorComponentDefinition<?>> getComponents();

  public <T extends EditorComponentDefinition<T>> EditorGroupDefinition addComponent(T pValue);

  public <T extends EditorComponentDefinition<T>> EditorGroupDefinition removeComponent(T pValue);

}

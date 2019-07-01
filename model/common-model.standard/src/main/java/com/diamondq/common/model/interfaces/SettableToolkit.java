package com.diamondq.common.model.interfaces;

import com.diamondq.common.model.generic.PersistenceLayer;

public interface SettableToolkit extends Toolkit {

  /**
   * Adds the persistence layer for a given scope
   *
   * @param pScope the scope
   * @param pLayer the persistence layer
   */
  public void setPersistenceLayer(Scope pScope, PersistenceLayer pLayer);

  /**
   * Returns a PersistenceLayer or throws an exception explaining that the scope is unknown.
   *
   * @param pScope the scope
   * @return the persistence layer, never null.
   */
  public PersistenceLayer getPersistenceLayer(Scope pScope);

}

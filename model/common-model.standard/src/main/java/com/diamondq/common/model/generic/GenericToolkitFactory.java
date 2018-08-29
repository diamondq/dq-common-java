package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.ToolkitFactory;

public class GenericToolkitFactory extends ToolkitFactory {

  public GenericToolkitFactory() {
    super();
  }

  /**
   * @see com.diamondq.common.model.interfaces.ToolkitFactory#newToolkit()
   */
  @Override
  public Toolkit newToolkit() {
    return new GenericToolkit();
  }
}

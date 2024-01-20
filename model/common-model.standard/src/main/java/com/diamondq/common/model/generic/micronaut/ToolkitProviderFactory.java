package com.diamondq.common.model.generic.micronaut;

import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.ToolkitFactory;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

@Factory
public class ToolkitProviderFactory {

  @Singleton
  public Toolkit createToolkit() {
    ToolkitFactory factory = ToolkitFactory.newInstance();
    return factory.newToolkit();
  }
}

package com.diamondq.common.model.generic.micronaut;

import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.ToolkitFactory;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;

@Factory
public class ToolkitProviderFactory {

  @Singleton
  public Toolkit createToolkit() {
    ToolkitFactory factory = ToolkitFactory.newInstance();
    return factory.newToolkit();
  }
}

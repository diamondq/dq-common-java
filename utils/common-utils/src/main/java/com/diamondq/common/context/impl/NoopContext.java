package com.diamondq.common.context.impl;

import com.diamondq.common.context.spi.ContextClass;

public class NoopContext extends ContextClass {

  NoopContext(ContextFactoryImpl pFactory) {
    super(pFactory, null, NoopContext.class, null, false, null);
  }

}

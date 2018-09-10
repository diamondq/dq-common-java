package com.diamondq.common.utils.context.impl;

import com.diamondq.common.utils.context.spi.ContextClass;

public class NoopContext extends ContextClass {

  NoopContext(ContextFactoryImpl pFactory) {
    super(pFactory, NoopContext.class, null, false, null);
  }

}

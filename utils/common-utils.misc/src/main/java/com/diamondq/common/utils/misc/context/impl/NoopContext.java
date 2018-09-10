package com.diamondq.common.utils.misc.context.impl;

import com.diamondq.common.utils.misc.context.spi.ContextClass;

public class NoopContext extends ContextClass {

  NoopContext(ContextFactoryImpl pFactory) {
    super(pFactory, NoopContext.class, null, false, null);
  }

}

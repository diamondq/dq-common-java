package com.diamondq.common.utils.context.impl;

import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.context.impl.logging.LoggingContextHandler;

/**
 * This class is used for non-OSGi environments to get a ContextFactory
 */
public class ContextFactorySetup {

  private static boolean setup = false;

  public static ContextFactory setup() {
    synchronized (ContextFactorySetup.class) {
      if (setup == false) {

        /* Create a new instance */

        ContextFactoryImpl impl = new ContextFactoryImpl();

        /* Register the basic handlers */

        impl.addContextHandler(new LoggingContextHandler());

        /* Activate it */

        impl.onActivate();
        setup = true;
      }
      return ContextFactory.getInstance();
    }
  }

}

package com.diamondq.common.tracing.sentry;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.context.ContextManager;
import io.sentry.dsn.Dsn;

public class CustomSentryFactory extends DefaultSentryClientFactory {

  /**
   * @see io.sentry.DefaultSentryClientFactory#getContextManager(io.sentry.dsn.Dsn)
   */
  @Override
  protected ContextManager getContextManager(Dsn pDsn) {
    return new DQContextManager();
  }
}
package com.diamondq.common.tracing.sentry;

import io.sentry.context.Context;
import io.sentry.context.ContextManager;

public class DQContextManager implements ContextManager {

  /**
   * @see io.sentry.context.ContextManager#getContext()
   */
  @Override
  public Context getContext() {
    com.diamondq.common.context.Context dqContext = com.diamondq.common.context.ContextFactory.currentContext();
    Context sentryContext = dqContext.getData("sentry-context", true, Context.class);
    if (sentryContext == null) {
      synchronized (this) {
        sentryContext = dqContext.getData("sentry-context", true, Context.class);
        if (sentryContext == null) {
          sentryContext = new Context();
          dqContext.setData("sentry-context", sentryContext);
        }
      }
    }
    return sentryContext;
  }

  /**
   * @see io.sentry.context.ContextManager#clear()
   */
  @Override
  public void clear() {
    com.diamondq.common.context.Context dqContext = com.diamondq.common.context.ContextFactory.currentContext();
    dqContext.setData("sentry-context", new Context());
  }

}

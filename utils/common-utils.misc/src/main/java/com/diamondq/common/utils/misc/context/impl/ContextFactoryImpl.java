package com.diamondq.common.utils.misc.context.impl;

import com.diamondq.common.utils.misc.context.Context;
import com.diamondq.common.utils.misc.context.ContextFactory;
import com.diamondq.common.utils.misc.context.spi.ContextClass;
import com.diamondq.common.utils.misc.context.spi.ContextHandler;

import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ContextFactoryImpl implements ContextFactory {

  private final ContextClass                         mNOOP_CONTEXT        = new NoopContext(this);

  public static volatile ContextFactory              sINSTANCE            = new ContextFactoryImpl();

  private final CopyOnWriteArrayList<ContextHandler> mHandlers            = new CopyOnWriteArrayList<>();

  private final ThreadLocal<Stack<Context>>          mThreadLocalContexts =
    ThreadLocal.withInitial(() -> new Stack<>());

  public ContextFactoryImpl() {
  }

  /**
   * The onActivate is called when OSGi has finished initializing us.
   */
  public void onActivate() {

    /* Override the existing sINSTANCE with the fully configured one */

    sINSTANCE = this;
  }

  public void addContextHandler(ContextHandler pHandler) {
    mHandlers.addIfAbsent(pHandler);
  }

  public void removeContextHandler(ContextHandler pHandler) {
    mHandlers.remove(pHandler);
  }

  /**
   * @see com.diamondq.common.utils.misc.context.ContextFactory#newContextWithMeta(java.lang.Class, java.lang.Object,
   *      java.lang.Object[])
   */
  @Override
  public Context newContextWithMeta(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {

    /* Create a new context */

    ContextClass context = new ContextClass(this, pClass, pThis, true, pArgs);

    mThreadLocalContexts.get().push(context);

    /* Execute the start */

    for (ContextHandler handler : mHandlers)
      handler.executeOnContextStart(context);

    return context;
  }

  /**
   * @see com.diamondq.common.utils.misc.context.ContextFactory#newContext(java.lang.Class, java.lang.Object,
   *      java.lang.Object[])
   */
  @Override
  public Context newContext(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {

    /* Create a new context */

    ContextClass context = new ContextClass(this, pClass, pThis, false, pArgs);

    mThreadLocalContexts.get().push(context);

    /* Execute the start */

    for (ContextHandler handler : mHandlers)
      handler.executeOnContextStart(context);

    return context;
  }

  public void closeContext(Context pContext) {
    Context peek = mThreadLocalContexts.get().peek();
    if (pContext.equals(peek) == false)
      throw new IllegalStateException();
    mThreadLocalContexts.get().pop();
  }

  /**
   * @see com.diamondq.common.utils.misc.context.ContextFactory#getCurrentContext()
   */
  @Override
  public Context getCurrentContext() {
    if (mThreadLocalContexts.get().isEmpty() == false)
      return mThreadLocalContexts.get().peek();
    return mNOOP_CONTEXT;
  }

  /**
   * @see com.diamondq.common.utils.misc.context.ContextFactory#reportThrowable(java.lang.Class, java.lang.Object,
   *      java.lang.Throwable)
   */
  @Override
  public RuntimeException reportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable) {
    try (ContextClass context = new ContextClass(this, pClass, pThis, false, null)) {
      mThreadLocalContexts.get().push(context);
      context.setData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      return context.reportThrowable(pThrowable);
    }
  }

  @Override
  public void reportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    try (ContextClass context = new ContextClass(this, pClass, pThis, false, null)) {
      mThreadLocalContexts.get().push(context);
      context.setData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      context.reportTrace(pArgs);
    }
  }

  /* ************************************************************ */
  /* ContextFactoryImpl methods */
  /* ************************************************************ */

  public <T> T internalExit(ContextClass pContext, T pResult) {
    pContext.setData(ContextHandler.sHAS_EXPLICIT_EXITED, Boolean.TRUE);
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextExplicitExit(pContext, pResult);
    }
    return pResult;
  }

  public void internalExit(ContextClass pContext) {
    pContext.setData(ContextHandler.sHAS_EXPLICIT_EXITED, Boolean.TRUE);
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextExplicitExit(pContext);
    }
  }

  public <T> T internalExitWithMeta(ContextClass pContext, T pResult,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc) {
    pContext.setData(ContextHandler.sHAS_EXPLICIT_EXITED, Boolean.TRUE);
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextExplicitExitWithMeta(pContext, pResult, pFunc);
    }
    return pResult;
  }

  public void internalReportTrace(ContextClass pContext, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportTrace(pContext, pArgs);
    }
  }

  public RuntimeException internalReportThrowable(ContextClass pContext, Throwable pThrowable) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextExplicitThrowable(pContext, pThrowable);
    }
    if (pThrowable instanceof RuntimeException)
      return (RuntimeException) pThrowable;
    return new RuntimeException(pThrowable);
  }

}

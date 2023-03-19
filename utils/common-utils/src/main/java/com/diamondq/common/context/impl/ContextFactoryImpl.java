package com.diamondq.common.context.impl;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.context.impl.logging.LoggingContextHandler;
import com.diamondq.common.context.spi.ContextClass;
import com.diamondq.common.context.spi.ContextHandler;
import com.diamondq.common.context.spi.SPIContextFactory;
import io.micronaut.context.annotation.Secondary;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.PostConstruct;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

@Secondary
public class ContextFactoryImpl implements SPIContextFactory {

  private static final Object sNULL_EXIT_VALUE = new Object();

  public static volatile ContextFactory sINSTANCE = new ContextFactoryImpl();

  private final CopyOnWriteArrayList<ContextHandler> mHandlers = new CopyOnWriteArrayList<>();

  private volatile LoggingContextHandler mLoggingHandler;

  private final ThreadLocal<Stack<ContextClass>> mThreadLocalContexts = ThreadLocal.withInitial(Stack::new);

  private final ContextClass mNOOP_CONTEXT;

  @SuppressWarnings("null")
  public ContextFactoryImpl() {
    mNOOP_CONTEXT = new NoopContext(this);
    mThreadLocalContexts.get().add(mNOOP_CONTEXT);
    mNOOP_CONTEXT.close();
  }

  /**
   * The onActivate is called when OSGi has finished initializing us.
   */
  @PostConstruct
  public void onActivate() {

    /* Override the existing sINSTANCE with the fully configured one */

    sINSTANCE = this;
  }

  public void addContextHandler(ContextHandler pHandler) {
    if (mHandlers.addIfAbsent(pHandler))
      if (pHandler instanceof LoggingContextHandler) mLoggingHandler = (LoggingContextHandler) pHandler;
  }

  public void removeContextHandler(ContextHandler pHandler) {
    mHandlers.remove(pHandler);
  }

  /**
   * @see com.diamondq.common.context.ContextFactory#newContextWithMeta(java.lang.Class, java.lang.Object,
   *   java.lang.Object[])
   */
  @Override
  public Context newContextWithMeta(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs) {

    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();

    /* Create a new context */

    ContextClass context = new ContextClass(this, parentContext, pClass, pThis, true, pArgs);

    contextStack.add(context);

    /* Execute the start */

    context.setHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, true);
    for (ContextHandler handler : mHandlers)
      handler.executeOnContextStart(context);
    context.setHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, null);

    return context;
  }

  /**
   * @see com.diamondq.common.context.ContextFactory#newContext(java.lang.Class, java.lang.Object, java.lang.Object[])
   */
  @Override
  public Context newContext(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs) {

    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();

    /* Create a new context */

    ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, pArgs);

    contextStack.add(context);

    /* Execute the start */

    context.setHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, true);
    for (ContextHandler handler : mHandlers)
      handler.executeOnContextStart(context);
    context.setHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, null);

    return context;
  }

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#closeContext(com.diamondq.common.context.spi.ContextClass)
   */
  @Override
  public void closeContext(ContextClass pContext) {
    boolean exitSet = false;
    @Nullable Object exitValue = pContext.getHandlerData(ContextHandler.sEXIT_VALUE, false, Object.class);
    if (exitValue == sNULL_EXIT_VALUE) {
      exitValue = null;
      exitSet = true;
    } else if (exitValue != null) exitSet = true;
    @Nullable Function<@Nullable Object, @Nullable Object> exitFunc;
    @Nullable Object exitFuncObj = pContext.getHandlerData(ContextHandler.sEXIT_FUNC, false, Object.class);
    if (exitFuncObj == sNULL_EXIT_VALUE) exitFunc = null;
    else if (exitFuncObj != null) {
      @SuppressWarnings(
        "unchecked") Function<@Nullable Object, @Nullable Object> convertExitFunc = (Function<@Nullable Object, @Nullable Object>) exitFuncObj;
      exitFunc = convertExitFunc;
    } else exitFunc = null;

    pContext.setHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, true);

    for (ContextHandler handler : mHandlers)
      handler.executeOnContextClose(pContext, exitSet, exitValue, exitFunc);

    pContext.setHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, null);

    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass oldContext;
    if (contextStack.isEmpty()) oldContext = null;
    else oldContext = contextStack.peek();
    if (pContext.equals(oldContext)) {
      contextStack.pop();
    } else {
      internalReportError(pContext, "Incorrect context found on stack", (Throwable) null);
      throw new IllegalStateException();
    }

  }

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#detachContextFromThread(com.diamondq.common.context.spi.ContextClass)
   */
  @Override
  public void detachContextFromThread(ContextClass pContext) {
    for (ContextHandler handler : mHandlers)
      handler.executeOnDetachContextToThread(pContext);
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass oldContext;
    if (contextStack.isEmpty()) oldContext = null;
    else oldContext = contextStack.peek();
    if (pContext.equals(oldContext)) {
      contextStack.pop();
    } else {
      internalReportError(pContext, "Incorrect context found on stack", (Throwable) null);
      throw new IllegalStateException();
    }
  }

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#attachContextToThread(com.diamondq.common.context.spi.ContextClass)
   */
  @Override
  public void attachContextToThread(ContextClass pContext) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    contextStack.add(pContext);
    for (ContextHandler handler : mHandlers)
      handler.executeOnAttachContextToThread(pContext);
  }

  /**
   * @see com.diamondq.common.context.ContextFactory#getCurrentContext()
   */
  @Override
  public Context getCurrentContext() {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    if (!contextStack.isEmpty()) return contextStack.peek();
    return mNOOP_CONTEXT;
  }

  @Override
  public @Nullable Context getNullableCurrentContext() {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    if (!contextStack.isEmpty()) return contextStack.peek();
    return null;
  }

  /**
   * @see com.diamondq.common.context.ContextFactory#reportThrowable(java.lang.Class, java.lang.Object,
   *   java.lang.Throwable)
   */
  @Override
  public RuntimeException reportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();
    try (ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, null)) {
      contextStack.add(context);
      context.setHandlerData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      return context.reportThrowable(pThrowable);
    }
  }

  @Override
  public void reportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();
    try (ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, null)) {
      contextStack.add(context);
      context.setHandlerData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      context.trace(pArgs);
    }
  }

  /**
   * @see com.diamondq.common.context.ContextFactory#reportTrace(java.lang.Class, java.lang.Object, java.lang.String,
   *   java.lang.Object[])
   */
  @Override
  public void reportTrace(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable ... pArgs) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();
    try (ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, null)) {
      contextStack.add(context);
      context.setHandlerData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      context.trace(pMessage, pArgs);
    }
  }

  /**
   * @see com.diamondq.common.context.ContextFactory#reportDebug(java.lang.Class, java.lang.Object, java.lang.String,
   *   java.lang.Object[])
   */
  @Override
  public void reportDebug(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable ... pArgs) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();
    try (ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, null)) {
      contextStack.add(context);
      context.setHandlerData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      context.debug(pMessage, pArgs);
    }
  }

  @Override
  public void reportInfo(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable ... pArgs) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();
    try (ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, null)) {
      contextStack.add(context);
      context.setHandlerData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      context.info(pMessage, pArgs);
    }
  }

  @Override
  public void reportWarn(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable ... pArgs) {
    Stack<ContextClass> contextStack = mThreadLocalContexts.get();
    @Nullable ContextClass parentContext;
    if (contextStack.isEmpty()) parentContext = null;
    else parentContext = contextStack.peek();
    try (ContextClass context = new ContextClass(this, parentContext, pClass, pThis, false, null)) {
      contextStack.add(context);
      context.setHandlerData(ContextHandler.sSIMPLE_CONTEXT, Boolean.TRUE);
      for (ContextHandler handler : mHandlers)
        handler.executeOnContextStart(context);
      context.warn(pMessage, pArgs);
    }
  }
  /* ************************************************************ */
  /* ContextFactoryImpl methods */
  /* ************************************************************ */

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#internalExitValue(com.diamondq.common.context.spi.ContextClass,
   *   java.lang.Object)
   */
  @Override
  public <T> T internalExitValue(ContextClass pContext, T pResult) {
    pContext.setHandlerData(ContextHandler.sEXIT_VALUE, (pResult == null ? sNULL_EXIT_VALUE : pResult));
    pContext.setHandlerData(ContextHandler.sEXIT_FUNC, sNULL_EXIT_VALUE);
    return pResult;
  }

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#internalExitValueWithMeta(com.diamondq.common.context.spi.ContextClass,
   *   java.lang.Object, java.util.function.Function)
   */
  @Override
  public <T> T internalExitValueWithMeta(ContextClass pContext, T pResult,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc) {
    pContext.setHandlerData(ContextHandler.sEXIT_VALUE, (pResult == null ? sNULL_EXIT_VALUE : pResult));
    pContext.setHandlerData(ContextHandler.sEXIT_FUNC, (pFunc == null ? sNULL_EXIT_VALUE : pFunc));
    return pResult;
  }

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#internalReportTrace(com.diamondq.common.context.spi.ContextClass,
   *   java.lang.Object[])
   */
  @Override
  public void internalReportTrace(ContextClass pContext, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportTrace(pContext, null, false, pArgs);
    }
  }

  /**
   * @see com.diamondq.common.context.spi.SPIContextFactory#internalReportTrace(com.diamondq.common.context.spi.ContextClass,
   *   java.lang.String, java.lang.Object[])
   */
  @Override
  public void internalReportTrace(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportTrace(pContext, pMessage, false, pArgs);
    }
  }

  @Override
  public void internalReportTraceWithMeta(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportTrace(pContext, pMessage, true, pArgs);
    }
  }

  @Override
  public boolean internalIsTraceEnabled(ContextClass pContext) {
    return mLoggingHandler.isTraceEnabled(pContext);
  }

  @Override
  public void internalReportDebug(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportDebug(pContext, pMessage, false, pArgs);
    }
  }

  @Override
  public void internalReportDebugWithMeta(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportDebug(pContext, pMessage, true, pArgs);
    }
  }

  @Override
  public boolean internalIsDebugEnabled(ContextClass pContext) {
    return mLoggingHandler.isDebugEnabled(pContext);
  }

  @Override
  public void internalReportInfo(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportInfo(pContext, pMessage, pArgs);
    }
  }

  @Override
  public boolean internalIsInfoEnabled(ContextClass pContext) {
    return mLoggingHandler.isInfoEnabled(pContext);
  }

  @Override
  public void internalReportWarn(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportWarn(pContext, pMessage, pArgs);
    }
  }

  @Override
  public boolean internalIsWarnEnabled(ContextClass pContext) {
    return mLoggingHandler.isWarnEnabled(pContext);
  }

  @Override
  public void internalReportError(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextReportError(pContext, pMessage, pArgs);
    }
  }

  @Override
  public void internalReportError(ContextClass pContext, String pMessage, @Nullable Throwable pThrowable) {
    for (ContextHandler handler : mHandlers) {
      if (pThrowable == null) handler.executeOnContextReportError(pContext, pMessage, (Object[]) null);
      else handler.executeOnContextReportError(pContext, pMessage, pThrowable);
    }
  }

  @Override
  public boolean internalIsErrorEnabled(ContextClass pContext) {
    return mLoggingHandler.isErrorEnabled(pContext);
  }

  @Override
  public RuntimeException internalReportThrowable(ContextClass pContext, Throwable pThrowable) {
    for (ContextHandler handler : mHandlers) {
      handler.executeOnContextExplicitThrowable(pContext, pThrowable);
    }
    if (pThrowable instanceof RuntimeException) return (RuntimeException) pThrowable;
    return new RuntimeException(pThrowable);
  }

}

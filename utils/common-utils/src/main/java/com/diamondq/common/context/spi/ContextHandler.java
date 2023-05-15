package com.diamondq.common.context.spi;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface ContextHandler {

  String sEXIT_VALUE = ContextClass.sHANDLER_DATA_PREFIX + "CH_EXIT_VALUE";

  String sEXIT_FUNC = ContextClass.sHANDLER_DATA_PREFIX + "CH_EXIT_FUNC";

  String sSIMPLE_CONTEXT = ContextClass.sHANDLER_DATA_PREFIX + "CH_SIMPLE_CONTEXT";

  /**
   * Called whenever a Context is created
   *
   * @param pContext the context
   */
  void executeOnContextStart(ContextClass pContext);

  /**
   * Called whenever the Context is closed
   *
   * @param pContext the context
   * @param pWithExitValue true if the exit value is provided or false if it's not (this is needed since null in
   *   pExitValue is not enough to tell if the exit was provided or not)
   * @param pExitValue the exit value if provided
   * @param pFunc the exit function
   */
  void executeOnContextClose(ContextClass pContext, boolean pWithExitValue, @Nullable Object pExitValue,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  /**
   * Called whenever the Context is passed an explicit throwable
   *
   * @param pContext the context
   * @param pThrowable the throwable
   */
  void executeOnContextExplicitThrowable(ContextClass pContext, Throwable pThrowable);

  void executeOnContextReportTrace(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable ... pArgs);

  void executeOnContextReportDebug(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable ... pArgs);

  void executeOnContextReportInfo(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable ... pArgs);

  void executeOnContextReportWarn(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable ... pArgs);

  void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage, Throwable pThrowable);

  void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable ... pArgs);

  void executeOnDetachContextToThread(ContextClass pContext);

  void executeOnAttachContextToThread(ContextClass pContext);
}

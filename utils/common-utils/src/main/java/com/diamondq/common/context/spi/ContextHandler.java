package com.diamondq.common.context.spi;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContextHandler {

  static final String sEXIT_VALUE     = ContextClass.sHANDLER_DATA_PREFIX + "CH_EXIT_VALUE";

  static final String sEXIT_FUNC      = ContextClass.sHANDLER_DATA_PREFIX + "CH_EXIT_FUNC";

  static final String sSIMPLE_CONTEXT = ContextClass.sHANDLER_DATA_PREFIX + "CH_SIMPLE_CONTEXT";

  /**
   * Called whenever a Context is created
   * 
   * @param pContext the context
   */
  public void executeOnContextStart(ContextClass pContext);

  /**
   * Called whenever the Context is closed
   * 
   * @param pContext the context
   * @param pWithExitValue true if the exit value is provided or false if it's not (this is needed since null in
   *          pExitValue is not enough to tell if the exit was provided or not)
   * @param pExitValue the exit value if provided
   * @param pFunc the exit function
   */
  public void executeOnContextClose(ContextClass pContext, boolean pWithExitValue, @Nullable Object pExitValue,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  /**
   * Called whenever the Context is passed an explicit throwable
   * 
   * @param pContext the context
   * @param pThrowable the throwable
   */
  public void executeOnContextExplicitThrowable(ContextClass pContext, Throwable pThrowable);

  public void executeOnContextReportTrace(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportDebug(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportInfo(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportWarn(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage, Throwable pThrowable);

  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnDetachContextToThread(ContextClass pContext);

  public void executeOnAttachContextToThread(ContextClass pContext);
}

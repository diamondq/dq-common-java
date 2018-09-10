package com.diamondq.common.utils.context.spi;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContextHandler {

  public static final String sHAS_EXPLICIT_EXITED = "CH_HAS_EXPLICIT_EXITED";

  public static final String sSIMPLE_CONTEXT      = "CH_SIMPLE_CONTEXT";

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
   * @param pHasExplicitlyExited true if it was explicitly exited before the close
   */
  public void executeOnContextClose(ContextClass pContext, boolean pHasExplicitlyExited);

  /**
   * Called whenever the Context is passed an explicit exit
   * 
   * @param pContext the context
   */
  public void executeOnContextExplicitExit(ContextClass pContext);

  /**
   * Called whenever the Context is passed an explicit exit
   * 
   * @param pContext the context
   * @param pArg the exit value
   */
  public void executeOnContextExplicitExit(ContextClass pContext, @Nullable Object pArg);

  /**
   * Called whenever the Context is passed an explicit exit
   * 
   * @param pContext the context
   * @param pArg the exit value
   * @param pFunc the conversion function
   */
  public void executeOnContextExplicitExitWithMeta(ContextClass pContext, @Nullable Object pArg,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  /**
   * Called whenever the Context is passed an explicit throwable
   * 
   * @param pContext the context
   * @param pThrowable the throwable
   */
  public void executeOnContextExplicitThrowable(ContextClass pContext, Throwable pThrowable);

  public void executeOnContextReportTrace(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportDebug(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportInfo(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportWarn(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage, Throwable pThrowable);

  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs);

}

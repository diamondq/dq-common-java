package com.diamondq.common.utils.context.logging;

import com.diamondq.common.utils.context.spi.ContextClass;
import com.diamondq.common.utils.context.spi.ContextHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingContextHandler implements ContextHandler {

  private ConcurrentMap<Class<?>, Logger> mLoggerMap = new ConcurrentHashMap<>();

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextStart(com.diamondq.common.utils.context.spi.ContextClass)
   */
  @Override
  public void executeOnContextStart(ContextClass pContext) {
    if (pContext.getData(ContextHandler.sSIMPLE_CONTEXT, false, Boolean.class) != null)
      return;
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(LoggingUtils.sENTRY_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      LoggingUtils.entryWithMetaInternal(logger, LoggingUtils.sENTRY_MARKER, pContext.startThis, methodName,
        pContext.argsHaveMeta, true, pContext.startArguments);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextClose(com.diamondq.common.utils.context.spi.ContextClass,
   *      boolean)
   */
  @Override
  public void executeOnContextClose(ContextClass pContext, boolean pHasExplictlyExited) {
    if (pContext.getData(ContextHandler.sSIMPLE_CONTEXT, false, Boolean.class) != null)
      return;
    if (pHasExplictlyExited == false) {
      Logger logger = mLoggerMap.get(pContext.startClass);
      if (logger == null) {
        Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
        if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
          logger = newLogger;
      }
      if (logger.isTraceEnabled(LoggingUtils.sEXIT_MARKER)) {
        String methodName = pContext.getLatestStackMethod();
        LoggingUtils.exitInternal(logger, pContext.startThis, methodName, true, null, false, null, null);
      }
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextExplicitExit(com.diamondq.common.utils.context.spi.ContextClass)
   */
  @Override
  public void executeOnContextExplicitExit(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(LoggingUtils.sEXIT_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      LoggingUtils.exitInternal(logger, pContext.startThis, methodName, true, null, false, null, null);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextExplicitExit(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.Object)
   */
  @Override
  public void executeOnContextExplicitExit(ContextClass pContext, @Nullable Object pArg) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(LoggingUtils.sEXIT_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      LoggingUtils.exitInternal(logger, pContext.startThis, methodName, true, null, true, pArg, null);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextExplicitExitWithMeta(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.Object, java.util.function.Function)
   */
  @Override
  public void executeOnContextExplicitExitWithMeta(ContextClass pContext, @Nullable Object pArg,
    @Nullable Function<@Nullable Object, @Nullable Object> pMeta) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(LoggingUtils.sEXIT_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      LoggingUtils.exitInternal(logger, pContext.startThis, methodName, true, null, true, pArg, pMeta);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextExplicitThrowable(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.Throwable)
   */
  @Override
  public void executeOnContextExplicitThrowable(ContextClass pContext, Throwable pThrowable) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(LoggingUtils.sEXIT_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      LoggingUtils.exitInternal(logger, pContext.startThis, methodName, false, pThrowable, false, null, null);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportTrace(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportTrace(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (pMessage == null) {
      if (logger.isTraceEnabled(LoggingUtils.sSIMPLE_ENTRY_MARKER)) {
        String methodName = pContext.getLatestStackMethod();
        LoggingUtils.entryWithMetaInternal(logger, LoggingUtils.sSIMPLE_ENTRY_MARKER, pContext.startThis, methodName,
          false, false, pArgs);
      }
    }
    else {
      if (logger.isTraceEnabled()) {
        logger.trace(pMessage, pArgs);
      }
    }
  }

  public boolean isTraceEnabled(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    return logger.isTraceEnabled();
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportDebug(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportDebug(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isDebugEnabled()) {
      logger.debug(pMessage, pArgs);
    }
  }

  public boolean isDebugEnabled(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    return logger.isDebugEnabled();
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportInfo(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportInfo(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isInfoEnabled()) {
      logger.info(pMessage, pArgs);
    }
  }

  public boolean isInfoEnabled(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    return logger.isInfoEnabled();
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportWarn(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportWarn(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isWarnEnabled()) {
      logger.warn(pMessage, pArgs);
    }
  }

  public boolean isWarnEnabled(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    return logger.isWarnEnabled();
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportError(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage,
    @Nullable Object @Nullable... pArgs) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isErrorEnabled()) {
      logger.error(pMessage, pArgs);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportError(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, java.lang.Throwable)
   */
  @Override
  public void executeOnContextReportError(ContextClass pContext, @Nullable String pMessage, Throwable pThrowable) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isErrorEnabled()) {
      logger.error(pMessage, pThrowable);
    }
  }

  public boolean isErrorEnabled(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    return logger.isErrorEnabled();
  }
}

package com.diamondq.common.utils.context.impl.logging;

import com.diamondq.common.utils.context.spi.ContextClass;
import com.diamondq.common.utils.context.spi.ContextHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class LoggingContextHandler implements ContextHandler {

  private ConcurrentMap<Class<?>, Logger> mLoggerMap               = new ConcurrentHashMap<>();

  public static Marker                    sSIMPLE_ENTRY_MARKER     = MarkerFactory.getMarker("ENTRY_S");

  public static Marker                    sENTRY_MARKER            = MarkerFactory.getMarker("ENTRY");

  public static Marker                    sEXIT_MARKER             = MarkerFactory.getMarker("EXIT");

  private static String                   sEXIT_MESSAGE_0          = "EXIT {}() from {}";

  private static String                   sEXIT_MESSAGE_1          = "EXIT {}(...) with {} from {}";

  private static String                   sDETACH_MESSAGE_0        = "DETACH {}() from {}";

  private static String                   sEXIT_MESSAGE_ERROR      = "EXIT {}() from {} with error";

  private static String[]                 sENTRY_MESSAGE_ARRAY     = new String[] {"{}() from {}", "{}({}) from {}",
      "{}({}, {}) from {}", "{}({}, {}, {}) from {}", "{}({}, {}, {}, {}) from {}"};

  private static int                      sENTRY_MESSAGE_ARRAY_LEN = sENTRY_MESSAGE_ARRAY.length;

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextStart(com.diamondq.common.utils.context.spi.ContextClass)
   */
  @Override
  public void executeOnContextStart(ContextClass pContext) {
    if (pContext.getHandlerData(ContextHandler.sSIMPLE_CONTEXT, false, Boolean.class) != null)
      return;
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(sENTRY_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      entryWithMetaInternal(pContext, logger, sENTRY_MARKER, pContext.startThis, methodName, pContext.argsHaveMeta,
        true, pContext.startArguments);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextClose(com.diamondq.common.utils.context.spi.ContextClass,
   *      boolean, java.lang.Object, java.util.function.Function)
   */
  @Override
  public void executeOnContextClose(ContextClass pContext, boolean pWithExitValue, @Nullable Object pExitValue,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc) {
    if (pContext.getHandlerData(ContextHandler.sSIMPLE_CONTEXT, false, Boolean.class) != null)
      return;
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(sEXIT_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      exitInternal(pContext, logger, pContext.startThis, methodName, true, null, pWithExitValue, false, pExitValue,
        pFunc);
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
    if (logger.isErrorEnabled()) {
      String methodName = pContext.getLatestStackMethod();
      exitInternal(pContext, logger, pContext.startThis, methodName, false, pThrowable, false, false, null, null);
    }
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnContextReportTrace(com.diamondq.common.utils.context.spi.ContextClass,
   *      java.lang.String, boolean, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportTrace(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
    @Nullable Object @Nullable... pArgs) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (pMessage == null) {
      if (logger.isTraceEnabled(sSIMPLE_ENTRY_MARKER)) {
        String methodName = pContext.getLatestStackMethod();
        entryWithMetaInternal(pContext, logger, sSIMPLE_ENTRY_MARKER, pContext.startThis, methodName, pWithMeta, false,
          pArgs);
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
   *      java.lang.String, boolean, java.lang.Object[])
   */
  @Override
  public void executeOnContextReportDebug(ContextClass pContext, @Nullable String pMessage, boolean pWithMeta,
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

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnAttachContextToThread(com.diamondq.common.utils.context.spi.ContextClass)
   */
  @Override
  public void executeOnAttachContextToThread(ContextClass pContext) {

  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextHandler#executeOnDetachContextToThread(com.diamondq.common.utils.context.spi.ContextClass)
   */
  @Override
  public void executeOnDetachContextToThread(ContextClass pContext) {
    Logger logger = mLoggerMap.get(pContext.startClass);
    if (logger == null) {
      Logger newLogger = LoggerFactory.getLogger(pContext.startClass);
      if ((logger = mLoggerMap.putIfAbsent(pContext.startClass, newLogger)) == null)
        logger = newLogger;
    }
    if (logger.isTraceEnabled(sEXIT_MARKER)) {
      String methodName = pContext.getLatestStackMethod();
      exitInternal(pContext, logger, pContext.startThis, methodName, true, null, false, true, null, null);
    }
    if (pContext.getHandlerData(ContextHandler.sSIMPLE_CONTEXT, false, Boolean.class) != null)
      return;
  }

  /**
   * Common internal function to handle the entry routine
   * 
   * @param pContextClass the context class
   * @param pLogger the logger
   * @param pMarker the marker
   * @param pThis the object representing 'this'
   * @param pMethodName the method name (if null, then it's calculated)
   * @param pWithMeta true if there is meta data in the arguments or false if there isn't.
   * @param pMatchEntryExit true if there must be matching exit or false if this is a standalone entry
   * @param pArgs any arguments to display
   */
  private void entryWithMetaInternal(ContextClass pContextClass, Logger pLogger, Marker pMarker, @Nullable Object pThis,
    @Nullable String pMethodName, boolean pWithMeta, boolean pMatchEntryExit, @Nullable Object @Nullable... pArgs) {
    String messagePattern;
    if (pArgs == null)
      pArgs = new Object[0];
    int argsLen = pArgs.length / (pWithMeta ? 2 : 1);
    if (argsLen < sENTRY_MESSAGE_ARRAY_LEN)
      messagePattern = sENTRY_MESSAGE_ARRAY[argsLen];
    else
      messagePattern = buildMessagePattern(argsLen);

    int expandedLen;
    if (argsLen == 0)
      expandedLen = 2;
    else
      expandedLen = argsLen + 2;
    Object[] expandedArgs = new Object[expandedLen];
    Object[] filteredArgs;

    /* See if the last entry is a Throwable */

    Object lastEntry = (pArgs.length > 0 ? pArgs[pArgs.length - 1] : null);

    if (expandedLen > 2) {

      if (pWithMeta == false) {

        /* Copy the arguments (skipping the final throwable if it's present */

        System.arraycopy(pArgs, 0, expandedArgs, 1, (lastEntry instanceof Throwable ? argsLen - 1 : argsLen));
        filteredArgs = pArgs;
      }
      else {
        filteredArgs = new Object[argsLen];
        for (int i = 0; i < argsLen; i++) {
          int argOffset = i * 2;
          @SuppressWarnings("unchecked")
          @Nullable
          Function<@Nullable Object, @Nullable Object> func =
            (Function<@Nullable Object, @Nullable Object>) pArgs[argOffset + 1];
          if (func == null)
            expandedArgs[1 + i] = pArgs[argOffset];
          else {
            expandedArgs[1 + i] = func.apply(pArgs[argOffset]);
            filteredArgs[i] = expandedArgs[1 + i];
          }
        }
      }

      /* Add the throwable back in */

      if (lastEntry instanceof Throwable)
        expandedArgs[expandedArgs.length] = lastEntry;
    }
    else
      filteredArgs = pArgs;

    /* Calculate the method name */

    if (pMethodName == null) {
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      String methodName = stackTraceElements[3].getMethodName();
      expandedArgs[0] = methodName;
      pMethodName = methodName;
    }
    else
      expandedArgs[0] = pMethodName;

    /* Add the caller object */

    if ("<init>".equals(pMethodName))
      expandedArgs[expandedArgs.length - (lastEntry instanceof Throwable ? 2 : 1)] =
        pThis == null ? null : pThis.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(pThis));
    else
      expandedArgs[expandedArgs.length - (lastEntry instanceof Throwable ? 2 : 1)] = pThis;

    FormattingTuple tp = MessageFormatter.arrayFormat(messagePattern, expandedArgs);
    pLogger.trace(pMarker, tp.getMessage(), filteredArgs);

  }

  private void exitInternal(ContextClass pContextClass, Logger pLogger, @Nullable Object pThis,
    @Nullable String pMethodName, boolean pMatchEntryExit, @Nullable Throwable pThrowable, boolean pWithResult,
    boolean pWithDetach, @Nullable Object pResult, @Nullable Function<@Nullable Object, @Nullable Object> pMeta) {

    String methodName;
    if (pMethodName == null) {
      /* Calculate the method name */

      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      methodName = stackTraceElements[3].getMethodName();
    }
    else
      methodName = pMethodName;

    if (pThrowable != null)
      pLogger.error(sEXIT_MARKER, sEXIT_MESSAGE_ERROR, methodName, pThis, pThrowable);

    else if (pWithResult == true) {
      if (pMeta != null) {
        Object newResult = pMeta.apply(pResult);
        pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_1, methodName, newResult, pThis);
      }
      else {
        pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_1, methodName, pResult, pThis);

      }
    }
    else {
      if (pWithDetach == true)
        pLogger.trace(sEXIT_MARKER, sDETACH_MESSAGE_0, methodName, pThis);
      else
        pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_0, methodName, pThis);
    }

  }

  private static String buildMessagePattern(int len) {
    StringBuilder sb = new StringBuilder();
    sb.append("{}(");
    for (int i = 0; i < len; i++) {
      sb.append("{}");
      if (i != (len - 1))
        sb.append(", ");
    }
    sb.append(") from {}");
    return sb.toString();
  }
}
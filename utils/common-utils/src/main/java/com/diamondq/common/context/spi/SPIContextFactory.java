package com.diamondq.common.context.spi;

import com.diamondq.common.context.ContextFactory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This extended ContextFactory interface defines additional methods that can be used by consumers of the SPI (i.e.
 * ContextHandler's)
 */
public interface SPIContextFactory extends ContextFactory {

  void attachContextToThread(ContextClass pContext);

  void closeContext(ContextClass pContext);

  void detachContextFromThread(ContextClass pContext);

  <T> T internalExitValue(ContextClass pContext, T pResult);

  <T> T internalExitValueWithMeta(ContextClass pContext, T pResult,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  void internalReportTrace(ContextClass pContext, @Nullable Object @Nullable [] pArgs);

  void internalReportTrace(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  void internalReportTraceWithMeta(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  boolean internalIsTraceEnabled(ContextClass pContext);

  void internalReportDebug(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  void internalReportDebugWithMeta(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  boolean internalIsDebugEnabled(ContextClass pContext);

  void internalReportInfo(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  boolean internalIsInfoEnabled(ContextClass pContext);

  void internalReportWarn(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  boolean internalIsWarnEnabled(ContextClass pContext);

  void internalReportError(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  void internalReportError(ContextClass pContext, String pMessage, @Nullable Throwable pThrowable);

  boolean internalIsErrorEnabled(ContextClass pContext);

  RuntimeException internalReportThrowable(ContextClass pContext, Throwable pThrowable);

}

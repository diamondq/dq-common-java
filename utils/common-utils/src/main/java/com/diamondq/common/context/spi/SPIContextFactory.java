package com.diamondq.common.context.spi;

import com.diamondq.common.context.ContextFactory;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This extended ContextFactory interface defines additional methods that can be used by consumers of the SPI (ie.
 * ContextHandler's)
 */
public interface SPIContextFactory extends ContextFactory {

  public void attachContextToThread(ContextClass pContext);

  public void closeContext(ContextClass pContext);

  public void detachContextFromThread(ContextClass pContext);

  public <T> T internalExitValue(ContextClass pContext, T pResult);

  public <T> T internalExitValueWithMeta(ContextClass pContext, T pResult,
    @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  public void internalReportTrace(ContextClass pContext, @Nullable Object @Nullable [] pArgs);

  public void internalReportTrace(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public void internalReportTraceWithMeta(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public boolean internalIsTraceEnabled(ContextClass pContext);

  public void internalReportDebug(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public void internalReportDebugWithMeta(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public boolean internalIsDebugEnabled(ContextClass pContext);

  public void internalReportInfo(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public boolean internalIsInfoEnabled(ContextClass pContext);

  public void internalReportWarn(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public boolean internalIsWarnEnabled(ContextClass pContext);

  public void internalReportError(ContextClass pContext, String pMessage, @Nullable Object @Nullable [] pArgs);

  public void internalReportError(ContextClass pContext, String pMessage, @Nullable Throwable pThrowable);

  public boolean internalIsErrorEnabled(ContextClass pContext);

  public RuntimeException internalReportThrowable(ContextClass pContext, Throwable pThrowable);

}

package com.diamondq.common.utils.context.spi;

import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.impl.ContextFactoryImpl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Internal implementation of Context with additional methods for ContextHandlers to use
 */
public class ContextClass implements Context {

  public final Class<?>                                    startClass;

  public final @Nullable Object                            startThis;

  public final boolean                                     argsHaveMeta;

  public final @Nullable Object @Nullable []               startArguments;

  private volatile @Nullable String                        mLatestStackMethod;

  private final ContextFactoryImpl                         mFactory;

  private volatile @Nullable ConcurrentMap<String, Object> mDataMap;

  public ContextClass(ContextFactoryImpl pFactory, Class<?> pStartClass, @Nullable Object pStartThis,
    boolean pArgsHaveMeta, @Nullable Object @Nullable [] pStartArguments) {
    mFactory = pFactory;
    startClass = pStartClass;
    startThis = pStartThis;
    argsHaveMeta = pArgsHaveMeta;
    startArguments = pStartArguments;
  }

  /**
   * Allows a ContextHandler to set some data within this Context
   * 
   * @param pKey the key
   * @param pValue the value
   */
  public void setData(String pKey, Object pValue) {
    ConcurrentMap<String, Object> dataMap = mDataMap;
    if (dataMap == null) {
      synchronized (this) {
        dataMap = mDataMap;
        if (dataMap == null) {
          dataMap = new ConcurrentHashMap<>();
          mDataMap = dataMap;
        }
      }
    }
    dataMap.put(pKey, pValue);
  }

  /**
   * Allows a ContextHandler to retrieve some data within the context
   * 
   * @param pKey the key
   * @return the value or null if there is no value
   */
  public @Nullable Object getData(String pKey) {
    ConcurrentMap<String, Object> dataMap = mDataMap;
    if (dataMap == null)
      return null;
    return dataMap.get(pKey);
  }

  /**
   * Returns the stack method that 'created' the Context. This is a cached value, but it must be called at least once
   * during the creation handling, otherwise, the method name won't be correct. <br/>
   * <br/>
   * NOTE: The method name is determined by walking the stack until we pass out of the ContextFactoryImpl.
   * 
   * @return the stack method
   */
  public String getLatestStackMethod() {
    synchronized (this) {
      if (mLatestStackMethod != null)
        return mLatestStackMethod;
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      boolean inFactory = false;
      for (int i = 0; i < stackTrace.length; i++) {
        if (inFactory == false) {
          String className = stackTrace[i].getClassName();
          if (className.startsWith("com.diamondq.common.utils.context.impl.ContextFactoryImpl") == true)
            inFactory = true;
        }
        else {
          String className = stackTrace[i].getClassName();
          if (className.startsWith("com.diamondq.common.utils.context.") == true)
            continue;
          mLatestStackMethod = stackTrace[i].getMethodName();
          break;
        }
      }

      if (mLatestStackMethod != null)
        return mLatestStackMethod;
      throw new IllegalStateException();
    }
  }

  /**
   * @see com.diamondq.common.utils.context.Context#exit(java.lang.Object)
   */
  @Override
  public <T> T exit(T pResult) {
    return mFactory.internalExit(this, pResult);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#exit()
   */
  @Override
  public void exit() {
    mFactory.internalExit(this);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#exit(java.lang.Object, java.util.function.Function)
   */
  @Override
  public <T> T exit(T pResult, @Nullable Function<@Nullable Object, @Nullable Object> pFunc) {
    return mFactory.internalExitWithMeta(this, pResult, pFunc);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#trace(java.lang.Object[])
   */
  @Override
  public void trace(@Nullable Object @Nullable... pArgs) {
    mFactory.internalReportTrace(this, pArgs);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#isTraceEnabled()
   */
  @Override
  public boolean isTraceEnabled() {
    return mFactory.internalIsTraceEnabled(this);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#trace(java.lang.String, java.lang.Object[])
   */
  @Override
  public void trace(String pMessage, @Nullable Object @Nullable... pArgs) {
    mFactory.internalReportTrace(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#debug(java.lang.String, java.lang.Object[])
   */
  @Override
  public void debug(String pMessage, @Nullable Object @Nullable... pArgs) {
    mFactory.internalReportDebug(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#isDebugEnabled()
   */
  @Override
  public boolean isDebugEnabled() {
    return mFactory.internalIsDebugEnabled(this);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#info(java.lang.String, java.lang.Object[])
   */
  @Override
  public void info(String pMessage, @Nullable Object @Nullable... pArgs) {
    mFactory.internalReportInfo(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#isInfoEnabled()
   */
  @Override
  public boolean isInfoEnabled() {
    return mFactory.internalIsInfoEnabled(this);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#warn(java.lang.String, java.lang.Object[])
   */
  @Override
  public void warn(String pMessage, @Nullable Object @Nullable... pArgs) {
    mFactory.internalReportWarn(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#isWarnEnabled()
   */
  @Override
  public boolean isWarnEnabled() {
    return mFactory.internalIsWarnEnabled(this);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#error(java.lang.String, java.lang.Object[])
   */
  @Override
  public void error(String pMessage, @Nullable Object @Nullable... pArgs) {
    mFactory.internalReportError(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#error(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void error(String pMessage, @Nullable Throwable pThrowable) {
    mFactory.internalReportError(this, pMessage, pThrowable);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#isErrorEnabled()
   */
  @Override
  public boolean isErrorEnabled() {
    return mFactory.internalIsErrorEnabled(this);
  }

  /**
   * @see com.diamondq.common.utils.context.Context#reportThrowable(java.lang.Throwable)
   */
  @Override
  public RuntimeException reportThrowable(Throwable pThrowable) {
    return mFactory.internalReportThrowable(this, pThrowable);
  }

  /**
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() {
    mFactory.closeContext(this);
  }

}

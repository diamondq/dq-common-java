package com.diamondq.common.context.spi;

import com.diamondq.common.context.Context;
import com.diamondq.common.errors.I18NStringAndException;
import com.diamondq.common.i18n.I18NString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Internal implementation of Context with additional methods for ContextHandlers to use
 */
public class ContextClass implements Context {

  public static final String                               sHANDLER_DATA_PREFIX    = "__$$__";

  /**
   * A monotonically increasing counter used to 'name' a context.
   */
  private static final AtomicLong                          sContextCounter         = new AtomicLong(0L);

  public static final String                               sDURING_CONTEXT_CONTROL =
    sHANDLER_DATA_PREFIX + "CC_DURING_CONTEXT_CONTROL";

  private static final String                              sI18NSTRING             = "{}";

  /**
   * All calls are routed to the factory for actual functioning
   */
  private final SPIContextFactory                          mFactory;

  /**
   * The unique name for this context
   */
  public final String                                      contextName             =
    String.valueOf(sContextCounter.incrementAndGet());

  /**
   * The class that created this context
   */
  public final Class<?>                                    startClass;

  /**
   * The instance that created this context
   */
  public final @Nullable Object                            startThis;

  /**
   * The set of arguments passed during creation of the context
   */
  public final @Nullable Object @Nullable []               startArguments;

  /**
   * Indicates whether the startArguments are interspersed with Functions converters or not
   */
  public final boolean                                     argsHaveMeta;

  /**
   * This map is used by callers to associate data with the context
   */
  private volatile @Nullable ConcurrentMap<String, Object> mDataMap;

  /**
   * Tracks the number of 'attaches' against this Context. This context doesn't fully close until 'close' is called an
   * equal number of times to prepareForAlternateThreads.
   */
  private final AtomicInteger                              mOpenCount              = new AtomicInteger(1);

  private volatile @Nullable String                        mLatestStackMethod;

  private final @Nullable ContextClass                     mParentContextClass;

  private volatile @Nullable List<String>                  mContextStackNames;

  public ContextClass(SPIContextFactory pFactory, @Nullable ContextClass pParentContextClass, Class<?> pStartClass,
    @Nullable Object pStartThis, boolean pArgsHaveMeta, @Nullable Object @Nullable [] pStartArguments) {
    mFactory = pFactory;
    mParentContextClass = pParentContextClass;
    startClass = pStartClass;
    startThis = pStartThis;
    argsHaveMeta = pArgsHaveMeta;
    startArguments = pStartArguments;
  }

  /**
   * @see com.diamondq.common.context.Context#setData(java.lang.String, java.lang.Object)
   */
  @Override
  public <@NonNull T> void setData(String pKey, T pValue) {

    /* Make sure the context is open */

    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.setData() called on an already closed Context", null);

    /* Make sure the key doesn't start with the handler prefix */

    if (pKey.startsWith(sHANDLER_DATA_PREFIX))
      throw new IllegalStateException();

    setHandlerData(pKey, pValue);
  }

  /**
   * Sets the data into the context
   * 
   * @param pKey the key
   * @param pValue the value
   */
  public void setHandlerData(String pKey, @Nullable Object pValue) {

    /* NOTE: The context is NOT verified to be open, since this may be called during the closing of the context */

    ConcurrentMap<String, Object> dataMap = mDataMap;

    /* Due the fact that the mDataMap is defined as volatile, it's ok to do the check/sync/check pattern */

    if (dataMap == null) {
      synchronized (this) {
        dataMap = mDataMap;
        if (dataMap == null) {
          dataMap = new ConcurrentHashMap<>();
          mDataMap = dataMap;
        }
      }
    }
    if (pValue == null)
      dataMap.remove(pKey);
    else
      dataMap.put(pKey, pValue);
  }

  /**
   * @see com.diamondq.common.context.Context#getData(java.lang.String, boolean, java.lang.Class)
   */
  @Override
  public <T> @Nullable T getData(String pKey, boolean pSearchParents, Class<T> pDataClass) {

    /* Make sure the context is open */

    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.getData() called on an already closed Context", null);

    /* Make sure the key doesn't start with the handler prefix */

    if (pKey.startsWith(sHANDLER_DATA_PREFIX))
      throw new IllegalStateException();

    return getHandlerData(pKey, pSearchParents, pDataClass);
  }

  /**
   * Gets data from the context.
   * 
   * @param pKey the key (NOTE: All handlers must start with the sHANDLER_DATA_PREFIX to guarantee uniqueness)
   * @param pSearchParents true if parent contexts should be searched if the current context doesn't have a value
   * @param pDataClass the expected class of the result
   * @return the result or null
   */
  public <T> @Nullable T getHandlerData(String pKey, boolean pSearchParents, Class<T> pDataClass) {

    /* NOTE: The context is NOT verified to be open, since this may be called during the closing of the context */

    /* If we're not searching the parent, then a simple handle */

    if (pSearchParents == false) {
      @Nullable
      ConcurrentMap<String, Object> dataMap = mDataMap;
      if (dataMap == null)
        return null;
      Object result = dataMap.get(pKey);
      if ((result != null) && (pDataClass.isInstance(result) == false))
        throw new IllegalArgumentException();
      @SuppressWarnings("unchecked")
      T objResult = (T) result;
      return objResult;
    }

    @SuppressWarnings("resource")
    @Nullable
    ContextClass context = this;
    while (context != null) {
      @Nullable
      ConcurrentMap<String, Object> dataMap = context.mDataMap;
      if (dataMap != null) {
        Object result = dataMap.get(pKey);
        if (result != null) {
          if (pDataClass.isInstance(result) == false)
            throw new IllegalArgumentException();
          @SuppressWarnings("unchecked")
          T objResult = (T) result;
          return objResult;
        }
      }
      context = context.mParentContextClass;
    }
    return null;
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
    if (mLatestStackMethod != null)
      return mLatestStackMethod;
    synchronized (this) {
      if (mLatestStackMethod == null) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        boolean inFactory = false;
        for (int i = 0; i < stackTrace.length; i++) {
          if (inFactory == false) {
            String className = stackTrace[i].getClassName();
            if (className.startsWith("com.diamondq.common.context.impl.ContextFactoryImpl") == true)
              inFactory = true;
          }
          else {
            String className = stackTrace[i].getClassName();
			
			/* There are a slew of 'internal' entries that we'll skip. This is mostly around CDI injection */
			
            if (className.startsWith("com.diamondq.common.context.") == true)
              continue;
            if (className.startsWith("sun.reflect.") == true)
              continue;
            if (className.equals("java.lang.reflect.Method") == true)
              continue;
            if (className.startsWith("org.jboss.weld.bean.proxy.") == true)
              continue;
            if (className.contains("**$$_WeldClientProxy") == true)
              continue;
            mLatestStackMethod = stackTrace[i].getMethodName();
            break;
          }
        }

      }
      if (mLatestStackMethod != null)
        return mLatestStackMethod;
      throw new IllegalStateException();
    }
  }

  /**
   * @see com.diamondq.common.context.Context#exit(java.lang.Object)
   */
  @Override
  public <T> T exit(T pResult) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.exit() called on an already closed Context", null);
    return mFactory.internalExitValue(this, pResult);
  }

  /**
   * @see com.diamondq.common.context.Context#exit(java.lang.Object, java.util.function.Function)
   */
  @Override
  public <T> T exit(T pResult, @Nullable Function<@Nullable Object, @Nullable Object> pFunc) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.exit() called on an already closed Context", null);
    return mFactory.internalExitValueWithMeta(this, pResult, pFunc);
  }

  /**
   * @see com.diamondq.common.context.Context#trace(com.diamondq.common.errors.I18NStringAndException)
   */
  @Override
  public I18NStringAndException trace(I18NStringAndException pEx) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.trace() called on an already closed Context", null);
    I18NString message = pEx.getMessage();
    Throwable throwable = pEx.getThrowable();
    if (throwable != null)
      mFactory.internalReportTrace(this, sI18NSTRING, new @Nullable Object[] {message});
    else
      mFactory.internalReportTrace(this, sI18NSTRING, new @Nullable Object[] {message, pEx});
    return pEx;
  }

  /**
   * @see com.diamondq.common.context.Context#trace(java.lang.Object[])
   */
  @Override
  public void trace(@Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.trace() called on an already closed Context", null);
    mFactory.internalReportTrace(this, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#isTraceEnabled()
   */
  @Override
  public boolean isTraceEnabled() {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.isTraceEnabled() called on an already closed Context", null);
    return mFactory.internalIsTraceEnabled(this);
  }

  /**
   * @see com.diamondq.common.context.Context#trace(java.lang.String, java.lang.Object[])
   */
  @Override
  public void trace(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.trace() called on an already closed Context", null);
    mFactory.internalReportTrace(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#traceWithMeta(java.lang.String, java.lang.Object[])
   */
  @Override
  public void traceWithMeta(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.trace() called on an already closed Context", null);
    mFactory.internalReportTraceWithMeta(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#debug(java.lang.String, java.lang.Object[])
   */
  @Override
  public void debug(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.debug() called on an already closed Context", null);
    mFactory.internalReportDebug(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#debugWithMeta(java.lang.String, java.lang.Object[])
   */
  @Override
  public void debugWithMeta(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.debug() called on an already closed Context", null);
    mFactory.internalReportDebugWithMeta(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#isDebugEnabled()
   */
  @Override
  public boolean isDebugEnabled() {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.isDebugEnabled() called on an already closed Context", null);
    return mFactory.internalIsDebugEnabled(this);
  }

  /**
   * @see com.diamondq.common.context.Context#info(java.lang.String, java.lang.Object[])
   */
  @Override
  public void info(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.info() called on an already closed Context", null);
    mFactory.internalReportInfo(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#isInfoEnabled()
   */
  @Override
  public boolean isInfoEnabled() {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.isInfoEnabled() called on an already closed Context", null);
    return mFactory.internalIsInfoEnabled(this);
  }

  /**
   * @see com.diamondq.common.context.Context#warn(java.lang.String, java.lang.Object[])
   */
  @Override
  public void warn(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.warn() called on an already closed Context", null);
    mFactory.internalReportWarn(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#isWarnEnabled()
   */
  @Override
  public boolean isWarnEnabled() {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.isWarnEnabled() called on an already closed Context", null);
    return mFactory.internalIsWarnEnabled(this);
  }

  /**
   * @see com.diamondq.common.context.Context#error(java.lang.String, java.lang.Object[])
   */
  @Override
  public void error(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.error() called on an already closed Context", null);
    mFactory.internalReportError(this, pMessage, pArgs);
  }

  /**
   * @see com.diamondq.common.context.Context#error(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void error(String pMessage, @Nullable Throwable pThrowable) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.error() called on an already closed Context", null);
    mFactory.internalReportError(this, pMessage, pThrowable);
  }

  /**
   * @see com.diamondq.common.context.Context#isErrorEnabled()
   */
  @Override
  public boolean isErrorEnabled() {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.isErrorEnabled() called on an already closed Context", null);
    return mFactory.internalIsErrorEnabled(this);
  }

  /**
   * @see com.diamondq.common.context.Context#reportThrowable(java.lang.Throwable)
   */
  @Override
  public RuntimeException reportThrowable(Throwable pThrowable) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.reportThrowable() called on an already closed Context", null);
    RuntimeException result = mFactory.internalReportThrowable(this, pThrowable);
    return result;
  }

  /**
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() {

    /* Only actually close if we've reached 0. */

    int count = mOpenCount.decrementAndGet();
    if (count == 0)
      mFactory.closeContext(this);
    else if (count > 0) {
      mFactory.detachContextFromThread(this);
    }
    else {
      mOpenCount.set(0);
      mFactory.internalReportWarn(this, "Context.close() called on an already closed Context", null);
    }
  }

  /**
   * @see com.diamondq.common.context.Context#prepareForAlternateThreads()
   */
  @Override
  public void prepareForAlternateThreads() {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.prepareForAlternateThreads() called on an already closed Context",
        null);
    else
      mOpenCount.incrementAndGet();
  }

  /**
   * @see com.diamondq.common.context.Context#activateOnThread(java.lang.String, java.lang.Object[])
   */
  @Override
  public Context activateOnThread(String pMessage, @Nullable Object @Nullable... pArgs) {
    if (mOpenCount.get() <= 0)
      mFactory.internalReportWarn(this, "Context.activateOnThread() called on an already closed Context", null);
    else {
      mFactory.attachContextToThread(this);
      if (pMessage.isEmpty() == false)
        trace(pMessage, pArgs);
    }
    return this;
  }

  /**
   * @see com.diamondq.common.context.Context#forceClose()
   */
  @Override
  public void forceClose() {
    if (mOpenCount.get() > 0)
      mOpenCount.set(1);
    close();
  }

  @SuppressWarnings("resource")
  public List<String> getContextStackNames(boolean pPrintRefCount) {
    if ((pPrintRefCount == false) && (mContextStackNames != null))
      return mContextStackNames;
    List<String> contextStackNames = new ArrayList<>();
    ContextClass context = this;
    while (context != null) {
      if (pPrintRefCount == true) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.contextName);
        sb.append('(');
        sb.append(context.mOpenCount.get());
        sb.append(')');
        contextStackNames.add(sb.toString());
      }
      else
        contextStackNames.add(context.contextName);
      context = context.mParentContextClass;
    }
    Collections.reverse(contextStackNames);
    if (pPrintRefCount == false)
      mContextStackNames = contextStackNames;
    return contextStackNames;
  }

  public @Nullable ContextClass getParentContextClass() {
    return mParentContextClass;
  }

  /**
   * @see com.diamondq.common.context.Context#getRootContext()
   */
  @Override
  public Context getRootContext() {
    ContextClass current = this;
    while (true) {
      ContextClass parent = current.getParentContextClass();
      if (parent == current)
        throw new IllegalStateException();
      if (parent == null)
        return current;
      current = parent;
    }
  }
}

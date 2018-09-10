package com.diamondq.common.utils.context;

import com.diamondq.common.utils.context.impl.ContextFactoryImpl;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContextFactory {

  public static ContextFactory getInstance() {
    return ContextFactoryImpl.sINSTANCE;
  }

  public static Context currentContext() {
    return getInstance().getCurrentContext();
  }

  public static void staticReportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    getInstance().reportTrace(pClass, pThis, pArgs);
  }

  public static RuntimeException staticReportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable) {
    return getInstance().reportThrowable(pClass, pThis, pThrowable);
  }

  public Context newContextWithMeta(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs);

  public Context newContext(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs);

  /**
   * Returns the current context within the current thread. If there is no context, then a NoopContext is returned
   * 
   * @return the context
   */
  public Context getCurrentContext();

  /**
   * Report an exception outside of a context. It will automatically create a context, report the exception and then end
   * the context.
   * 
   * @param pClass the class
   * @param pThis the this object
   * @param pThrowable the exception
   * @return a RuntimeException that can be immediately thrown
   */
  public RuntimeException reportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable);

  /**
   * Report an exception outside of a context. It will automatically create a context, report the exception and then end
   * the context.
   * 
   * @param pClass the class
   * @param pThis the this object
   * @param pArgs the arguments
   */
  public void reportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs);

}

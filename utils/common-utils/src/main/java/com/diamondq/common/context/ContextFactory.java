package com.diamondq.common.context;

import com.diamondq.common.context.impl.ContextFactoryImpl;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.future.FutureUtils;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContextFactory {

  public static ContextFactory getInstance() {
    return ContextFactoryImpl.sINSTANCE;
  }

  public static Context currentContext() {
    return getInstance().getCurrentContext();
  }

  public static @Nullable Context nullableCurrentContext() {
    return getInstance().getNullableCurrentContext();
  }

  public static <T> ContextExtendedCompletableFuture<T> completedFuture(T pValue) {
    return FutureUtils.completedFuture(pValue);
  }

  public static <T> ContextExtendedCompletableFuture<T> completedFailure(Throwable pValue) {
    return FutureUtils.completedFailure(pValue);
  }

  public static <T> ContextExtendedCompletableFuture<T> of(CompletionStage<T> pFuture) {
    return FutureUtils.of(pFuture);
  }

  public static <T> ContextExtendedCompletableFuture<T> newCompletableFuture() {
    return FutureUtils.newCompletableFuture();
  }

  public static <T> ContextExtendedCompletableFuture<List<T>> listOf(
    List<@NonNull ? extends @NonNull ExtendedCompletionStage<T>> pList) {
    return FutureUtils.listOf(pList);
  }

  public static void staticReportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    getInstance().reportTrace(pClass, pThis, pArgs);
  }

  public static void staticReportTrace(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable... pArgs) {
    getInstance().reportTrace(pClass, pThis, pMessage, pArgs);
  }

  public static RuntimeException staticReportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable) {
    return getInstance().reportThrowable(pClass, pThis, pThrowable);
  }

  /**
   * This represents the ENTRY of a context. It MUST be matched with a corresponding EXIT (even under Exceptions). This
   * differs from entry in that each argument must be followed with a @Nullable Function<@Nullable Object, @Nullable
   * Object> that is called to convert the argument into what is displayed. Usually the Function constants provided by
   * ContextPrinters are used. For example newContextWithMeta(myClass, this, byteArray, ContextPrinters.sBytesType,
   * hashData, ContextPrinters.sHashType). Provide a null function if conversion isn't necessary. NOTE: The last value
   * in pArgs can be an Exception in which case it doesn't have a corresponding conversion.
   *
   * @param pClass the class
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   * @return the context
   */
  public Context newContextWithMeta(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs);

  /**
   * This represents the ENTRY of a context. It MUST be matched with a corresponding EXIT (even under Exceptions).
   *
   * @param pClass the class
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   * @return the new context
   */
  public Context newContext(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable... pArgs);

  /**
   * Returns the current context within the current thread. If there is no context, then a NoopContext is returned
   * 
   * @return the context
   */
  public Context getCurrentContext();

  /**
   * Returns the current context within the current thread. If there is no context, then null is returned.
   * 
   * @return the context
   */
  public @Nullable Context getNullableCurrentContext();

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

  /**
   * Report an exception outside of a context. It will automatically create a context, report the exception and then end
   * the context.
   * 
   * @param pClass the class
   * @param pThis the this object
   * @param pMessage the message
   * @param pArgs the arguments
   */
  public void reportTrace(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable... pArgs);

  /**
   * Report an debug outside of a context. It will automatically create a context, report the debug and then end the
   * context.
   * 
   * @param pClass the class
   * @param pThis the this object
   * @param pMessage the message
   * @param pArgs the arguments
   */
  public void reportDebug(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable... pArgs);

}

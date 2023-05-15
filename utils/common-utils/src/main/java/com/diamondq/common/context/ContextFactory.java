package com.diamondq.common.context;

import com.diamondq.common.context.impl.ContextFactoryImpl;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.future.FutureUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ContextFactory {

  static ContextFactory getInstance() {
    return ContextFactoryImpl.sINSTANCE;
  }

  static Context currentContext() {
    return getInstance().getCurrentContext();
  }

  static @Nullable Context nullableCurrentContext() {
    return getInstance().getNullableCurrentContext();
  }

  static <T> ContextExtendedCompletableFuture<T> completedFuture(T pValue) {
    return FutureUtils.completedFuture(pValue);
  }

  static <T> ContextExtendedCompletableFuture<T> completedFailure(Throwable pValue) {
    return FutureUtils.completedFailure(pValue);
  }

  static <T> ContextExtendedCompletableFuture<T> of(CompletionStage<T> pFuture) {
    return FutureUtils.of(pFuture);
  }

  static <T> ContextExtendedCompletableFuture<T> newCompletableFuture() {
    return FutureUtils.newCompletableFuture();
  }

  static <T> ContextExtendedCompletableFuture<List<T>> listOf(
    List<@NotNull ? extends @NotNull ExtendedCompletionStage<T>> pList) {
    return FutureUtils.listOf(pList);
  }

  static void staticReportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs) {
    getInstance().reportTrace(pClass, pThis, pArgs);
  }

  static void staticReportTrace(Class<?> pClass, @Nullable Object pThis, String pMessage,
    @Nullable Object @Nullable ... pArgs) {
    getInstance().reportTrace(pClass, pThis, pMessage, pArgs);
  }

  static RuntimeException staticReportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable) {
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
  Context newContextWithMeta(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs);

  /**
   * This represents the ENTRY of a context. It MUST be matched with a corresponding EXIT (even under Exceptions).
   *
   * @param pClass the class
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   * @return the new context
   */
  Context newContext(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs);

  /**
   * Returns the current context within the current thread. If there is no context, then a NoopContext is returned
   *
   * @return the context
   */
  Context getCurrentContext();

  /**
   * Returns the current context within the current thread. If there is no context, then null is returned.
   *
   * @return the context
   */
  @Nullable Context getNullableCurrentContext();

  /**
   * Report an exception outside a context. It will automatically create a context, report the exception and then end
   * the context.
   *
   * @param pClass the class
   * @param pThis the caller object
   * @param pThrowable the exception
   * @return a RuntimeException that can be immediately thrown
   */
  RuntimeException reportThrowable(Class<?> pClass, @Nullable Object pThis, Throwable pThrowable);

  /**
   * Report an exception outside a context. It will automatically create a context, report the exception and then end
   * the context.
   *
   * @param pClass the class
   * @param pThis the caller object
   * @param pArgs the arguments
   */
  void reportTrace(Class<?> pClass, @Nullable Object pThis, @Nullable Object @Nullable ... pArgs);

  /**
   * Report an exception outside a context. It will automatically create a context, report the exception and then end
   * the context.
   *
   * @param pClass the class
   * @param pThis the caller object
   * @param pMessage the message
   * @param pArgs the arguments
   */
  void reportTrace(Class<?> pClass, @Nullable Object pThis, String pMessage, @Nullable Object @Nullable ... pArgs);

  /**
   * Report a debug outside a context. It will automatically create a context, report the debug and then end the
   * context.
   *
   * @param pClass the class
   * @param pThis the caller object
   * @param pMessage the message
   * @param pArgs the arguments
   */
  void reportDebug(Class<?> pClass, @Nullable Object pThis, String pMessage, @Nullable Object @Nullable ... pArgs);

  /**
   * Report an info outside a context. It will automatically create a context, report the info and then end the
   * context.
   *
   * @param pClass the class
   * @param pThis the caller object
   * @param pMessage the message
   * @param pArgs the arguments
   */
  void reportInfo(Class<?> pClass, @Nullable Object pThis, String pMessage, @Nullable Object @Nullable ... pArgs);

  /**
   * Report a "warn" outside a context. It will automatically create a context, report the "warn" and then end the
   * context.
   *
   * @param pClass the class
   * @param pThis the caller object
   * @param pMessage the message
   * @param pArgs the arguments
   */
  void reportWarn(Class<?> pClass, @Nullable Object pThis, String pMessage, @Nullable Object @Nullable ... pArgs);

}

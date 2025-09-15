package com.diamondq.common.lambda.future;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FutureUtils {

  private static volatile Method ofFutureMethod;

  private static volatile Method newCompletableFutureMethod;

  private static volatile Method completedFutureMethod;

  private static volatile Method failedFutureMethod;

  private static volatile Method listOfFutureMethod;

  private static volatile Class<?> completedClass;

  static {
    try {
      ofFutureMethod = ExtendedCompletableFuture.class.getDeclaredMethod("of", CompletableFuture.class);
      newCompletableFutureMethod = ExtendedCompletableFuture.class.getDeclaredMethod("newCompletableFuture");
      completedFutureMethod = ExtendedCompletableFuture.class.getDeclaredMethod("completedFuture", Object.class);
      failedFutureMethod = ExtendedCompletableFuture.class.getDeclaredMethod("completedFailure", Throwable.class);
      listOfFutureMethod = ExtendedCompletableFuture.class.getDeclaredMethod("listOf", List.class);
      completedClass = ExtendedCompletableFuture.class;
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static boolean setMethods(Method pOfFutureMethod, Method pNewCompletabledFutureMethod,
    Method pCompletedFutureMethod, Method pCompletedFailureMethod, Method pListOfFutureMethod, Class<?> pCompletedClass,
    Set<Class<?>> pValidReplacements) {

    synchronized (FutureUtils.class) {
      if (!pValidReplacements.contains(completedClass)) return false;
      ofFutureMethod = pOfFutureMethod;
      newCompletableFutureMethod = pNewCompletabledFutureMethod;
      completedFutureMethod = pCompletedFutureMethod;
      failedFutureMethod = pCompletedFailureMethod;
      listOfFutureMethod = pListOfFutureMethod;
      completedClass = pCompletedClass;
      return true;
    }
  }

  /**
   * Returns a completed future with the given value. NOTE: This is the best class to use to create futures, since more
   * enhanced versions can be inserted into the creation cycle (such as ContextExtendedCompletedFuture or
   * VertxContextExtendedCompletedFuture)
   *
   * @param pValue the value
   * @return the future
   */
  public static <T extends @Nullable Object, U extends ExtendedCompletableFuture<T>> U completedFuture(T pValue) {
    try {
      Object resultObj = completedFutureMethod.invoke(null, pValue);
      @SuppressWarnings("unchecked") U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Returns a completed future with the given value. NOTE: This is the best class to use to create futures, since more
   * enhanced versions can be inserted into the creation cycle (such as ContextExtendedCompletedFuture or
   * VertxContextExtendedCompletedFuture)
   *
   * @param pValue the value
   * @return the future
   */
  public static <T extends @Nullable Object, U extends ExtendedCompletableFuture<T>> U completedFailure(
    Throwable pValue) {
    try {
      Object resultObj = failedFutureMethod.invoke(null, pValue);
      @SuppressWarnings("unchecked") U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Returns a completable future for the given CompletionStage. NOTE: This is the best class to wrap an existing
   * CompletionStage/CompletableFuture since more enhanced versions can be inserted into the creation cycle.
   *
   * @param <T> the type
   * @param pFuture the existing future
   * @return the extended future
   */
  public static <T extends @Nullable Object, U extends ExtendedCompletableFuture<T>> U of(CompletionStage<T> pFuture) {
    CompletableFuture<T> future;
    if (pFuture instanceof CompletableFuture) future = (CompletableFuture<T>) pFuture;
    else future = pFuture.toCompletableFuture();
    try {
      Object resultObj = ofFutureMethod.invoke(null, future);
      @SuppressWarnings("unchecked") U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T extends @Nullable Object, U extends ExtendedCompletableFuture<T>> U newCompletableFuture() {
    try {
      Object resultObj = newCompletableFutureMethod.invoke(null);
      @SuppressWarnings("unchecked") U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T extends @Nullable Object, U extends ExtendedCompletionStage<List<T>>> U listOf(
    List<? extends ExtendedCompletionStage<T>> pList) {
    try {
      Object resultObj = listOfFutureMethod.invoke(null, pList);
      @SuppressWarnings("unchecked") U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }
}

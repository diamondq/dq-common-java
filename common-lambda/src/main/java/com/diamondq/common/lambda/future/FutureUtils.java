package com.diamondq.common.lambda.future;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class FutureUtils {

  private static volatile Method   newCompletableFutureMethod;

  private static volatile Method   completedFutureMethod;

  private static volatile Method   failedFutureMethod;

  private static volatile Method   listOfFutureMethod;

  private static volatile Class<?> completedClass;

  static {
    try {
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

  public static boolean setMethods(Method pNewCompletabledFutureMethod, Method pCompletedFutureMethod,
    Method pCompletedFailureMethod, Method pListOfFutureMethod, Class<?> pCompletedClass,
    Set<Class<?>> pValidReplacements) {

    synchronized (FutureUtils.class) {
      if (pValidReplacements.contains(completedClass) == false)
        return false;
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
  public static <T, U extends ExtendedCompletableFuture<T>> U completedFuture(T pValue) {
    try {
      Object resultObj = completedFutureMethod.invoke(null, pValue);
      @SuppressWarnings("unchecked")
      U result = (U) resultObj;
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
  public static <T, U extends ExtendedCompletableFuture<T>> U completedFailure(Throwable pValue) {
    try {
      Object resultObj = failedFutureMethod.invoke(null, pValue);
      @SuppressWarnings("unchecked")
      U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T, U extends ExtendedCompletableFuture<T>> U newCompletableFuture() {
    try {
      Object resultObj = newCompletableFutureMethod.invoke(null);
      @SuppressWarnings("unchecked")
      U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T, U extends ExtendedCompletionStage<List<T>>> U listOf(
    List<? extends ExtendedCompletionStage<T>> pList) {
    try {
      Object resultObj = listOfFutureMethod.invoke(pList);
      @SuppressWarnings("unchecked")
      U result = (U) resultObj;
      return result;
    }
    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }
}

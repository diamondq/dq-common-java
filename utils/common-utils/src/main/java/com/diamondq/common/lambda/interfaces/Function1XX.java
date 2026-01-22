package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Function1XX<T1, R> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @return the result
   */
  R apply(T1 t1);
}

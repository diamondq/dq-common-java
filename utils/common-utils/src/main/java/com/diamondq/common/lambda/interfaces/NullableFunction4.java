package com.diamondq.common.lambda.interfaces;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface NullableFunction4<T1, T2, T3, T4, R> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   * @param t3 the input argument
   * @param t4 the input argument
   * @return the result
   */
  @Nullable R apply(T1 t1, T2 t2, T3 t3, T4 t4);
}

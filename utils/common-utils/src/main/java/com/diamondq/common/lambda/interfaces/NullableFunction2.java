package com.diamondq.common.lambda.interfaces;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface NullableFunction2<T1, T2, R> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   * @return the result
   */
  @Nullable R apply(T1 t1, T2 t2);
}

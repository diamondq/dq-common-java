package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Function3<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, R extends @Nullable Object> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   * @param t3 the input argument
   * @return the result
   */
  R apply(T1 t1, T2 t2, T3 t3);
}

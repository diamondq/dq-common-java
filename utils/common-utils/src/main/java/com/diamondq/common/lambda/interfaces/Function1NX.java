package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Function1NX<T1 extends @Nullable Object, R> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @return the result
   */
  R apply(@Nullable T1 t1);
}

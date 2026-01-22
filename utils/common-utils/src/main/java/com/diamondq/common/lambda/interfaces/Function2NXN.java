package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Function2NXN<T1 extends @Nullable Object, T2, R extends @Nullable Object>
  extends NullableFunction2<T1, T2, R> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   * @return the result
   */
  @Nullable R apply(@Nullable T1 t1, T2 t2);
}

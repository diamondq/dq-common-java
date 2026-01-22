package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Function2NNN<T1 extends @Nullable Object, T2 extends @Nullable Object, R extends @Nullable Object>
  extends NullableFunction2<T1, T2, R> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   * @return the result
   */
  @Nullable R apply(@Nullable T1 t1, @Nullable T2 t2);
}

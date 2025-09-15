package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface NullableFunction1<T1 extends @Nullable Object, R extends @Nullable Object> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @return the result
   */
  @Nullable
  R apply(T1 t1);
}

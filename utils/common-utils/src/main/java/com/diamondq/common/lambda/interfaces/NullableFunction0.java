package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface NullableFunction0<R extends @Nullable Object> {

  /**
   * Performs this operation
   *
   * @return the result
   */
  @Nullable
  R apply();
}

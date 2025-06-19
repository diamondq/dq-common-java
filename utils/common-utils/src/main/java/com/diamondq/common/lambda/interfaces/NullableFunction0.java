package com.diamondq.common.lambda.interfaces;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface NullableFunction0<R> {

  /**
   * Performs this operation
   *
   * @return the result
   */
  @Nullable R apply();
}

package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Function0<R extends @Nullable Object> {

  /**
   * Performs this operation
   *
   * @return the result
   */
  R apply();
}

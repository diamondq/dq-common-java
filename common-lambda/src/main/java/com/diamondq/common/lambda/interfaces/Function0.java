package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Function0<R> {

  /**
   * Performs this operation
   *
   * @return the result
   */
  R apply();
}

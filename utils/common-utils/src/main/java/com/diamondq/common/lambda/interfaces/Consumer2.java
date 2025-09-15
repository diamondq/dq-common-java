package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Consumer2<T1 extends @Nullable Object, T2 extends @Nullable Object> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   */
  void accept(T1 t1, T2 t2);
}

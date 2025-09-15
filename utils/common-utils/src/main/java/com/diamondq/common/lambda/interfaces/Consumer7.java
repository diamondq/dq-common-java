package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Consumer7<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object, T7 extends @Nullable Object> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   * @param t3 the input argument
   * @param t4 the input argument
   * @param t5 the input argument
   * @param t6 the input argument
   * @param t7 the input argument
   */
  void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
}

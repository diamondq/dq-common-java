package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Consumer2<T1, T2> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the input argument
   * @param t2 the input argument
   */
  void accept(T1 t1, T2 t2);
}

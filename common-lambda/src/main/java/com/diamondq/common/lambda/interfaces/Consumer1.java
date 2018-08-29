package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Consumer1<T> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   */
  void accept(T t);
}

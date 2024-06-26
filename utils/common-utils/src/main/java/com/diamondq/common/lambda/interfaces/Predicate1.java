package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Predicate1<T1> {

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param t the input argument
   * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
   */
  boolean test(T1 t);

}

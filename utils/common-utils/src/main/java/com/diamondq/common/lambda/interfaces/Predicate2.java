package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Predicate2<T1, T2> {

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param t the input argument
   * @param t2 the second input argument
   * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
   */
  boolean test(T1 t, T2 t2);

}

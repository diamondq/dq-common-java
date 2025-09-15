package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Predicate2<T1 extends @Nullable Object, T2 extends @Nullable Object> {

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param t the input argument
   * @param t2 the second input argument
   * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
   */
  boolean test(T1 t, T2 t2);

}

package com.diamondq.common.lambda.interfaces;

@FunctionalInterface
public interface Supplier<T> {

  public T get();
}

package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Supplier<T extends @Nullable Object> {

  T get();
}

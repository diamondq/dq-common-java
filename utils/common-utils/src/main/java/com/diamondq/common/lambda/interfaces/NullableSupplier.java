package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface NullableSupplier<T extends @Nullable Object> {

  @Nullable
  T get();
}

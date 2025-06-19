package com.diamondq.common.lambda.interfaces;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface NullableSupplier<T> {

  @Nullable T get();
}

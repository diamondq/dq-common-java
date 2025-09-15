package com.diamondq.common;

import org.jspecify.annotations.Nullable;

public class Holder<T extends @Nullable Object> {

  public volatile T object;

  public Holder(T pInitialValue) {
    object = pInitialValue;
  }

}

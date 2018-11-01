package com.diamondq.common.utils.misc;

public class Holder<T> {

  public volatile T object;

  public Holder(T pInitialValue) {
    object = pInitialValue;
  }

}

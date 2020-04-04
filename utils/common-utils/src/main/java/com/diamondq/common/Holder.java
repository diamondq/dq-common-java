package com.diamondq.common;

public class Holder<T> {

  public volatile T object;

  public Holder(T pInitialValue) {
    object = pInitialValue;
  }

}

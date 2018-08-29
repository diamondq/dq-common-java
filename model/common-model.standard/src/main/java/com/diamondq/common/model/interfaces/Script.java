package com.diamondq.common.model.interfaces;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Script {

  public @Nullable Object evaluate(Property<?> pProperty);

}

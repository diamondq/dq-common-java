package com.diamondq.common.model.interfaces;

import org.jetbrains.annotations.Nullable;

public interface Script {

  public @Nullable Object evaluate(Property<?> pProperty);

}

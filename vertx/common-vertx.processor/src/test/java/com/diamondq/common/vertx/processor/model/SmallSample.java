package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.utils.context.ContextExtendedCompletionStage;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface SmallSample {

  public ContextExtendedCompletionStage<@Nullable Void> withDouble(double pDouble);

}

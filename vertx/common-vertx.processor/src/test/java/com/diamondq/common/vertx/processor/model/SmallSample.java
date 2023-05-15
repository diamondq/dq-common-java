package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import org.jetbrains.annotations.Nullable;

public interface SmallSample {

  public ContextExtendedCompletionStage<@Nullable Void> withDouble(double pDouble);

}

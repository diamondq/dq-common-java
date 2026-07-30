package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import org.jspecify.annotations.Nullable;

public interface SmallSample {

  ContextExtendedCompletionStage<Void> withDouble(double pDouble);

}

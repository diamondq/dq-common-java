package com.diamondq.common.vertx.processor.tests;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.vertx.annotations.ProxyGen;
import org.jspecify.annotations.Nullable;

@ProxyGen
public interface SimpleProxy {

  ContextExtendedCompletionStage<String> getName();

  ContextExtendedCompletionStage<Void> setName(String pValue);

  ContextExtendedCompletionStage<Void> setTitle(@Nullable String pValue);

  ContextExtendedCompletionStage<Void> setWidth(int pWidth);

  ContextExtendedCompletionStage<Void> setHeight(short pWidth);

  ContextExtendedCompletionStage<Void> save();

}

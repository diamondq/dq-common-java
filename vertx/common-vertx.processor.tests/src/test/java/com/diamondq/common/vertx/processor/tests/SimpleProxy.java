package com.diamondq.common.vertx.processor.tests;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.vertx.annotations.ProxyGen;
import org.jspecify.annotations.Nullable;

@ProxyGen
public interface SimpleProxy {

  ContextExtendedCompletionStage<String> getName();

  ContextExtendedCompletionStage<@Nullable Void> setName(String pValue);

  ContextExtendedCompletionStage<@Nullable Void> setTitle(@Nullable String pValue);

  ContextExtendedCompletionStage<@Nullable Void> setWidth(int pWidth);

  ContextExtendedCompletionStage<@Nullable Void> setHeight(short pWidth);

  ContextExtendedCompletionStage<@Nullable Void> save();

}

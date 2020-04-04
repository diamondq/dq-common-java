package com.diamondq.common.vertx.processor.tests;

import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.vertx.annotations.ProxyGen;

import org.checkerframework.checker.nullness.qual.Nullable;

@ProxyGen
public interface SimpleProxy {

  public ContextExtendedCompletionStage<String> getName();

  public ContextExtendedCompletionStage<@Nullable Void> setName(String pValue);

  public ContextExtendedCompletionStage<@Nullable Void> setTitle(@Nullable String pValue);

  public ContextExtendedCompletionStage<@Nullable Void> setWidth(int pWidth);

  public ContextExtendedCompletionStage<@Nullable Void> setHeight(short pWidth);

  public ContextExtendedCompletionStage<@Nullable Void> save();

}

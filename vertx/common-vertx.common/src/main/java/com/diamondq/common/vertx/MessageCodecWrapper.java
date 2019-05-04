package com.diamondq.common.vertx;

import io.vertx.core.eventbus.MessageCodec;

public interface MessageCodecWrapper<I, S> {

  public Class<I> getCodecClass();

  public MessageCodec<S, S> getMessageCodec();
}

package com.diamondq.common.vertx;

import io.vertx.core.eventbus.MessageCodec;

public abstract class BaseMessageCodecWrapper<I, S> implements MessageCodecWrapper<I, S> {

  private final Class<I>           mClass;

  private final MessageCodec<S, S> mMessageCodec;

  public BaseMessageCodecWrapper(Class<I> pClass, MessageCodec<S, S> pMessageCodec) {
    super();
    mClass = pClass;
    mMessageCodec = pMessageCodec;
  }

  /**
   * @see com.diamondq.common.vertx.MessageCodecWrapper#getCodecClass()
   */
  @Override
  public Class<I> getCodecClass() {
    return mClass;
  }

  /**
   * @see com.diamondq.common.vertx.MessageCodecWrapper#getMessageCodec()
   */
  @Override
  public MessageCodec<S, S> getMessageCodec() {
    return mMessageCodec;
  }

}

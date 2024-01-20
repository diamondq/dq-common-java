package com.diamondq.common.vertx;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

public class MapMessageCodec implements MessageCodec<Map<Object, Object>, Map<Object, Object>> {

  @SuppressWarnings("rawtypes")
  @Singleton
  public static class HashMapMessageCodec extends BaseMessageCodecWrapper<HashMap, Map<Object, Object>> {

    public HashMapMessageCodec() {
      super(HashMap.class, new MapMessageCodec());
    }

  }

  /**
   * @see io.vertx.core.eventbus.MessageCodec#encodeToWire(io.vertx.core.buffer.Buffer, java.lang.Object)
   */
  @Override
  public void encodeToWire(Buffer pBuffer, Map<Object, Object> pS) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see io.vertx.core.eventbus.MessageCodec#decodeFromWire(int, io.vertx.core.buffer.Buffer)
   */
  @Override
  public Map<Object, Object> decodeFromWire(int pPos, Buffer pBuffer) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see io.vertx.core.eventbus.MessageCodec#transform(java.lang.Object)
   */
  @Override
  public Map<Object, Object> transform(Map<Object, Object> pS) {
    return pS;
  }

  /**
   * @see io.vertx.core.eventbus.MessageCodec#name()
   */
  @Override
  public String name() {
    return "generic-map";
  }

  /**
   * @see io.vertx.core.eventbus.MessageCodec#systemCodecID()
   */
  @Override
  public byte systemCodecID() {
    return -1;
  }
}

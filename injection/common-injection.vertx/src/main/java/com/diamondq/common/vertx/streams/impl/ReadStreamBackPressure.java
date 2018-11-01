package com.diamondq.common.vertx.streams.impl;

import com.diamondq.common.vertx.streams.BackPressure;

import io.vertx.core.streams.ReadStream;

public class ReadStreamBackPressure implements BackPressure {
  private final ReadStream<?> mStream;

  public ReadStreamBackPressure(ReadStream<?> pStream) {
    mStream = pStream;
  }

  /**
   * @see com.diamondq.common.vertx.streams.BackPressure#pause()
   */
  @Override
  public void pause() {
    mStream.pause();
  }

  /**
   * @see com.diamondq.common.vertx.streams.BackPressure#resume()
   */
  @Override
  public void resume() {
    mStream.resume();
  }
}

package com.diamondq.common.vertx.streams;

public interface BackPressure {

  public void pause();
  
  public void resume();
}

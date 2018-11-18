package com.diamondq.common.vertx;

public class EventServiceImpl implements EventService {

  private final String mAddress;

  public EventServiceImpl(String pAddress) {
    mAddress = pAddress;
  }

  /**
   * @see com.diamondq.common.vertx.EventService#getAddress()
   */
  @Override
  public String getAddress() {
    return mAddress;
  }

}

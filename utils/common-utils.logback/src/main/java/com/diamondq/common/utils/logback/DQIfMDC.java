package com.diamondq.common.utils.logback;

import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class DQIfMDC extends DQMDC {

  protected String mValue;

  @SuppressWarnings("null")
  public DQIfMDC() {
  }

  /**
   * @see com.diamondq.common.utils.logback.DQMDC#start()
   */
  @Override
  public void start() {
    List<String> optionList = getOptionList();
    if ((optionList == null) || (optionList.isEmpty())) {
      mValue = "";
      super.processOptionList(optionList);
    }
    else {
      mValue = optionList.remove(optionList.size() - 1);
      mValue = mValue.replaceAll("\\\\s", " ");
      super.processOptionList(optionList);
    }
  }

  /**
   * @see ch.qos.logback.core.pattern.Converter#convert(java.lang.Object)
   */
  @Override
  public String convert(ILoggingEvent pEvent) {
    String result = super.convert(pEvent);
    if (result.isEmpty() == true)
      return "";
    return mValue;
  }
}

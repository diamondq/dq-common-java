package com.diamondq.common.utils.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.List;

public class DQLogger extends ClassicConverter {

  private int mParts;

  /**
   * @see ch.qos.logback.core.pattern.DynamicConverter#start()
   */
  @Override
  public void start() {
    List<String> optionList = getOptionList();
    if ((optionList == null) || (optionList.isEmpty())) mParts = 3;
    else mParts = Integer.parseInt(optionList.get(0));
    super.start();
  }

  /**
   * @see ch.qos.logback.core.pattern.Converter#convert(java.lang.Object)
   */
  @Override
  public String convert(ILoggingEvent pEvent) {
    String loggerName = pEvent.getLoggerName();
    String[] parts = loggerName.split("\\.");
    int fullStart;
    StringBuilder sb = new StringBuilder();
    if (parts.length > mParts) {
      fullStart = parts.length - mParts;
      for (int i = 0; i < fullStart; i++)
        sb.append(parts[i].charAt(0)).append('.');
    } else fullStart = 0;
    for (int i = fullStart; i < parts.length; i++) {
      sb.append(parts[i]);
      if (i < (parts.length - 1)) sb.append('.');
    }
    return sb.toString();
  }
}

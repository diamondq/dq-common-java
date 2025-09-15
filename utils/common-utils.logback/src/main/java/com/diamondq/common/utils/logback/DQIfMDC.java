package com.diamondq.common.utils.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * This LogBack converter will output the final entry in the list if there is an MDC property that matches the property.
 * (i.e., DQIfMDC{myKey,something} will output something if myKey exists as an MDC property).
 */
public class DQIfMDC extends DQMDC {

  protected @Nullable String mValue;

  @SuppressWarnings("null")
  public DQIfMDC() {
  }

  @Override
  public void start() {
    List<String> optionList = getOptionList();
    if ((optionList == null) || (optionList.isEmpty())) {
      mValue = "";
      super.processOptionList(optionList);
    } else {
      mValue = optionList.remove(optionList.size() - 1);
      mValue = mValue.replaceAll("\\\\s", " ");
      super.processOptionList(optionList);
    }
  }

  @Override
  public String convert(ILoggingEvent pEvent) {
    String result = super.convert(pEvent);
    if (result.isEmpty()) return "";
    return mValue == null ? "" : mValue;
  }
}

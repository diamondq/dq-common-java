package com.diamondq.common.utils.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * This LogBack converter output the time from the provided MDC properties (if they exist). If the MDC property does not
 * exist, then the time from the log entry will be used. The first entry of the configuration is the formatting of the
 * DataTimeFormatter.
 */
public class DQTime extends ClassicConverter {

  protected @Nullable List<String>      mKeys;
  protected @Nullable DateTimeFormatter mFormat;

  @SuppressWarnings("null")
  public DQTime() {
  }

  @Override
  public void start() {
    processOptionList(getOptionList());
  }

  protected void processOptionList(@Nullable List<String> pOptionList) {
    List<String> keys = new ArrayList<>();
    if (pOptionList != null) keys.addAll(pOptionList);
    if (keys.isEmpty()) keys.add("yyyy-MM-dd HH:mm:ss.SSS");
    mFormat = DateTimeFormatter.ofPattern(keys.remove(0), Locale.ENGLISH).withZone(ZoneId.systemDefault());
    mKeys = Collections.unmodifiableList(keys);
    super.start();
  }

  @Override
  public String convert(ILoggingEvent pEvent) {
    long logTime = pEvent.getTimeStamp();
    var map = pEvent.getMDCPropertyMap();
    if (map != null && mKeys != null) {
      for (var key : mKeys) {
        var value = map.get(key);
        if (value != null) logTime = Long.parseLong(value);
      }
    }

    /* Now format the time */

    if (mFormat == null) return "N/A";

    return mFormat.format(Instant.ofEpochMilli(logTime));
  }
}

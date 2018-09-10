package com.diamondq.common.utils.logback;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.checkerframework.checker.nullness.qual.Nullable;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;

public class DQMDC extends ClassicConverter {

  protected Map<String, @Nullable String> mKeep;

  protected Set<String>                   mOmit;

  @SuppressWarnings("null")
  public DQMDC() {
  }

  @Override
  public void start() {
    processOptionList(getOptionList());
  }

  protected void processOptionList(@Nullable List<String> pOptionList) {
    Set<String> omits = new HashSet<>();
    Map<String, @Nullable String> keeps = new LinkedHashMap<>();
    if (pOptionList != null)
      for (String option : pOptionList) {
        String[] optionInfo = OptionHelper.extractDefaultReplacement(option);
        String key = optionInfo[0];
        if (key != null) {
          if (key.startsWith("!"))
            omits.add(key.substring(1));
          else
            keeps.put(key, optionInfo[1]);
        }
      }
    mKeep = Collections.unmodifiableMap(keeps);
    mOmit = Collections.unmodifiableSet(omits);
    super.start();
  }

  /**
   * @see ch.qos.logback.core.pattern.Converter#convert(java.lang.Object)
   */
  @Override
  public String convert(ILoggingEvent pEvent) {
    Map<String, @Nullable String> mdcPropertyMap = pEvent.getMDCPropertyMap();

    StringBuilder sb = new StringBuilder();
    TreeSet<String> keys = new TreeSet<>();
    if (mKeep.isEmpty() == true)
      keys.addAll(mdcPropertyMap.keySet());
    else
      keys.addAll(mKeep.keySet());
    mOmit.forEach((k) -> keys.remove(k));
    boolean onlyOne = mKeep.size() == 1;
    boolean first = true;
    for (String key : keys) {
      if (first)
        first = false;
      else
        sb.append(", ");

      // format: key0=value0, key1=value1
      String r = mdcPropertyMap.get(key);
      if ((r != null) && ("DQIndent".equals(key))) {
        String mdcThreadName = mdcPropertyMap.get("DQT");
        String threadName = Thread.currentThread().getName();
        if (threadName.equals(mdcThreadName) == false)
          r = null;
      }
      if (r == null)
        r = mKeep.get(key);
      if (r == null)
        r = "";
      if (onlyOne == false)
        sb.append(key).append('=');
      sb.append(r);
    }

    return sb.toString();
  }
}

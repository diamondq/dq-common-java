package com.diamondq.common.utils.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.OptionHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This LogBack converter will output the MDC properties. The configuration is a comma-separated list of keys. If the
 * keys start with a !, then they will be omitted. If the key is listed, then the value will be used. The key can
 * contain a default value by using the ":-" delimiter (i.e., DQMDC{myKey:-defaultValue} )
 */
@SuppressWarnings("SpellCheckingInspection")
public class DQMDC extends ClassicConverter {

  protected @Nullable Map<String, @Nullable String> mKeep;

  protected @Nullable Set<String> mOmit;

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
    if (pOptionList != null) for (String option : pOptionList) {
      @Nullable String[] optionInfo = OptionHelper.extractDefaultReplacement(option);
      String key = optionInfo[0];
      if (key != null) {
        if (key.startsWith("!")) omits.add(key.substring(1));
        else keeps.put(key, optionInfo[1]);
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
    Map<String, @Nullable String> keep = mKeep != null ? mKeep : Collections.emptyMap();
    Set<String> omit = mOmit != null ? mOmit : Collections.emptySet();
    if (keep.isEmpty()) keys.addAll(mdcPropertyMap.keySet());
    else keys.addAll(keep.keySet());
    omit.forEach(keys::remove);
    boolean onlyOne = keep.size() == 1;
    boolean first = true;
    for (String key : keys) {
      if (first) first = false;
      else sb.append(", ");

      // format: key0=value0, key1=value1
      @Nullable String r = mdcPropertyMap.get(key);
      if ((r != null) && ("DQIndent".equals(key))) {
        String mdcThreadName = mdcPropertyMap.get("DQT");
        String threadName = Thread.currentThread().getName();
        if (!threadName.equals(mdcThreadName)) r = null;
      }
      if (r == null) r = keep.get(key);
      if (r == null) r = "";
      if (!onlyOne) sb.append(key).append('=');
      sb.append(r);
    }

    return sb.toString();
  }
}

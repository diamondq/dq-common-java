package com.diamondq.common.utils.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.MdcPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Plugin(name = "DQMDCPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "dqmdc", "DQMDC" })
public class DQMDC extends LogEventPatternConverter {

  private final MdcPatternConverter mMDCPatternConverter;

  private final Set<String> mOmit;

  private static final String[] sEMPTY_PATTERN = new String[] { "___NEVER_MATCHES___" };

  /**
   * Private constructor.
   *
   * @param options options, may be null.
   */
  private DQMDC(final String @Nullable [] options) {
    super((options != null) && (options.length > 0) ? "DQMDC{" + options[0] + '}' : "DQMDC", "dqmdc");
    final Set<String> omits = new HashSet<>();
    if ((options != null) && (options.length > 0) && (options[0] != null)) {
      @NotNull String[] keys;
      if (options[0].indexOf(',') > 0) keys = options[0].split(",");
      else {
        final String opt = options[0];
        if (opt == null) throw new IllegalStateException();
        keys = new @NotNull String[] { opt };
      }
      final Set<String> keep = new HashSet<>();
      for (int i = 0; i < keys.length; i++) {
        keys[i] = keys[i].trim();
        if (keys[i].startsWith("!")) omits.add(keys[i].substring(1));
        else keep.add(keys[i]);
      }
      if (keep.isEmpty()) mMDCPatternConverter = MdcPatternConverter.newInstance(sEMPTY_PATTERN);
      else mMDCPatternConverter = MdcPatternConverter.newInstance(new String[] { String.join(",", keep) });
    } else mMDCPatternConverter = MdcPatternConverter.newInstance(sEMPTY_PATTERN);
    mOmit = Collections.unmodifiableSet(omits);
  }

  /**
   * Obtains an instance of pattern converter.
   *
   * @param pOptions options, may be null.
   * @return instance of pattern converter.
   */
  public static DQMDC newInstance(final String @Nullable [] pOptions) {
    return new DQMDC(pOptions);
  }

  /**
   * @see org.apache.logging.log4j.core.pattern.LogEventPatternConverter#format(org.apache.logging.log4j.core.LogEvent,
   *   java.lang.StringBuilder)
   */
  @Override
  public void format(LogEvent pEvent, StringBuilder pToAppendTo) {
    final ReadOnlyStringMap contextData = pEvent.getContextData();
    if (contextData.isEmpty() == true) mMDCPatternConverter.format(pEvent, pToAppendTo);
    else {
      final SortedArrayStringMap map = new SortedArrayStringMap(contextData);
      mOmit.forEach((k) -> map.remove(k));
      final MutableLogEvent updatedEvent = new MutableLogEvent();
      updatedEvent.initFrom(pEvent);
      updatedEvent.setContextData(map);
      mMDCPatternConverter.format(updatedEvent, pToAppendTo);
    }
  }
}

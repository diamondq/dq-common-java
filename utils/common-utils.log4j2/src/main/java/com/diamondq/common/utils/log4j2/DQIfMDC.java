package com.diamondq.common.utils.log4j2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.MdcPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Plugin(name = "DQIfMDCPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"DQIfMDC"})
public class DQIfMDC extends LogEventPatternConverter {
  /**
   * Singleton.
   */
  private static final DQIfMDC      INSTANCE       = new DQIfMDC(null);

  private final MdcPatternConverter mMDCPatternConverter;

  private final Set<String>         mOmit;

  protected String                  mValue         = "";

  private static final String[]     sEMPTY_PATTERN = new String[] {"___NEVER_MATCHES___"};

  /**
   * Private constructor.
   *
   * @param options options, may be null.
   */
  private DQIfMDC(final String @Nullable [] options) {
    super("DQIfMDC", null);
    final Set<String> omits = new HashSet<>();
    if ((options != null) && (options.length > 0) && (options[0] != null)) {
      @NonNull
      String[] keys;
      if (options[0].indexOf(',') > 0)
        keys = options[0].split(",");
      else {
        final String opt = options[0];
        if (opt == null)
          throw new IllegalStateException();
        keys = new @NonNull String[] {opt};
      }
      final Set<String> keep = new HashSet<>();
      for (int i = 0; i < (keys.length - 1); i++) {
        keys[i] = keys[i].trim();
        if (keys[i].startsWith("!"))
          omits.add(keys[i].substring(1));
        else
          keep.add(keys[i]);
      }
      if (keep.isEmpty())
        mMDCPatternConverter = MdcPatternConverter.newInstance(sEMPTY_PATTERN);
      else
        mMDCPatternConverter = MdcPatternConverter.newInstance(new String[] {String.join(",", keep)});
      mValue = keys[keys.length - 1].replaceAll("\\\\s", " ");
    }
    else
      mMDCPatternConverter = MdcPatternConverter.newInstance(sEMPTY_PATTERN);
    mOmit = Collections.unmodifiableSet(omits);
  }

  /**
   * Obtains an instance of pattern converter.
   *
   * @param pOptions options, may be null.
   * @return instance of pattern converter.
   */
  public static DQIfMDC newInstance(final String @Nullable [] pOptions) {
    if ((pOptions == null) || (pOptions.length == 0))
      return INSTANCE;

    return new DQIfMDC(pOptions);
  }

  /**
   * @see org.apache.logging.log4j.core.pattern.LogEventPatternConverter#format(org.apache.logging.log4j.core.LogEvent,
   *      java.lang.StringBuilder)
   */
  @Override
  public void format(LogEvent pEvent, StringBuilder pToAppendTo) {
    final StringBuilder sb = new StringBuilder();
    final ReadOnlyStringMap contextData = pEvent.getContextData();
    if (contextData.isEmpty() == true)
      mMDCPatternConverter.format(pEvent, sb);
    else {
      final SortedArrayStringMap map = new SortedArrayStringMap(contextData);
      mOmit.forEach((k) -> map.remove(k));
      final MutableLogEvent updatedEvent = new MutableLogEvent();
      updatedEvent.initFrom(pEvent);
      updatedEvent.setContextData(map);
      mMDCPatternConverter.format(updatedEvent, sb);
    }
    final String result = sb.toString();
    if (result.isEmpty() == false)
      pToAppendTo.append(mValue);
  }
}

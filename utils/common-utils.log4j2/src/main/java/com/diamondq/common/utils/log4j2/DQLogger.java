package com.diamondq.common.utils.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Plugin(name = "DQLoggerPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "dqlogger" })
public class DQLogger extends LogEventPatternConverter {
  /**
   * Singleton.
   */
  private static final DQLogger INSTANCE = new DQLogger(null);

  private final int mParts;

  /**
   * Private constructor.
   *
   * @param options options, may be null.
   */
  private DQLogger(final @NotNull String @Nullable [] options) {
    super("DQLogger", null);
    if ((options == null) || (options.length == 0)) mParts = 3;
    else mParts = Integer.parseInt(options[0]);
  }

  /**
   * Obtains an instance of pattern converter.
   *
   * @param pOptions options, may be null.
   * @return instance of pattern converter.
   */
  public static DQLogger newInstance(final @NotNull String @Nullable [] pOptions) {
    if ((pOptions == null) || (pOptions.length == 0)) {
      return INSTANCE;
    }

    return new DQLogger(pOptions);
  }

  /**
   * @see org.apache.logging.log4j.core.pattern.LogEventPatternConverter#format(org.apache.logging.log4j.core.LogEvent,
   *   java.lang.StringBuilder)
   */
  @Override
  public void format(LogEvent pEvent, StringBuilder pToAppendTo) {
    String loggerName = pEvent.getLoggerName();
    String[] parts = loggerName.split("\\.");
    int fullStart;
    if (parts.length > mParts) {
      fullStart = parts.length - mParts;
      for (int i = 0; i < fullStart; i++)
        pToAppendTo.append(parts[i].charAt(0)).append('.');
    } else fullStart = 0;
    for (int i = fullStart; i < parts.length; i++) {
      pToAppendTo.append(parts[i]);
      if (i < (parts.length - 1)) pToAppendTo.append('.');
    }

  }
}

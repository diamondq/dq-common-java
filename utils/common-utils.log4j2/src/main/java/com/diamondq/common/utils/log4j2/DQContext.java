package com.diamondq.common.utils.log4j2;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.context.spi.ContextClass;

import java.util.List;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Plugin(name = "DQContextPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"DQContext"})
public class DQContext extends LogEventPatternConverter {
  /**
   * Singleton.
   */
  private static final DQContext INSTANCE       = new DQContext(null);

  protected boolean              mPrintContext  = false;

  protected boolean              mPrintRefCount = false;

  protected int                  mContextLength = -1;

  /**
   * Private constructor.
   *
   * @param options options, may be null.
   */
  private DQContext(final String @Nullable [] options) {
    super("DQContext", null);
    if ((options != null) && (options.length > 0) && (options[0] != null)) {
      @NonNull
      String @NonNull [] keys;
      if (options[0].indexOf(',') > 0)
        keys = options[0].split(",");
      else {
        final String opt = options[0];
        if (opt == null)
          throw new IllegalStateException();
        keys = new @NonNull String[] {opt};
      }
      if (keys.length >= 1) {
        final String key = keys[0];
        if (key.length() > 0) {
          mContextLength = Integer.parseInt(key);
          mPrintContext = true;
        }
      }
      if (keys.length >= 2) {
        final String key = keys[1];
        if (key.length() > 0)
          mPrintRefCount = Boolean.parseBoolean(key);
      }
    }
  }

  /**
   * Obtains an instance of pattern converter.
   *
   * @param pOptions options, may be null.
   * @return instance of pattern converter.
   */
  public static DQContext newInstance(final String @Nullable [] pOptions) {
    if ((pOptions == null) || (pOptions.length == 0))
      return INSTANCE;

    return new DQContext(pOptions);
  }

  /**
   * @see org.apache.logging.log4j.core.pattern.LogEventPatternConverter#format(org.apache.logging.log4j.core.LogEvent,
   *      java.lang.StringBuilder)
   */
  @Override
  public void format(LogEvent pEvent, StringBuilder pToAppendTo) {
    final ContextClass context = (ContextClass) ContextFactory.nullableCurrentContext();
    if (context == null)
      return;
    final List<String> stack = context.getContextStackNames(mPrintRefCount);
    int size = stack.size();
    if (mPrintContext == true) {
      final StringBuilder sb = new StringBuilder();
      final StringBuilder mid = new StringBuilder();
      for (int i = 0; i < size; i++)
        if (i == 0)
          sb.append(stack.get(i));
        else if (i == (size - 1)) {
          final String last = stack.get(i);
          if (mContextLength == -1)
            sb.append(mid);
          else {
            String midStr = mid.toString();
            if ((last.length() + sb.length() + midStr.length() + 1) > mContextLength) {
              final int midLen = mContextLength - last.length() - 1 - sb.length() - 4;
              midStr = ">..." + midStr.substring(midStr.length() - midLen);
            }
            sb.append(midStr);
          }
          sb.append(">");
          sb.append(last);
          pToAppendTo.append(sb.toString());
        }
        else {
          mid.append(">");
          mid.append(stack.get(i));
        }
    }
    else {
      final Boolean duringNormal = context.getHandlerData(ContextClass.sDURING_CONTEXT_CONTROL, false, Boolean.class);
      if ((duringNormal != null) && (duringNormal == true))
        size--;
      if (size > 0)
        for (int i = 0; i < size; i++)
          pToAppendTo.append("  ");
    }
  }
}
